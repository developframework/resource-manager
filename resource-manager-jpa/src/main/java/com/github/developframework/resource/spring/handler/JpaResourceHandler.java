package com.github.developframework.resource.spring.handler;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.Search;
import com.github.developframework.resource.spring.JpaSearch;
import com.github.developframework.resource.spring.SpringDataPagingAndSortingResourceHandler;
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
        > extends SpringDataResourceHandler<ENTITY, ID, REPOSITORY> implements SpringDataPagingAndSortingResourceHandler<ENTITY, ID> {

    public JpaResourceHandler(REPOSITORY repository) {
        super(repository);
    }

    @Override
    public int update(ENTITY entity, Search<ENTITY> search) {
        return 0;
    }

    @Override
    public List<ENTITY> query(Search<ENTITY> search) {
        Specification<ENTITY> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification) : IterableUtils.toList(repository.findAll());
    }

    @Override
    public List<ENTITY> query(Sort sort, Search<ENTITY> search) {
        Specification<ENTITY> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification, sort) : IterableUtils.toList(repository.findAll(sort));
    }

    @Override
    public Page<ENTITY> queryPager(Pageable pageable, Search<ENTITY> search) {
        Specification<ENTITY> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification, pageable) : repository.findAll(pageable);
    }

    private Specification<ENTITY> safeSearch(Search<ENTITY> search) {
        return search != null ? ((JpaSearch<ENTITY>) search).toSpecification() : null;
    }
}
