package com.github.developframework.resource.spring.cache;

import com.github.developframework.resource.Entity;
import develop.toolkit.base.struct.KeyValuePair;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

/**
 * LIST类型资源缓存操作
 *
 * @author qiushui on 2020-05-01.
 */
public class ListResourceCacheOperate<ENTITY extends Entity<ID>, ID extends Serializable> extends ResourceCacheOperate<ENTITY, ID> {

    public ListResourceCacheOperate(RedisTemplate<String, ENTITY> redisTemplate, String key, Duration timeout) {
        super(redisTemplate, key, timeout);
    }

    @Override
    public void addCache(ENTITY entity) {
        redisTemplate.opsForList().rightPush(key, entity);
    }

    @Override
    public void refreshCache(ENTITY entity) {
        if (RedisCacheHelper.hasKey(redisTemplate, key)) {
            KeyValuePair<Long, ENTITY> kv = RedisCacheHelper.listFind(redisTemplate, key, p -> p.getId().equals(entity.getId()));
            if (kv.getKey() >= 0) {
                redisTemplate.opsForList().set(key, kv.getKey(), entity);
                return;
            }
        }
        addCache(entity);
    }

    @Override
    public void deleteCache(ENTITY entity) {
        readCache(entity.getId()).ifPresent(e -> redisTemplate.opsForList().remove(key, 1, e));
    }

    @Override
    public Optional<ENTITY> readCache(ID id) {
        return Optional.ofNullable(
                RedisCacheHelper.listFind(redisTemplate, key, p -> p.getId().equals(id)).getValue()
        );
    }
}
