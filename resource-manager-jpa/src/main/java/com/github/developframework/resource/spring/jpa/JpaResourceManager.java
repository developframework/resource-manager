package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.spring.SpringDataResourceManager;
import develop.toolkit.base.utils.CollectionAdvice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * jpa-data-jpa资源管理器
 *
 * @author qiushui on 2019-08-15.
 */
public abstract class JpaResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID> & JpaSpecificationExecutor<ENTITY>
        > extends SpringDataResourceManager<ENTITY, ID, REPOSITORY> {

    @PersistenceContext
    protected EntityManager entityManager;

    public JpaResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition) {
        super(repository, resourceDefinition, new JpaResourceHandler<>(repository, resourceDefinition));
    }

    public JpaResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition, JpaResourceHandler<ENTITY, ID, REPOSITORY> jpaResourceHandler) {
        super(repository, resourceDefinition, jpaResourceHandler);
    }

    @Override
    public List<ENTITY> listForIds(ID[] ids) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ENTITY> query = builder.createQuery(resourceDefinition.getEntityClass());
        Root<ENTITY> root = query.from(resourceDefinition.getEntityClass());
        query.select(root).where(root.get("id").in((Object[]) ids));
        List<ENTITY> list = entityManager.createQuery(query).getResultList();
        return Stream.of(ids)
                .map(id -> CollectionAdvice.getFirstMatch(list, id, Entity::getId).orElse(null))
                .collect(Collectors.toList());
    }
}
