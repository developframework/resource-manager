package com.github.developframework.resource.spring;

import com.github.developframework.resource.*;
import lombok.Getter;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * jpa-data资源管理器
 *
 * @author qiushui on 2019-08-15.
 */
public abstract class SpringDataResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID>
        > extends AbstractResourceManager<ENTITY, ID> {

    @Getter
    protected REPOSITORY repository;

    public SpringDataResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler) {
        super(resourceDefinition, resourceHandler);
        this.repository = repository;
    }

    @Override
    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(SEARCH search) {
        return resourceHandler.query(search);
    }
}
