package com.salary.admin.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 客户端 IP 工具类（优化版）
 *
 * <p>
 * 优化点：
 * 1. 改为静态方法，无需注入，使用更方便
 * 2. 移除 InetAddress.getByName 避免潜在的 DNS 阻塞风险
 * 3. 常量提取，减少对象创建
 * 4. 更加严格的空值判断
 * </p>
 */
@Slf4j
public class IpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String SEPARATOR = ",";

    // 优先级顺序：越靠前越可信（或越常用）
    private static final String[] HEADERS = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    /**
     * 私有构造，禁止实例化
     */
    private IpUtils() {}

    /**
     * 获取客户端真实 IP
     *
     * @param request HttpServletRequest
     * @return 真实 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return UNKNOWN;
        }

        String ip = null;

        // 1. 尝试从各种代理 Header 中获取
        for (String header : HEADERS) {
            ip = request.getHeader(header);
            if (!isUnknown(ip)) {
                // 只有 X-Forwarded-For 可能包含多个 IP
                if (ip.contains(SEPARATOR)) {
                    // 多次反向代理后会有多个ip值，第一个ip才是真实ip
                    String[] ips = ip.split(SEPARATOR);
                    for (String subIp : ips) {
                        if (!isUnknown(subIp)) {
                            ip = subIp.trim();
                            break;
                        }
                    }
                }
                break; // 找到了有效 IP，跳出循环
            }
        }

        // 2. 如果 Header 都没有，则取 RemoteAddr
        if (isUnknown(ip)) {
            ip = request.getRemoteAddr();
            // 如果是本机访问，尝试获取本机局域网 IP (可选逻辑)
            if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ip = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    log.warn("获取本机 IP 失败", e);
                }
            }
        }

        // 3. 最终格式化 (IPv6 处理)
        return normalizeIp(ip);
    }

    /**
     * 判断 IP 是否无效 (为空 或 unknown)
     */
    private static boolean isUnknown(String ip) {
        return !StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip.trim());
    }

    /**
     * IP 归一化处理
     * 去除 IPv6 的 ::ffff: 前缀，统一返回 IPv4 格式
     */
    private static String normalizeIp(String ip) {
        if (!StringUtils.hasText(ip)) {
            return UNKNOWN;
        }

        // 处理多 IP 情况，取第一个有效 IP
        if (ip.contains(SEPARATOR)) {
            for (String subIp : ip.split(SEPARATOR)) {
                if (StringUtils.hasText(subIp) && !UNKNOWN.equalsIgnoreCase(subIp.trim())) {
                    ip = subIp.trim();
                    break;
                }
            }
        }

        ip = ip.trim();

        // 转换 IPv6 localhost
        if (LOCALHOST_IPV6.equals(ip)) {
            return LOCALHOST_IPV4;
        }

        // 转换 ::ffff:192.168.x.x 格式
        if (ip.startsWith("::ffff:")) {
            return ip.substring(7);
        }
        return ip;
    }
}
