package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.*;
import com.github.developframework.resource.spring.SpringDataResourceManager;
import com.github.developframework.resource.utils.ResourceAssert;
import develop.toolkit.base.utils.CollectionAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * spring-data-jpa资源管理器
 *
 * @author qiushui on 2019-08-15.
 */
public abstract class JpaResourceManager<
        PO extends com.github.developframework.resource.spring.jpa.PO<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<PO, ID> & JpaSpecificationExecutor<PO>
        > extends SpringDataResourceManager<PO, ID, REPOSITORY> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    public JpaResourceManager(REPOSITORY repository, Class<PO> entityClass, String resourceName) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
    }

    @PostConstruct
    public void init() {
        this.resourceHandler = new JpaResourceHandler<>(repository, resourceDefinition, entityManager);
        this.resourceOperateRegistry = new ResourceOperateRegistry<>(this);
    }

    @Override
    public Optional<PO> add(Object dto) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.add(dto));
            }
        } else {
            return transactionTemplate.execute(transactionStatus -> super.add(dto));
        }
    }

    @Override
    public Optional<PO> modifyById(ID id, Object dto) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.modifyById(id, dto));
            }
        } else {
            return transactionTemplate.execute(transactionStatus -> super.modifyById(id, dto));
        }
    }

    @Override
    public boolean remove(PO entity) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                final Boolean execute = transactionTemplate.execute(transactionStatus -> super.remove(entity));
                return execute != null ? execute : false;
            }
        } else {
            final Boolean execute = transactionTemplate.execute(transactionStatus -> super.remove(entity));
            return execute != null ? execute : false;
        }
    }

    @Override
    public Optional<PO> removeById(ID id) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.removeById(id));
            }
        } else {
            return super.removeById(id);
        }

    }

    public List<PO> listForIds(ID[] ids) {
        return listForIds("id", ids);
    }

    public Optional<PO> findOneByIdForUpdate(ID id) {
        return resourceHandler.queryByIdForUpdate(id).map(this::execSearchOperate);
    }

    @SuppressWarnings("unchecked")
    public PO findOneByIdRequiredForUpdate(ID id) {
        return (PO) ResourceAssert
                .resourceExistAssertBuilder(resourceDefinition.getResourceName(), resourceHandler.queryByIdForUpdate(id))
                .addParameter("id", id)
                .returnValue();
    }

    @Override
    public List<PO> listForIds(String idProperty, ID[] ids) {
        if (ids.length == 0) {
            return new ArrayList<>();
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PO> query = builder.createQuery(resourceDefinition.getEntityClass());
        Root<PO> root = query.from(resourceDefinition.getEntityClass());
        query.select(root).where(root.get(idProperty).in(ids));
        List<PO> list = entityManager.createQuery(query).getResultList();
        return Stream.of(ids)
                .map(id -> CollectionAdvice.getFirstMatch(list, id, Entity::getId).orElse(null))
                .collect(Collectors.toList());
    }

    @Override
    public <T extends DTO> AddCheckExistsLogic<PO, T, ID> byFieldAddCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldJpaAddCheckExistsLogic<>(resourceDefinition, entityManager, fields);
    }

    @Override
    public <T extends DTO> ModifyCheckExistsLogic<PO, T, ID> byFieldModifyCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldJpaModifyCheckExistsLogic<>(resourceDefinition, entityManager, fields);
    }
}
