package com.github.developframework.resource.spring.jpa;

import com.github.developframework.resource.*;
import com.github.developframework.resource.spring.SpringDataResourceManager;
import develop.toolkit.base.utils.CollectionAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
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

    @Autowired
    protected TransactionTemplate transactionTemplate;

    public JpaResourceManager(REPOSITORY repository, Class<ENTITY> entityClass, String resourceName) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
    }

    @PostConstruct
    public void init() {
        this.resourceHandler = new JpaResourceHandler<>(repository, resourceDefinition, entityManager);
        this.resourceOperateRegistry = new ResourceOperateRegistry(this);
    }

    @Override
    public Optional<ENTITY> add(Object dto) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.add(dto));
            }
        } else {
            return super.add(dto);
        }
    }

    @Override
    public boolean modifyById(ID id, Object dto) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                Boolean result = transactionTemplate.execute(transactionStatus -> super.modifyById(id, dto));
                return result != null && result;
            }
        } else {
            return super.modifyById(id, dto);
        }
    }

    @Override
    public void remove(ENTITY entity) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        JpaResourceManager.super.remove(entity);
                    }
                });
            }
        } else {
            super.remove(entity);
        }
    }

    @Override
    public ENTITY removeById(ID id) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.removeById(id));
            }
        } else {
            return super.removeById(id);
        }

    }

    @Transactional(readOnly = true)
    @Override
    public List<ENTITY> listForIds(ID[] ids) {
        if (ids.length == 0) {
            return new ArrayList<>();
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ENTITY> query = builder.createQuery(resourceDefinition.getEntityClass());
        Root<ENTITY> root = query.from(resourceDefinition.getEntityClass());
        query.select(root).where(root.get("id").in(ids));
        List<ENTITY> list = entityManager.createQuery(query).getResultList();
        return Stream.of(ids)
                .map(id -> CollectionAdvice.getFirstMatch(list, id, Entity::getId).orElse(null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ENTITY findOneByIdRequired(ID id) {
        return super.findOneByIdRequired(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<ENTITY> findOneById(ID id) {
        return super.findOneById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public <SEARCH extends Search<ENTITY>> Page<ENTITY> pager(Pageable pageable, SEARCH search) {
        return super.pager(pageable, search);
    }

    @Transactional(readOnly = true)
    @Override
    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(SEARCH search) {
        return super.list(search);
    }

    @Transactional(readOnly = true)
    @Override
    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(Sort sort, SEARCH search) {
        return super.list(sort, search);
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
