package com.github.developframework.resource.spring;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.spring.cache.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

/**
 * spring-data资源缓存管理器
 *
 * @author qiushui on 2020-05-01.
 */
public abstract class SpringDataResourceCacheManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID>
        > extends SpringDataResourceManager<ENTITY, ID, REPOSITORY> {

    @Resource
    protected RedisTemplate<String, ENTITY> redisTemplate;

    protected final String cacheKey;

    protected final Duration timeout;

    protected final CacheType cacheType;

    protected ResourceCacheOperate<ENTITY, ID> cacheOperate;

    public SpringDataResourceCacheManager(
            REPOSITORY repository,
            ResourceDefinition<ENTITY> resourceDefinition,
            String cacheKey,
            Duration timeout,
            CacheType cacheType
    ) {
        super(repository, resourceDefinition);
        this.cacheKey = cacheKey;
        this.timeout = timeout;
        this.cacheType = cacheType;
    }

    @PostConstruct
    public void initCacheOperate() {
        switch (cacheType) {
            case VALUE:
                cacheOperate = new ValueResourceCacheOperate<>(redisTemplate, cacheKey, timeout);
                break;
            case HASH:
                cacheOperate = new HashResourceCacheOperate<>(redisTemplate, cacheKey, timeout);
                break;
            case LIST:
                cacheOperate = new ListResourceCacheOperate<>(redisTemplate, cacheKey, timeout);
                break;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public Optional<ENTITY> add(Object dto) {
        return super.add(dto)
                .map(entity -> {
                    if (cacheAble(entity)) {
                        cacheOperate.addCache(entity);
                    }
                    return entity;
                });
    }

    @Override
    public Optional<ENTITY> modifyById(ID id, Object dto) {
        return super.modifyById(id, dto)
                .map(entity -> {
                    if (cacheAble(entity)) {
                        cacheOperate.refreshCache(entity);
                    } else {
                        cacheOperate.deleteCache(entity);
                    }
                    return entity;
                });
    }

    @Override
    public boolean remove(ENTITY entity) {
        boolean success = super.remove(entity);
        cacheOperate.deleteCache(entity);
        return success;
    }

    @Override
    public Optional<ENTITY> removeById(ID id) {
        return super.removeById(id)
                .map(entity -> {
                    cacheOperate.deleteCache(entity);
                    return entity;
                });
    }

    @Override
    public Optional<ENTITY> findOneById(ID id) {
        Optional<ENTITY> optional = cacheOperate.readCache(id);
        if (optional.isPresent()) {
            return optional;
        } else {
            return super.findOneById(id)
                    .map(entity -> {
                        if (cacheAble(entity)) {
                            cacheOperate.addCache(entity);
                        }
                        return entity;
                    });
        }
    }

    @Override
    public ENTITY findOneByIdRequired(ID id) {
        return cacheOperate
                .readCache(id)
                .orElseGet(() -> {
                    ENTITY entity = super.findOneByIdRequired(id);
                    if (cacheAble(entity)) {
                        cacheOperate.addCache(entity);
                    }
                    return entity;
                });
    }

    /**
     * 判断是否需要缓存
     *
     * @param entity
     * @return
     */
    protected boolean cacheAble(ENTITY entity) {
        return true;
    }
}
