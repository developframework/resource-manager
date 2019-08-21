package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.Search;
import com.github.developframework.resource.spring.SpringDataResourceHandler;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * 基于spring-data-jpa的资源操作器
 *
 * @author qiushui on 2019-08-15.
 */
public class JpaResourceHandler<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID> & JpaSpecificationExecutor<ENTITY>
        > extends SpringDataResourceHandler<ENTITY, ID, REPOSITORY> {

    public JpaResourceHandler(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition) {
        super(repository, resourceDefinition);
    }

    @Override
    public List<ENTITY> query(Search<ENTITY> search) {
        Specification<ENTITY> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification) : IterableUtils.toList(repository.findAll());
    }

    @Override
    public <SEARCH extends Search<ENTITY>> List<ENTITY> query(Sort sort, SEARCH search) {
        Specification<ENTITY> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification, sort) : IterableUtils.toList(repository.findAll(sort));
    }

    @Override
    public <SEARCH extends Search<ENTITY>> Page<ENTITY> queryPager(Pageable pageable, SEARCH search) {
        Specification<ENTITY> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification, pageable) : repository.findAll(pageable);
    }

    private Specification<ENTITY> safeSearch(Search<ENTITY> search) {
        return search != null ? ((JpaSearch<ENTITY>) search).toSpecification() : null;
    }
}
