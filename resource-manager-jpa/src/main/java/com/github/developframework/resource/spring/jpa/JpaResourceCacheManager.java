package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.cache.*;
import com.github.developframework.resource.utils.ResourceAssert;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Duration;
import java.util.Optional;

/**
 * @author qiushui on 2020-04-30.
 */
@Getter
public abstract class JpaResourceCacheManager<
        PO extends com.github.developframework.resource.spring.jpa.PO<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<PO, ID> & JpaSpecificationExecutor<PO>
        > extends JpaResourceManager<PO, ID, REPOSITORY> {

    @Resource
    protected RedisTemplate<String, PO> redisTemplate;

    protected final String cacheKey;

    protected final Duration timeout;

    protected final CacheType cacheType;

    protected ResourceCacheOperate<PO, ID> cacheOperate;

    public JpaResourceCacheManager(
            REPOSITORY repository,
            Class<PO> entityClass,
            String resourceName,
            String cacheKey,
            Duration timeout,
            CacheType cacheType
    ) {
        super(repository, entityClass, resourceName);
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
    public Optional<PO> add(Object dto) {
        return super.add(dto)
                .map(entity -> {
                    if (cacheAble(entity)) {
                        cacheOperate.addCache(entity);
                    }
                    return entity;
                });
    }

    @Override
    public Optional<PO> modifyById(ID id, Object dto) {
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
    public boolean remove(PO entity) {
        boolean success = super.remove(entity);
        cacheOperate.deleteCache(entity);
        return success;
    }

    @Override
    public Optional<PO> removeById(ID id) {
        return super.removeById(id)
                .map(entity -> {
                    cacheOperate.deleteCache(entity);
                    return entity;
                });
    }

    @Override
    public Optional<PO> findOneById(ID id) {
        Optional<PO> optional = cacheOperate.readCache(id);
        if (optional.isPresent()) {
            return optional;
        } else {
            return findOneById(id)
                    .map(entity -> {
                        cacheOperate.addCache(entity);
                        return entity;
                    });
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public PO findOneByIdRequired(ID id) {
        return cacheOperate
                .readCache(id)
                .orElseGet(() -> {
                    PO po = (PO) ResourceAssert
                            .resourceExistAssertBuilder(resourceDefinition.getResourceName(), resourceHandler.queryById(id))
                            .addParameter("id", id)
                            .returnValue();
                    cacheOperate.addCache(execSearchOperate(po));
                    return po;
                });
    }

    /**
     * 判断是否需要缓存
     *
     * @param entity
     * @return
     */
    protected boolean cacheAble(PO entity) {
        return true;
    }
}
