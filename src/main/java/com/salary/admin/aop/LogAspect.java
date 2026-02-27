package com.salary.admin.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salary.admin.annotation.Loggable;
import com.salary.admin.utils.UserContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final ObjectMapper objectMapper;

    // 建议注入异步服务，将日志落库逻辑解耦
    // private final ISysOperLogService operLogService;

    // 专用日志线程池，避免和业务线程池抢资源
    private static final ExecutorService LOG_EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                    r -> {
                        Thread t = new Thread(r);
                        t.setName("log-async-" + t.getId());
                        t.setDaemon(true);
                        return t;
                    });
    // 轻量级 TraceId 生成器，避免 UUID.randomUUID() 的 SecureRandom 锁竞争
    private static final AtomicLong COUNTER = new AtomicLong();

    private String generateTraceId() {
        return Long.toHexString(System.currentTimeMillis())
                + Long.toHexString(COUNTER.incrementAndGet())
                + Long.toHexString(ThreadLocalRandom.current().nextInt());
    }

    @Around("@annotation(loggable)")
    public Object logAround(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        // 1. 链路追踪：在日志中埋入 TraceId，方便 grep 整个请求链路
        String traceId = generateTraceId();
        MDC.put("trace_id", traceId);

        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            // 捕获上下文快照 final
            Map<String, String> contextMap = MDC.getCopyOfContextMap();

            // 确保日志的序列化和 IO 不影响主流程响应耗时
            //  捕获当前线程的上下文快照
             String usernameSnapshot = UserContextUtil.getUsername();
            if (usernameSnapshot == null) {
                Object[] args = joinPoint.getArgs();
                if (args != null) {
                    // 寻找包含用户名属性的 DTO 或者直接是 String 类型的 username 参数
                    // 这里我们利用反射或简单的类型判断
                    usernameSnapshot = Arrays.stream(args)
                            .filter(arg -> arg != null)
                            .map(arg -> {
                                // 方式A: 如果参数本身就是 UserLoginReqDTO (或者包含 getUsername 方法的对象)
                                try {
                                    return (String) arg.getClass().getMethod("getUsername").invoke(arg);
                                } catch (Exception e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse(null);
                }
            }
            final String finalUsername = usernameSnapshot;
            final Object finalResult = result;
            final Throwable finalException = exception;
            // 2. 异步处理：使用 JDK 21 的线程池或 CompletableFuture
            CompletableFuture.runAsync(() -> {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                try {

                    // 将捕获到的 username 显式传入处理方法，或者在这里临时设置异步线程的上下文
                    handleLog(joinPoint, loggable, finalResult, finalException, costTime, finalUsername,traceId);
                } finally {
                    // 必须清理，防止线程池污染
                    MDC.remove("trace_id");
                }
            }, LOG_EXECUTOR); // ✅ 显式传入专用线程池

            MDC.remove("trace_id");
        }
    }

    private void handleLog(ProceedingJoinPoint joinPoint, Loggable loggable, Object result, Throwable ex, long costTime, String username,String traceId) {
        try {
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            // 1. 构建结构化的 Log 对象，而非字符串拼接
            Map<String, Object> logMap = new LinkedHashMap<>();
            logMap.put("title", loggable.title());
            logMap.put("class", className);
            logMap.put("method", methodName);
            logMap.put("costTime", costTime + "ms");
            logMap.put("username", username != null ? username : "anonymous");
            logMap.put("traceId", traceId);
            // 3. 入参处理 (针对文件上传进行特殊过滤)先过滤，再脱敏，最后以对象形式放入 Map
            if (loggable.logRequest()) {
                Object[] args = filterArgs(joinPoint.getArgs());
                // 这里我们不再先转一次 JSON，而是直接处理对象
                logMap.put("params", maskSensitiveData(args));
            }

            // 4. 出参处理
            if (ex == null && loggable.logResponse() && result != null) {
                // 建议：对于 login 接口，返回的 Token 太长，可以只记录关键信息或截断
                logMap.put("response", result);
            }


            // 5. 结构化打印：便于 ELK 收集分析

            if (ex != null) {
                logMap.put("exception", ex.getMessage());
                log.error(objectMapper.writeValueAsString(logMap), ex);
            } else {
                // 生产环境通常单行输出，开发环境可以配置 ObjectMapper 开启美化
                log.info(objectMapper.writeValueAsString(logMap));
            }

        } catch (Exception e) {
            log.error("日志切面执行异常", e);
        }
    }

    /**
     * 核心过滤：大数据架构必须排除二进制流和容器对象
     */
    private Object[] filterArgs(Object[] args) {
        if (args == null) return new Object[0];
        return Arrays.stream(args)
                .filter(arg -> arg != null
                        // 过滤单文件上传
                        && !(arg instanceof MultipartFile)
                        // 过滤多文件上传 (数组形式)
                        && !(arg instanceof MultipartFile[])
                        // 过滤集合形式的文件上传
                        && !(arg instanceof Collection && ((Collection<?>) arg).stream().anyMatch(item -> item instanceof MultipartFile))
                        // 过滤 Servlet 容器原生对象，防止序列化失败
                        && !(arg instanceof HttpServletRequest)
                        && !(arg instanceof HttpServletResponse)
                        && !(arg instanceof BindingResult))
                .toArray();
    }

    /**
     * 简单脱敏逻辑：大数据架构必备
     */
    private Object maskSensitiveData(Object[] args) {
        try {
            // 将 args 转为 Map 或 List 进行深度遍历，替换 password 等字段
            // 这里为了简单，你可以使用 fastjson 或 jackson 的 Filter
            // 简单演示：如果是 Map 就替换 password
            String json = objectMapper.writeValueAsString(args);
            return objectMapper.readTree(json.replaceAll("(\"password\"\\s*:\\s*\")[^\"]+(\")", "$1******$2"));
        } catch (Exception e) {
            return args;
        }
    }
}
