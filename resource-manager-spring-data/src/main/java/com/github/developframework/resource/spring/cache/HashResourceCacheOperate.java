package com.github.developframework.resource.spring.cache;

import com.github.developframework.resource.Entity;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

/**
 * @author qiushui on 2020-05-01.
 */
public class HashResourceCacheOperate<ENTITY extends Entity<ID>, ID extends Serializable> extends ResourceCacheOperate<ENTITY, ID> {

    public HashResourceCacheOperate(RedisTemplate<String, ENTITY> redisTemplate, String key, Duration timeout) {
        super(redisTemplate, key, timeout);
    }

    @Override
    public void addCache(ENTITY entity) {
        refreshCache(entity);
    }

    @Override
    public void refreshCache(ENTITY entity) {
        redisTemplate.opsForHash().put(key, String.valueOf(entity.getId()), entity);
    }

    @Override
    public void deleteCache(ENTITY entity) {
        redisTemplate.opsForHash().delete(key, String.valueOf(entity.getId()));
    }

    @Override
    public Optional<ENTITY> readCache(ID id) {
        return Optional.ofNullable(
                redisTemplate.<String, ENTITY>opsForHash().get(key, String.valueOf(id))
        );
    }
}
