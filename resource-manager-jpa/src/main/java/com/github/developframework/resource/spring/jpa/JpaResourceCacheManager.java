package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.*;
import com.github.developframework.resource.spring.SpringDataResourceCacheManager;
import com.github.developframework.resource.spring.cache.CacheType;
import com.github.developframework.resource.utils.ResourceAssert;
import develop.toolkit.base.utils.CollectionAdvice;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author qiushui on 2020-04-30.
 */
@Getter
public abstract class JpaResourceCacheManager<
        PO extends com.github.developframework.resource.spring.jpa.PO<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<PO, ID> & JpaSpecificationExecutor<PO>
        > extends SpringDataResourceCacheManager<PO, ID, REPOSITORY> {

    @PersistenceContext
    protected EntityManager entityManager;

    public JpaResourceCacheManager(REPOSITORY repository, Class<PO> entityClass, String resourceName, String cacheKey, Duration timeout, CacheType cacheType) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName), cacheKey, timeout, cacheType);
    }

    @Autowired
    public void setJpaTransactionManager(JpaTransactionManager jpaTransactionManager) {
        super.transactionTemplate = new TransactionTemplate(jpaTransactionManager);
    }

    @PostConstruct
    public void init() {
        this.resourceHandler = new JpaResourceHandler<>(repository, resourceDefinition, entityManager);
        this.resourceOperateRegistry = new ResourceOperateRegistry<>(this);
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
        return CollectionAdvice.sort(list, ids, (po, id) -> po.getId().equals(id));
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
