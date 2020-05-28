package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.Search;
import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-15.
 */
public interface JpaSearch<
        PO extends com.github.developframework.resource.spring.jpa.PO<? extends Serializable>
        > extends Search<PO> {

    Specification<PO> toSpecification();
}
