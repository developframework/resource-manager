package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.utils.CacheType;
import com.github.developframework.resource.utils.ResourceAssert;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;

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

    private final Duration timeout;

    private final CacheType cacheType;

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

    public final void refreshCache(PO entity) {
        switch (cacheType) {
            case VALUE: {
                if (timeout == null) {
                    redisTemplate.opsForValue().set(cacheKey + ":" + entity.getId(), entity);
                } else {
                    redisTemplate.opsForValue().set(cacheKey + ":" + entity.getId(), entity, timeout);
                }
            }
            break;
            case HASH: {
                redisTemplate.opsForHash().put(cacheKey, String.valueOf(entity.getId()), entity);
            }
            break;
            default:
                throw new AssertionError();
        }
    }

    public final void deleteCache(PO entity) {
        redisTemplate.opsForHash().delete(cacheKey, String.valueOf(entity.getId()));
    }

    public final PO readCache(ID id) {
        switch (cacheType) {
            case VALUE: {
                return redisTemplate.opsForValue().get(cacheKey + ":" + id);
            }
            case HASH: {
                return redisTemplate.<String, PO>opsForHash().get(cacheKey, String.valueOf(id));
            }
            default:
                throw new AssertionError();
        }
    }

    @Override
    public Optional<PO> add(Object dto) {
        return add(dto)
                .map(entity -> {
                    this.refreshCache(entity);
                    return entity;
                });
    }

    @Override
    public Optional<PO> modifyById(ID id, Object dto) {
        return modifyById(id, dto)
                .map(entity -> {
                    this.refreshCache(entity);
                    return entity;
                });
    }

    @Override
    public boolean remove(PO entity) {
        boolean success = remove(entity);
        deleteCache(entity);
        return success;
    }

    @Override
    public Optional<PO> removeById(ID id) {
        return removeById(id)
                .map(entity -> {
                    deleteCache(entity);
                    return entity;
                });
    }

    @Override
    public Optional<PO> findOneById(ID id) {
        PO po = readCache(id);
        if (po != null) {
            return Optional.of(po);
        } else {
            return findOneById(id)
                    .map(entity -> {
                        refreshCache(entity);
                        return entity;
                    });
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public PO findOneByIdRequired(ID id) {
        PO po = readCache(id);
        if (po == null) {
            po = (PO) ResourceAssert
                    .resourceExistAssertBuilder(resourceDefinition.getResourceName(), resourceHandler.queryById(id))
                    .addParameter("id", id)
                    .returnValue();
            refreshCache(execSearchOperate(po));
        }
        return po;
    }
}
