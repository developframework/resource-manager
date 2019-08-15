package com.github.developframework.resource.spring;

import com.github.developframework.resource.AbstractResourceManager;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceHandler;
import lombok.Getter;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * spring-data资源管理器
 *
 * @author qiushui on 2019-08-15.
 */
public abstract class SpringDataResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID>
        > extends AbstractResourceManager<ENTITY, ID> {

    @Getter
    private REPOSITORY repository;

    public SpringDataResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler) {
        super(resourceDefinition, resourceHandler);
        this.repository = repository;
    }

    @Override
    public ENTITY removeById(ID id) {
        return null;
    }

    @Override
    public List<ENTITY> listForIds(ID[] ids) {
        return null;
    }
}
