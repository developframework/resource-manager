package com.github.developframework.resource.spring.cache;

import develop.toolkit.base.struct.KeyValuePair;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * redis缓存助手
 *
 * @author qiushui on 2020-04-30.
 */
public final class RedisCacheHelper {

    /**
     * 判断是否有键
     *
     * @param redisTemplate
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> boolean hasKey(RedisTemplate<K, V> redisTemplate, K key) {
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

    /**
     * 获取列表长度
     *
     * @param redisTemplate
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> long listSize(RedisTemplate<K, V> redisTemplate, K key) {
        Long size = redisTemplate.opsForList().size(key);
        return size != null ? size : 0;
    }

    /**
     * 列表是否有值
     *
     * @param redisTemplate
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> boolean listNotEmpty(RedisTemplate<K, V> redisTemplate, K key) {
        Long size = redisTemplate.opsForList().size(key);
        return size != null && size > 0;
    }

    /**
     * 列表是否为空
     *
     * @param redisTemplate
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> boolean listEmpty(RedisTemplate<K, V> redisTemplate, K key) {
        Long size = redisTemplate.opsForList().size(key);
        return size == null || size == 0;
    }

    /**
     * 列表查询匹配的
     *
     * @param redisTemplate
     * @param key
     * @param predicate
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> KeyValuePair<Long, V> listFind(RedisTemplate<K, V> redisTemplate, K key, Predicate<V> predicate) {
        ListOperations<K, V> listOperations = redisTemplate.opsForList();
        long size = listSize(redisTemplate, key);
        for (long i = 0; i < size; i++) {
            V v = listOperations.index(key, i);
            if (v != null && predicate.test(v)) {
                return KeyValuePair.of(i, v);
            }
        }
        return KeyValuePair.of(-1L, null);
    }

    /**
     * 读取值
     *
     * @param redisTemplate
     * @param key
     * @param function
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> V readValue(RedisTemplate<K, V> redisTemplate, K key, Function<K, V> function, Duration timeout) {
        ValueOperations<K, V> valueOperations = redisTemplate.opsForValue();
        V v = valueOperations.get(key);
        if (v == null) {
            v = function.apply(key);
            if (v != null) {
                valueOperations.set(key, v, timeout);
            }
        }
        return v;
    }

    /**
     * 读取Hash值
     *
     * @param redisTemplate
     * @param key
     * @param hashKey
     * @param function
     * @param <H>
     * @param <HK>
     * @param <HV>
     * @return
     */
    public static <H, HK, HV> HV readHash(RedisTemplate<H, HV> redisTemplate, H key, HK hashKey, Function<HK, HV> function) {
        HashOperations<H, HK, HV> hashOperations = redisTemplate.opsForHash();
        HV v = hashOperations.get(key, hashKey);
        if (v == null) {
            v = function.apply(hashKey);
            if (v != null) {
                hashOperations.put(key, hashKey, v);
            }
        }
        return v;
    }
}
