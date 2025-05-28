package com.github.developframework.resource.spring;

import com.github.developframework.resource.*;
import develop.toolkit.base.struct.TwoValues;
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
                .getFirstTrue(resourceDefinition.getEntityClass().getDeclaredFields(), f -> f.isAnnotationPresent(Id.class) || f.isAnnotationPresent(javax.persistence.Id.class))
                .map(Field::getName)
                .orElse("id");
    }

    @Override
    public TwoValues<String, ? extends Class<?>> ownerField(String ownerType) {
        return ArrayAdvice
                .getFirstTrue(resourceDefinition.getEntityClass().getDeclaredFields(), f -> {
                    final Owner owner = f.getAnnotation(Owner.class);
                    return owner != null && (owner.value().isEmpty() || owner.value().equals(ownerType));
                })
                .map(f -> TwoValues.of(f.getName(), f.getType()))
                .orElse(null);
    }

    @Override
    public final boolean existsById(ID id, OwnerInfo ownerInfo) {
        if (ownerInfo == null) {
            return repository.existsById(id);
        } else {
            return existsByIdAndOwnerId(id, ownerInfo);
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
    public final void deleteById(ID id, OwnerInfo ownerInfo) {
        if (ownerInfo == null) {
            repository.deleteById(id);
        } else {
            deleteByIdAndOwnerId(id, ownerInfo);
        }
    }

    @Override
    public void delete(ENTITY entity) {
        repository.delete(entity);
    }

    @Override
    public final Optional<ENTITY> queryById(ID id, OwnerInfo ownerInfo) {
        if (ownerInfo == null) {
            return repository.findById(id);
        } else {
            return queryByIdAndOwnerId(id, ownerInfo);
        }
    }

    protected abstract boolean existsByIdAndOwnerId(ID id, OwnerInfo ownerInfo);

    protected abstract void deleteByIdAndOwnerId(ID id, OwnerInfo ownerInfo);

    protected abstract Optional<ENTITY> queryByIdAndOwnerId(ID id, OwnerInfo ownerInfo);

    public abstract <SEARCH extends Search<ENTITY>> Page<ENTITY> queryPager(Pageable pageable, SEARCH search);

    public abstract <SEARCH extends Search<ENTITY>> List<ENTITY> query(Sort sort, SEARCH search);
}
