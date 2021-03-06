package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.Search;
import com.github.developframework.resource.spring.SpringDataResourceHandler;
import develop.toolkit.base.utils.K;
import lombok.Getter;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 基于spring-data-jpa的资源操作器
 *
 * @author qiushui on 2019-08-15.
 */
public class JpaResourceHandler<
        PO extends com.github.developframework.resource.spring.jpa.PO<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<PO, ID> & JpaSpecificationExecutor<PO>
        > extends SpringDataResourceHandler<PO, ID, REPOSITORY> {

    @Getter
    private final EntityManager entityManager;

    public JpaResourceHandler(REPOSITORY repository, ResourceDefinition<PO> resourceDefinition, EntityManager entityManager) {
        super(repository, resourceDefinition);
        this.entityManager = entityManager;
    }

    @Override
    public Optional<PO> queryByIdForUpdate(ID id) {
        return Optional.ofNullable(entityManager.find(resourceDefinition.getEntityClass(), id, LockModeType.PESSIMISTIC_WRITE));
    }

    @Override
    public List<PO> query(Search<PO> search) {
        Specification<PO> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification) : IterableUtils.toList(repository.findAll());
    }

    @Override
    public <SEARCH extends Search<PO>> List<PO> query(Sort sort, SEARCH search) {
        Specification<PO> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification, sort) : IterableUtils.toList(repository.findAll(sort));
    }

    @Override
    public <SEARCH extends Search<PO>> Page<PO> queryPager(Pageable pageable, SEARCH search) {
        Specification<PO> specification = safeSearch(search);
        return specification != null ? repository.findAll(specification, pageable) : repository.findAll(pageable);
    }

    private Specification<PO> safeSearch(Search<PO> search) {
        return K.map(search, s -> ((JpaSearch<PO>) s).toSpecification());
    }
}
