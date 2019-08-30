package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.*;
import com.github.developframework.resource.spring.SpringDataResourceManager;
import develop.toolkit.base.utils.CollectionAdvice;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * jpa-data-jpa资源管理器
 *
 * @author qiushui on 2019-08-15.
 */
@SuppressWarnings("unchecked")
public abstract class JpaResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID> & JpaSpecificationExecutor<ENTITY>
        > extends SpringDataResourceManager<ENTITY, ID, REPOSITORY> {

    @PersistenceContext
    protected EntityManager entityManager;

    public JpaResourceManager(REPOSITORY repository, Class<ENTITY> entityClass, String resourceName) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
    }

    @PostConstruct
    public void init() {
        this.resourceHandler = new JpaResourceHandler<>(repository, resourceDefinition, entityManager);
        this.resourceOperateRegistry = new ResourceOperateRegistry(resourceDefinition.getEntityClass(), this);
    }

    @Transactional
    @Override
    public Optional<ENTITY> add(Object dto) {
        return super.add(dto);
    }

    @Transactional
    @Override
    public boolean modifyById(ID id, Object dto) {
        return super.modifyById(id, dto);
    }

    @Transactional
    @Override
    public void remove(ENTITY entity) {
        super.remove(entity);
    }

    @Transactional
    @Override
    public ENTITY removeById(ID id) {
        return super.removeById(id);
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

    @Override
    public <T extends DTO> AddCheckExistsLogic<ENTITY, T, ID> byFieldAddCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldJpaAddCheckExistsLogic<>(resourceDefinition, entityManager, fields);
    }

    @Override
    public <T extends DTO> ModifyCheckExistsLogic<ENTITY, T, ID> byFieldModifyCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldJpaModifyCheckExistsLogic<>(resourceDefinition, entityManager, fields);
    }
}
