package com.salary.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.api.StatefulConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;

/**
 * Redis 配置类
 * 特点：
 * 1. 支持三种模式自动适配：Standalone（单机）、Sentinel（哨兵）、Cluster（集群）
 * 2. 具备Fallback 降级机制提供 Fallback 开关：在配置缺失时是否允许自动降级到单机模式可由 spring.data.redis.allow-fallback 开关控制
 * 3. 全局 ObjectMapper Bean：保证序列化行为一致
 * 4. 提供多种 RedisTemplate：Object、Long、String
 * 5. 启动时打印详细的启动摘要日志与节点拓扑监控运维摘要，方便排查问题
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig implements InitializingBean {

    private final RedisProperties redisProperties;

    /**
     * 是否允许在 Sentinel/Cluster 配置缺失时降级到 Standalone
     * 默认 false：生产环境建议强制报错，避免误用单机模式。生产环境 (PROD) 务必设为 false，以确保架构确定性
     * 可通过配置 spring.data.redis.allow-fallback=true 来开启
     */
    @Value("${spring.data.redis.allow-fallback:false}")
    private boolean allowFallback;

    /**
     * 全局统一的 Jackson 序列化映射器Bean ObjectMapper Bean
     * 用于 Redis 序列化，支持 Java8 时间类型，禁用时间戳格式
     */
    @Bean(name = "redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册 JSR310 时间模块，支持 Java8 LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        // 禁用将日期序列化为时间戳，改用标准 ISO-8601 格式
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 注意：此处不开启全局 activateDefaultTyping 以规避反序列化漏洞，建议在 DTO 上按需配置 @JsonTypeInfo
        return mapper;
    }

    /**
     * 启动时打印 Redis 运维摘要
     * 包含 Fallback 状态、连接池配置等信息
     */
    @Override
    public void afterPropertiesSet() {
        log.info("============== Redis 运维摘要 ==============");
        log.info("降级机制Fallback机制: {} | 状态: {}",
                allowFallback ? "已开启" : "已关闭",
                allowFallback ? "⚠️ 允许自动降级" : "✅ 严格校验模式");

        if (allowFallback) {
            log.warn("[SECURITY WARN] 当前项目开启了Redis Fallback机制.生产环境下建议关闭 Fallback (allow-fallback: false)，防止非预期的单机运行。");
        }

        Optional.ofNullable(redisProperties.getLettuce()).map(RedisProperties.Lettuce::getPool).ifPresent(p -> {
            log.info("连接池快照: [MaxActive: {}, MaxIdle: {}, MinIdle: {}, MaxWait: {}]",
                    p.getMaxActive(), p.getMaxIdle(),p.getMinIdle(), p.getMaxWait());
        });
        log.info("===========================================");
    }
    /**
     * 获取连接池配置
     * 明确泛型为 GenericObjectPoolConfig<?> 以适配 Lettuce 内部的捕获逻辑
     */
    private GenericObjectPoolConfig<StatefulConnection<?, ?>> getPoolConfig() {
        // 明确声明泛型类型
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();

        Optional.ofNullable(redisProperties.getLettuce())
                .map(RedisProperties.Lettuce::getPool)
                .ifPresent(p -> {
                    poolConfig.setMaxTotal(p.getMaxActive());
                    poolConfig.setMaxIdle(p.getMaxIdle());
                    poolConfig.setMinIdle(p.getMinIdle());
                    // 优雅处理超时时间
                    Duration maxWait = p.getMaxWait() != null ? p.getMaxWait() : Duration.ofSeconds(2);
                    poolConfig.setMaxWait(maxWait);
                });
        return poolConfig;
    }
    /**
     * 构建 LettuceConnectionFactory 的统一方法
     * 包含连接池配置、命令超时、优雅停机超时等参数
     *
     * @param config Redis模式配置（Standalone/Sentinel/Cluster）
     * @param mode 当前模式描述
     * @param nodeDetails 节点详情（IP:Port 列表）
     * @return LettuceConnectionFactory
     */
    private LettuceConnectionFactory createFactory(RedisConfiguration config, String mode, String nodeDetails) {
        log.info("正在初始化 Redis 连接工厂 | 模式: {} | 详细节点详情: [{}]", mode, nodeDetails);
        // 1. 获取配置并明确指定泛型或使用原始类型以规避编译器捕获错误
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = getPoolConfig();

        // 2.Lettuce 客户端配置
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Optional.ofNullable(redisProperties.getTimeout()).orElse(Duration.ofSeconds(2)))
                .shutdownTimeout(Duration.ofMillis(500)) // 优雅停机超时
                .poolConfig(poolConfig)
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    // ==================== 多模式 Bean 定义 ====================

    /**
     * Sentinel 模式连接工厂
     * 当配置 spring.data.redis.sentinel.master 时生效
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.redis.sentinel", name = "master")
    public RedisConnectionFactory sentinelConnectionFactory() {
        RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
        if (sentinel == null || CollectionUtils.isEmpty(sentinel.getNodes())) {
            if (allowFallback) {
                log.warn("⚠️ Sentinel 节点配置缺失，尝试降级至 Standalone...");
                return standaloneConnectionFactory();
            }
            throw new IllegalArgumentException("Redis Sentinel nodes are required!");
        }

        RedisSentinelConfiguration config = new RedisSentinelConfiguration(
                sentinel.getMaster(), new HashSet<>(sentinel.getNodes()));
        Optional.ofNullable(redisProperties.getPassword()).ifPresent(config::setPassword);

        return createFactory(config, "SENTINEL", String.join(", ", sentinel.getNodes()));
    }

    /**
     * Cluster 模式连接工厂
     * 当配置 spring.data.redis.cluster.nodes[0] 时生效
     */
    @Bean
    @ConditionalOnProperty(prefix = "spring.data.redis.cluster", name = "nodes[0]")
    public RedisConnectionFactory clusterConnectionFactory() {
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        if (cluster == null || CollectionUtils.isEmpty(cluster.getNodes())) {
            if (allowFallback) {
                log.warn("⚠️ Cluster 节点配置缺失，尝试降级至 Standalone...");
                return standaloneConnectionFactory();
            }
            throw new IllegalArgumentException("Redis Cluster nodes are required!");
        }

        RedisClusterConfiguration config = new RedisClusterConfiguration(cluster.getNodes());
        Optional.ofNullable(redisProperties.getPassword()).ifPresent(config::setPassword);

        return createFactory(config, "CLUSTER", String.join(", ", cluster.getNodes()));
    }

    /**
     * Standalone 模式连接工厂
     * 默认模式，作为所有场景的兜底方案
     */
    @Bean
    @Primary
    // 只有当配置中显式存在 host 且不存在 sentinel 时，才初始化单机工厂
    @ConditionalOnProperty(prefix = "spring.data.redis", name = "host")
    @ConditionalOnMissingBean(RedisSentinelConfiguration.class)
    public RedisConnectionFactory standaloneConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(
                redisProperties.getHost(), redisProperties.getPort());
        config.setDatabase(redisProperties.getDatabase());
        Optional.ofNullable(redisProperties.getPassword()).ifPresent(config::setPassword);

        return createFactory(config, "STANDALONE", redisProperties.getHost() + ":" + redisProperties.getPort());
    }

    // ==================== RedisTemplate 定义 ====================

    /**
     * 通用 RedisTemplate
     * Key 使用 String 序列化，Value 使用 JSON 序列化
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, ObjectMapper redisObjectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper));
        return template;
    }

    /**
     * Long 类型专用 RedisTemplate
     * 避免数字转成 JSON 字符串的冗余
     */
    @Bean
    public RedisTemplate<String, Long> longRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        return template;
    }

    /**
     * String 类型专用 RedisTemplate
     * 常用于简单的 KV 存储
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
