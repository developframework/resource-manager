package com.github.developframework.resource.spring;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.Search;
import com.github.developframework.resource.spring.handler.JpaResourceHandler;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID> & JpaSpecificationExecutor<ENTITY>
        > extends SpringDataResourceManager<ENTITY, ID, REPOSITORY> {

    public JpaResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition, JpaResourceHandler<ENTITY, ID, REPOSITORY> resourceHandler) {
        super(repository, resourceDefinition, resourceHandler);
    }

    @Override
    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(SEARCH search) {
        return resourceHandler.query(search);
    }
}
