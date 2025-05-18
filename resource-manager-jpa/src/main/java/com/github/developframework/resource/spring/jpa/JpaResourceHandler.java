package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.Owner;
import com.github.developframework.resource.OwnerInfo;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.Search;
import com.github.developframework.resource.spring.SpringDataResourceHandler;
import com.github.developframework.resource.spring.jpa.utils.Specifications;
import develop.toolkit.base.utils.ArrayAdvice;
import develop.toolkit.base.utils.CollectionAdvice;
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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.LockModeType;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 基于spring-data-jpa的资源操作器
 *
 * @author qiushui on 2019-08-15.
 */
@Getter
public class JpaResourceHandler<
        PO extends com.github.developframework.resource.spring.jpa.PO<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<PO, ID> & JpaSpecificationExecutor<PO>
        > extends SpringDataResourceHandler<PO, ID, REPOSITORY> {

    private final EntityManager entityManager;

    public JpaResourceHandler(REPOSITORY repository, ResourceDefinition<PO> resourceDefinition, EntityManager entityManager) {
        super(repository, resourceDefinition);
        this.entityManager = entityManager;
    }

    @Override
    public String ownerFieldName() {
        return ArrayAdvice
                .getFirstTrue(resourceDefinition.getEntityClass().getDeclaredFields(), f -> f.isAnnotationPresent(Owner.class))
                .map(field -> {
                    if (field.isAnnotationPresent(JoinColumn.class)) {
                        return ArrayAdvice
                                .getFirstTrue(field.getType().getDeclaredFields(), f -> f.isAnnotationPresent(Id.class))
                                .map(f -> field.getName() + "." + f.getName())
                                .orElse(null);
                    }
                    return field.getName();
                })
                .orElse(null);
    }

    @Override
    public final boolean existsByIdAndOwnerId(ID id, OwnerInfo ownerInfo) {
        final Class<PO> entityClass = resourceDefinition.getEntityClass();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<PO> root = query.from(entityClass);
        query.select(cb.count(root)).where(buildPredicates(cb, root, id, ownerInfo));
        return entityManager.createQuery(query).getSingleResult() > 0;
    }

    @Override
    public final void deleteByIdAndOwnerId(ID id, OwnerInfo ownerInfo) {
        final Class<PO> entityClass = resourceDefinition.getEntityClass();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<PO> query = cb.createCriteriaDelete(entityClass);
        Root<PO> root = query.from(entityClass);
        query.where(buildPredicates(cb, root, id, ownerInfo));
        entityManager.createQuery(query).executeUpdate();
    }

    @Override
    public final Optional<PO> queryByIdAndOwnerId(ID id, OwnerInfo ownerInfo) {
        final List<PO> list = entityManager.createQuery(buildQuery(id, ownerInfo)).getResultList();
        return CollectionAdvice.get(list, 0);
    }

    @Override
    public final Optional<PO> queryByIdForUpdate(ID id, OwnerInfo ownerInfo) {
        final List<PO> list = entityManager.createQuery(buildQuery(id, ownerInfo))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
        return CollectionAdvice.get(list, 0);
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

    private CriteriaQuery<PO> buildQuery(ID id, OwnerInfo ownerInfo) {
        final Class<PO> entityClass = resourceDefinition.getEntityClass();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PO> query = cb.createQuery(entityClass);
        Root<PO> root = query.from(entityClass);
        query.where(buildPredicates(cb, root, id, ownerInfo));
        return query;
    }

    private Predicate[] buildPredicates(CriteriaBuilder cb, Root<PO> root, ID id, OwnerInfo ownerInfo) {
        final String primaryKeyFieldName = primaryKeyFieldName();
        final String ownerFieldName = ownerFieldName();
        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(primaryKeyFieldName), id));
        if (ownerInfo != null
                && ownerInfo.getOwnerId() != null
                && ownerFieldName != null
                && !ownerFieldName.equals(primaryKeyFieldName)
        ) {
            predicates.add(cb.equal(Specifications.path(root, ownerFieldName), ownerInfo.getOwnerId()));
        }
        return predicates.toArray(Predicate[]::new);
    }
}
