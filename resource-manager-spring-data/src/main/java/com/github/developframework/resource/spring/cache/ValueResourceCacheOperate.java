package com.github.developframework.resource.spring.cache;

import com.github.developframework.resource.Entity;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

/**
 * STRING类型资源缓存操作
 *
 * @author qiushui on 2020-05-01.
 */
public class ValueResourceCacheOperate<ENTITY extends Entity<ID>, ID extends Serializable> extends ResourceCacheOperate<ENTITY, ID> {


    public ValueResourceCacheOperate(RedisTemplate<String, ENTITY> redisTemplate, String key, Duration timeout) {
        super(redisTemplate, key, timeout);
    }

    @Override
    public void addCache(ENTITY entity) {
        refreshCache(entity);
    }

    @Override
    public void refreshCache(ENTITY entity) {
        redisTemplate.opsForValue().set(keyWithCondition(entity.getId()), entity, timeout);
    }

    @Override
    public void deleteCache(ENTITY entity) {
        redisTemplate.delete(keyWithCondition(entity.getId()));
    }

    @Override
    public Optional<ENTITY> readCache(ID id) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get(keyWithCondition(id))
        );
    }
}
