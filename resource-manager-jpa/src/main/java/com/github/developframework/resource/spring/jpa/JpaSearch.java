package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.Search;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-15.
 */
public interface JpaSearch<ENTITY extends Entity<? extends Serializable>> extends Search<ENTITY> {

    Specification<ENTITY> toSpecification();
}
