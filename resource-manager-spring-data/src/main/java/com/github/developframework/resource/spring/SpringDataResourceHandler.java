package com.github.developframework.resource.spring;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceHandler;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

/**
 * @author qiushui on 2019-08-15.
 */
public abstract class SpringDataResourceHandler<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID>
        > implements ResourceHandler<ENTITY, ID> {

    protected REPOSITORY repository;

    protected ResourceDefinition<ENTITY> resourceDefinition;

    public SpringDataResourceHandler(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition) {
        this.repository = repository;
        this.resourceDefinition = resourceDefinition;
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    public ENTITY insert(ENTITY entity) {
        return repository.save(entity);
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
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(ENTITY entity) {
        repository.delete(entity);
    }

    @Override
    public Optional<ENTITY> queryById(ID id) {
        return repository.findById(id);
    }
}
