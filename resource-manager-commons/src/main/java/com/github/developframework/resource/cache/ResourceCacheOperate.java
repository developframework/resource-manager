package com.github.developframework.resource.cache;

import com.github.developframework.resource.Entity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

/**
 * 资源缓存操作
 *
 * @author qiushui on 2020-05-01.
 */
public abstract class ResourceCacheOperate<ENTITY extends Entity<ID>, ID extends Serializable> {

    protected RedisTemplate<String, ENTITY> redisTemplate;

    protected String key;

    protected Duration timeout;

    public ResourceCacheOperate(RedisTemplate<String, ENTITY> redisTemplate, String key, Duration timeout) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.timeout = timeout;
    }

    /**
     * 添加缓存
     */
    public abstract void addCache(ENTITY entity);

    /**
     * 刷新缓存
     */
    public abstract void refreshCache(ENTITY entity);

    /**
     * 删除缓存
     */
    public abstract void deleteCache(ENTITY entity);

    /**
     * 根据ID查询缓存
     */
    public abstract Optional<ENTITY> readCache(ID id);

    /**
     * 拼接带条件的key
     *
     * @param condtions
     * @return
     */
    public final String keyWithCondition(Object... condtions) {
        return key + "::" + StringUtils.join(condtions, "-");
    }
}
