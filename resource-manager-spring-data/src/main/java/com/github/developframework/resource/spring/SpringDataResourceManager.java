package com.github.developframework.resource.spring;

import com.github.developframework.resource.AbstractResourceManager;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.Search;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * spring-data资源管理器
 *
 * @author qiushui on 2019-08-15.
 */
@SuppressWarnings("unchecked")
public abstract class SpringDataResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID>
        > extends AbstractResourceManager<ENTITY, ID> {

    @Getter
    protected REPOSITORY repository;

    public SpringDataResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition) {
        super(resourceDefinition);
        this.repository = repository;
    }

    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(SEARCH search) {
        List<ENTITY> list = resourceHandler.query(search);
        return execSearchOperate(list);
    }

    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(Sort sort, SEARCH search) {
        List<ENTITY> list = ((SpringDataResourceHandler) resourceHandler).query(sort, search);
        return execSearchOperate(list);
    }

    public <SEARCH extends Search<ENTITY>> Page<ENTITY> pager(Pageable pageable, SEARCH search) {
        Page<ENTITY> page = ((SpringDataResourceHandler) resourceHandler).queryPager(pageable, search);
        return execSearchOperate(page);
    }
}
