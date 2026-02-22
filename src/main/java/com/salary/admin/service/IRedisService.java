package com.salary.admin.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用操作接口
 *
 * 特点：
 * 1. 覆盖 Redis 的五大核心数据结构：String、Hash、List、Set、ZSet
 * 2. 提供常用的 Key 管理方法（过期、删除、TTL 等）
 * 3. 支持泛型序列化/反序列化，避免强制字符串化
 * 4. 设计贴近 Redis 原生命令，降低学习成本
 */
public interface IRedisService {

    // ============================ Key (Generic) ============================

    /**
     * 判断多个 key 是否存在
     * @param keys key 集合
     * @return 存在的数量
     */
    Long exists(Collection<String> keys);

    /**
     * 判断单个 key 是否存在
     * @param key Redis key
     * @return 存在返回 1，不存在返回 0
     */
    Long exists(String key);

    /**
     * 删除单个 key
     * @param key Redis key
     * @return 删除成功数量 (0 或 1)
     */
    Long del(String key);

    /**
     * 批量删除多个 key
     * @param keys key 集合
     * @return 删除成功的数量
     */
    Long del(Collection<String> keys);

    /**
     * 设置 key 的过期时间
     * @param key Redis key
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    Boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 移除 key 的过期时间，使其永久有效
     * @param key Redis key
     * @return 是否成功
     */
    Boolean persist(String key);

    /**
     * 获取 key 的剩余过期时间
     * @param key Redis key
     * @return 剩余时间（秒），-1 表示永久有效，-2 表示不存在
     */
    Long ttl(String key);

    // ============================ String ============================

    /**
     * 设置字符串值
     */
    void set(String key, Object value);

    /**
     * 设置字符串值并指定过期时间
     */
    void setEx(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 获取字符串值并反序列化为指定类型
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 批量设置多个 key-value
     */
    void mSet(Map<String, Object> map);

    /**
     * 批量获取多个 key 的值
     */
    <T> List<T> mGet(Collection<String> keys, Class<T> clazz);

    /**
     * 自增操作（默认 +1）
     */
    Long incr(String key);

    /**
     * 按指定步长自增
     */
    Long incrBy(String key, long delta);

    /**
     * 自减操作（默认 -1）
     */
    Long decr(String key);

    // ============================ Hash ============================

    /**
     * 设置哈希表字段值
     */
    void hSet(String key, String hashKey, Object value);

    /**
     * 获取哈希表字段值
     */
    <T> T hGet(String key, String hashKey, Class<T> clazz);

    /**
     * 判断哈希表字段是否存在
     */
    Boolean hExists(String key, String hashKey);

    /**
     * 批量设置哈希表字段
     */
    void hMSet(String key, Map<String, Object> map);

    /**
     * 批量获取哈希表字段值
     */
    <T> List<T> hMGet(String key, Collection<String> hashKeys, Class<T> clazz);

    /**
     * 获取哈希表所有字段和值
     */
    <T, R> Map<T, R> hGetAll(String key, Class<T> keyClazz, Class<R> valueClazz);

    /**
     * 删除哈希表字段
     */
    Long hDel(String key, Object... hashKeys);

    // ============================ List ============================

    /**
     * 从左侧插入元素
     */
    Long lPush(String key, Object value);

    /**
     * 从右侧插入元素
     */
    Long rPush(String key, Object value);

    /**
     * 从右侧弹出元素
     */
    <T> T rPop(String key, Class<T> clazz);

    /**
     * 按索引获取元素
     */
    <T> T lIndex(String key, long index, Class<T> clazz);

    /**
     * 获取指定区间的元素
     */
    <T> List<T> lRange(String key, long start, long end, Class<T> clazz);

    /**
     * 裁剪列表，只保留指定区间的元素
     */
    void lTrim(String key, long start, long end);

    /**
     * 获取列表长度
     */
    Long lLen(String key);

    // ============================ Set ============================

    /**
     * 添加元素到集合
     */
    Long sAdd(String key, Object... values);

    /**
     * 获取集合所有成员
     */
    <T> Set<T> sMembers(String key, Class<T> clazz);

    /**
     * 判断元素是否在集合中
     */
    Boolean sIsMember(String key, Object value);

    /**
     * 删除集合中的元素
     */
    Long sRem(String key, Object... values);

    /**
     * 获取集合大小
     */
    Long sCard(String key);

    // ============================ ZSet (Sorted Set) ============================

    /**
     * 添加元素到有序集合
     */
    Boolean zAdd(String key, Object value, double score);

    /**
     * 获取指定区间的成员（升序）
     */
    <T> Set<T> zRange(String key, long start, long end, Class<T> clazz);

    /**
     * 获取指定区间的成员（降序）
     */
    <T> Set<T> zRevRange(String key, long start, long end, Class<T> clazz);

    /**
     * 获取元素的分数
     */
    Double zScore(String key, Object value);

    /**
     * 删除有序集合中的元素
     */
    Long zRem(String key, Object... values);

    /**
     * 获取有序集合大小
     */
    Long zCard(String key);
}
