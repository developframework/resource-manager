package com.github.developframework.resource.spring;

import com.github.developframework.resource.*;
import develop.toolkit.base.utils.ArrayAdvice;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 *
 * spring-data资源操作器
 * @author qiushui on 2019-08-15.
 */
public abstract class SpringDataResourceHandler<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID>
        > implements ResourceHandler<ENTITY, ID> {

    protected final REPOSITORY repository;

    protected final ResourceDefinition<ENTITY> resourceDefinition;

    public SpringDataResourceHandler(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition) {
        this.repository = repository;
        this.resourceDefinition = resourceDefinition;
    }

    @Override
    public String primaryKeyFieldName() {
        return ArrayAdvice
                .getFirstTrue(resourceDefinition.getEntityClass().getDeclaredFields(), f -> f.isAnnotationPresent(Id.class))
                .map(Field::getName)
                .orElse("id");
    }

    @Override
    public String ownerFieldName() {
        return ArrayAdvice
                .getFirstTrue(resourceDefinition.getEntityClass().getDeclaredFields(), f -> f.isAnnotationPresent(Owner.class))
                .map(Field::getName)
                .orElse(null);
    }

    @Override
    public final boolean existsById(ID id, Object ownerId) {
        if (ownerId == null) {
            return repository.existsById(id);
        } else {
            return existsByIdAndOwnerId(id, ownerId);
        }
    }

    @Override
    public void insert(ENTITY entity) {
        repository.save(entity);
    }

    @Override
    public void insertAll(Collection<ENTITY> entities) {
        repository.saveAll(entities);
    }

    @Override
    public boolean update(ENTITY entity) {
        repository.save(entity);
        return true;
    }

    @Override
    public final void deleteById(ID id, Object ownerId) {
        if (ownerId == null) {
            repository.deleteById(id);
        } else {
            deleteByIdAndOwnerId(id, ownerId);
        }
    }

    @Override
    public void delete(ENTITY entity) {
        repository.delete(entity);
    }

    @Override
    public final Optional<ENTITY> queryById(ID id, Object ownerId) {
        if (ownerId == null) {
            return repository.findById(id);
        } else {
            return queryByIdAndOwnerId(id, ownerId);
        }
    }

    protected abstract boolean existsByIdAndOwnerId(ID id, Object ownerId);

    protected abstract void deleteByIdAndOwnerId(ID id, Object ownerId);

    protected abstract Optional<ENTITY> queryByIdAndOwnerId(ID id, Object ownerId);

    public abstract <SEARCH extends Search<ENTITY>> Page<ENTITY> queryPager(Pageable pageable, SEARCH search);

    public abstract <SEARCH extends Search<ENTITY>> List<ENTITY> query(Sort sort, SEARCH search);
}
