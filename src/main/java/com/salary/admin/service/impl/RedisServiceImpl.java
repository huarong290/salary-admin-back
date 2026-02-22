package com.salary.admin.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.salary.admin.service.IRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis 通用业务实现类
 * <p>
 * 特点：
 *     1. 使用统一的 ObjectMapper 做 JSON 序列化/反序列化，保证跨服务兼容性。
 *     2. 所有非 String 类型的值都会序列化为 JSON 存储。
 *     3. 提供了 Key、String、Hash、List、Set、ZSet 的常用操作。
 * 设计思路：
 * 1. 序列化方案：统一使用 StringRedisTemplate，Key/Value 均为明文展示，方便运维排查。
 * 2. 对象处理：复杂 POJO 采用 Jackson 进行 JSON 序列化存储。
 * 3. 泛型支持：通过传入 Class<T> 自动完成反序列化转换。
 * 4. 容错性：内部捕获序列化异常并记录日志，防止因缓存故障拖垮主业务流程。
 * </p>
 *
 * @author Harvey
 * @since 2026-02-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements IRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // ============================ Key 通用操作 ============================
    /**
     * 判断单个 key 是否存在
     */
    @Override
    public Long exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key)) ? 1L : 0L;
    }
    /**
     * 判断多个 key 是否存在，返回存在的数量
     */
    @Override
    public Long exists(Collection<String> keys) {
        // 批量探测 Key 存在的数量，常用于大数据量预热检查
        return redisTemplate.countExistingKeys(keys);
    }
    /**
     * 删除单个 key
     */
    @Override
    public Long del(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key)) ? 1L : 0L;
    }
    /**
     * 批量删除多个 key
     */
    @Override
    public Long del(Collection<String> keys) {
        // 批量删除，建议 keys 数量控制在 1000 以内，防止阻塞 Redis 线程
        return redisTemplate.delete(keys);
    }
    /**
     * 设置 key 的过期时间
     */
    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
    /**
     * 移除 key 的过期时间，使其永久有效
     */
    @Override
    public Boolean persist(String key) {
        // 移除过期时间，将 Key 转为永久有效
        return redisTemplate.persist(key);
    }
    /**
     * 获取 key 的剩余过期时间
     */
    @Override
    public Long ttl(String key) {
        // 返回过期剩余时间，-1 代表永久，-2 代表 Key 不存在
        return redisTemplate.getExpire(key);
    }

    // ============================ String 字符串操作 ============================
    /**
     * 设置字符串值（序列化为 JSON 存储）
     */
    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, toJson(value));
    }
    /**
     * 设置字符串值并指定过期时间
     */
    @Override
    public void setEx(String key, Object value, long timeout, TimeUnit unit) {
        // 原子性操作：设置值的同时设置过期时间
        redisTemplate.opsForValue().set(key, toJson(value), timeout, unit);
    }
    /**
     * 获取字符串值并反序列化为指定类型
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        return fromJson(redisTemplate.opsForValue().get(key), clazz);
    }
    /**
     * 批量设置多个 key-value
     */
    @Override
    public void mSet(Map<String, Object> map) {
        // 批量插入，利用 Pipeline 思想减少网络 RTT 开销
        Map<String, String> jsonMap = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toJson(e.getValue())));
        redisTemplate.opsForValue().multiSet(jsonMap);
    }
    /**
     * 批量获取多个 key 的值
     */
    @Override
    public <T> List<T> mGet(Collection<String> keys, Class<T> clazz) {
        List<String> raw = redisTemplate.opsForValue().multiGet(keys);
        return raw == null ? Collections.emptyList() :
                raw.stream().map(s -> fromJson(s, clazz)).collect(Collectors.toList());
    }
    /**
     * 自增操作（默认 +1）
     */
    @Override
    public Long incr(String key) {
        // 原子自增 1，常用于限流或分布式序列号生成
        return redisTemplate.opsForValue().increment(key);
    }
    /**
     * 按指定步长自增
     */
    @Override
    public Long incrBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
    /**
     * 自减操作（默认 -1）
     */
    @Override
    public Long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    // ============================ Hash 哈希操作 ============================
    /**
     * 设置哈希表字段值
     */
    @Override
    public void hSet(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, toJson(value));
    }
    /**
     * 获取哈希表字段值
     */
    @Override
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        return fromJson((String) redisTemplate.opsForHash().get(key, hashKey), clazz);
    }
    /**
     * 判断哈希表字段是否存在
     */
    @Override
    public Boolean hExists(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }
    /**
     * 批量设置哈希表字段
     */
    @Override
    public void hMSet(String key, Map<String, Object> map) {
        Map<String, String> jsonMap = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> toJson(e.getValue())));
        redisTemplate.opsForHash().putAll(key, jsonMap);
    }
    /**
     * 批量获取哈希表字段值
     */
    @Override
    public <T> List<T> hMGet(String key, Collection<String> hashKeys, Class<T> clazz) {
        // 批量获取 Hash 字段，在大数据量对象部分字段读取时效率最高
        List<Object> raw = redisTemplate.opsForHash().multiGet(key, new ArrayList<>(hashKeys));
        return raw.stream().map(o -> fromJson((String) o, clazz)).collect(Collectors.toList());
    }
    /**
     * 获取哈希表所有字段和值
     */
    @Override
    public <T, R> Map<T, R> hGetAll(String key, Class<T> keyClazz, Class<R> valueClazz) {
        // 获取全部 Hash 内容，慎用，若 Hash 包含万级以上字段会造成阻塞
        Map<Object, Object> raw = redisTemplate.opsForHash().entries(key);
        Map<T, R> result = new HashMap<>();
        raw.forEach((k, v) -> result.put(fromJson((String) k, keyClazz), fromJson((String) v, valueClazz)));
        return result;
    }
    /**
     * 删除哈希表字段
     */
    @Override
    public Long hDel(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(key, hashKeys);
    }

    // ============================ List 列表操作 ============================
    /**
     * 从左侧插入元素
     */
    @Override
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, toJson(value));
    }
    /**
     * 从右侧插入元素
     */
    @Override
    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, toJson(value));
    }
    /**
     * 从右侧弹出元素
     */
    @Override
    public <T> T rPop(String key, Class<T> clazz) {
        // 常用作任务队列：右侧弹出，左侧压入 (FIFO)
        return fromJson(redisTemplate.opsForList().rightPop(key), clazz);
    }
    /**
     * 按索引获取元素
     */
    @Override
    public <T> T lIndex(String key, long index, Class<T> clazz) {
        return fromJson(redisTemplate.opsForList().index(key, index), clazz);
    }
    /**
     * 获取指定区间的元素
     */
    @Override
    public <T> List<T> lRange(String key, long start, long end, Class<T> clazz) {
        // 分页获取列表数据，start 和 end 均为包含关系
        List<String> raw = redisTemplate.opsForList().range(key, start, end);
        return raw == null ? Collections.emptyList() :
                raw.stream().map(s -> fromJson(s, clazz)).collect(Collectors.toList());
    }
    /**
     * 裁剪列表，只保留指定区间的元素
     */
    @Override
    public void lTrim(String key, long start, long end) {
        // 强制裁剪列表，仅保留指定区间。在大数据清洗或固定长度日志场景常用
        redisTemplate.opsForList().trim(key, start, end);
    }
    /**
     * 获取列表长度
     */
    @Override
    public Long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // ============================ Set 无序集合 ============================
    /**
     * 添加元素到集合
     */
    @Override
    public Long sAdd(String key, Object... values) {
        String[] jsonValues = Arrays.stream(values).map(this::toJson).toArray(String[]::new);
        return redisTemplate.opsForSet().add(key, jsonValues);
    }
    /**
     * 获取集合所有成员
     */
    @Override
    public <T> Set<T> sMembers(String key, Class<T> clazz) {
        Set<String> raw = redisTemplate.opsForSet().members(key);
        return raw == null ? Collections.emptySet() :
                raw.stream().map(s -> fromJson(s, clazz)).collect(Collectors.toSet());
    }
    /**
     * 判断元素是否在集合中
     */
    @Override
    public Boolean sIsMember(String key, Object value) {
        // $O(1)$ 时间复杂度判断是否存在，常用于去重过滤
        return redisTemplate.opsForSet().isMember(key, toJson(value));
    }
    /**
     * 删除集合中的元素
     */
    @Override
    public Long sRem(String key, Object... values) {
        Object[] jsonValues = Arrays.stream(values).map(this::toJson).toArray();
        return redisTemplate.opsForSet().remove(key, jsonValues);
    }
    /**
     * 获取集合大小
     */
    @Override
    public Long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    // ============================ ZSet ============================

    /**
     * 添加元素到有序集合
     * @param key Redis key
     * @param value 元素值（会序列化为 JSON）
     * @param score 分数（排序依据）
     * @return 是否添加成功
     */
    @Override
    public Boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, toJson(value), score);
    }

    /**
     * 获取指定区间的成员（升序）
     * @param key Redis key
     * @param start 起始下标
     * @param end 结束下标
     * @param clazz 反序列化类型
     * @return 成员集合（按 score 从小到大排序）
     */
    @Override
    public <T> Set<T> zRange(String key, long start, long end, Class<T> clazz) {
        Set<String> raw = redisTemplate.opsForZSet().range(key, start, end);
        return raw == null ? Collections.emptySet()
                : raw.stream().map(s -> fromJson(s, clazz)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 获取指定区间的成员（降序）
     * @param key Redis key
     * @param start 起始下标
     * @param end 结束下标
     * @param clazz 反序列化类型
     * @return 成员集合（按 score 从大到小排序）
     */
    @Override
    public <T> Set<T> zRevRange(String key, long start, long end, Class<T> clazz) {
        Set<String> raw = redisTemplate.opsForZSet().reverseRange(key, start, end);
        return raw == null ? Collections.emptySet()
                : raw.stream().map(s -> fromJson(s, clazz)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 获取指定元素的分数
     * @param key Redis key
     * @param value 元素值
     * @return 分数（Double），不存在返回 null
     */
    @Override
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, toJson(value));
    }

    /**
     * 删除有序集合中的元素
     * @param key Redis key
     * @param values 要删除的元素
     * @return 删除的数量
     */
    @Override
    public Long zRem(String key, Object... values) {
        Object[] jsonValues = Arrays.stream(values).map(this::toJson).toArray();
        return redisTemplate.opsForZSet().remove(key, jsonValues);
    }

    /**
     * 获取有序集合的大小
     * @param key Redis key
     * @return 集合元素数量
     */
    @Override
    public Long zCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    // ============================ Helpers ============================

    /**
     * 序列化对象为 JSON 字符串
     * - 如果是 String，直接返回
     * - 如果是 null，返回 null
     * - 其他对象使用 ObjectMapper 序列化
     */
    private String toJson(Object value) {
        if (value == null) return null;
        if (value instanceof String s) return s;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Redis 序列化失败", e);
            return null;
        }
    }

    /**
     * 反序列化 JSON 字符串为指定类型对象
     * - 如果 clazz 是 String，直接返回原始字符串
     * - 如果 json 为 null，返回 null
     * - 其他对象使用 ObjectMapper 反序列化
     */
    private <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || clazz == null) return null;
        if (clazz.equals(String.class)) return clazz.cast(json);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Redis 反序列化失败", e);
            return null;
        }
    }
}