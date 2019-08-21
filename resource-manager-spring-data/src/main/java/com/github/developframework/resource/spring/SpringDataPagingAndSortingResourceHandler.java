package com.github.developframework.resource.spring;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * @author qiushui on 2019-08-16.
 */
public interface SpringDataPagingAndSortingResourceHandler<ENTITY extends Entity<ID>, ID extends Serializable> {

    Page<ENTITY> queryPager(Pageable pageable, Search<ENTITY> search);

    List<ENTITY> query(Sort sort, Search<ENTITY> search);
}
