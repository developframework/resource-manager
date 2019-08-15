package com.github.developframework.resource.spring;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.Search;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * spring-data-jpa资源管理器
 *
 * @author qiushui on 2019-08-15.
 */
public class JpaResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID>
        > extends SpringDataResourceManager<ENTITY, ID, REPOSITORY> {

    public JpaResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler) {
        super(repository, resourceDefinition, resourceHandler);
    }

    @Override
    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(SEARCH search) {
        return null;
    }
}
