package com.github.developframework.resource.spring.mongo;

import com.github.developframework.resource.DTO;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceOperateRegistry;
import com.github.developframework.resource.spring.SpringDataResourceManager;
import com.github.developframework.resource.spring.mongo.utils.AggregationOperationBuilder;
import com.github.developframework.resource.spring.mongo.utils.Querys;
import develop.toolkit.base.utils.CollectionAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MongoDB资源管理器
 *
 * @author qiushui on 2019-08-21.
 */
public class MongoResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends MongoRepository<ENTITY, ID>
        > extends SpringDataResourceManager<ENTITY, ID, REPOSITORY> {

    @Autowired
    protected MongoOperations mongoOperations;

    protected TransactionTemplate transactionTemplate;

    public MongoResourceManager(REPOSITORY repository, Class<ENTITY> entityClass, String resourceName) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
    }

    public MongoResourceManager(REPOSITORY repository, Class<ENTITY> entityClass, String resourceName, MongoResourceHandler<ENTITY, ID, REPOSITORY> resourceHandler) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
        this.resourceOperateRegistry = new ResourceOperateRegistry<>(this);
        this.mongoOperations = resourceHandler.getMongoOperations();
        this.resourceHandler = resourceHandler;
    }

    @Autowired
    public void setMongoTransactionManager(MongoTransactionManager mongoTransactionManager) {
        this.transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    }

    @Override
    public Optional<ENTITY> add(Object dto) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.add(dto));
            }
        } else {
            return transactionTemplate.execute(transactionStatus -> super.add(dto));
        }
    }

    @Override
    public boolean modifyById(ID id, Object dto) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                Boolean result = transactionTemplate.execute(transactionStatus -> !super.modifyById(id, dto));
                return result == null || !result;
            }
        } else {
            Boolean result = transactionTemplate.execute(transactionStatus -> !super.modifyById(id, dto));
            return result == null || !result;
        }
    }

    @Override
    public boolean remove(ENTITY entity) {
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
    public Optional<ENTITY> removeById(ID id) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.removeById(id));
            }
        } else {
            return super.removeById(id);
        }
    }

    public List<ENTITY> listForIds(ID[] ids) {
        return listForIds(Fields.UNDERSCORE_ID, ids);
    }

    @Override
    public List<ENTITY> listForIds(String idProperty, ID[] ids) {
        if (ids.length == 0) {
            return new ArrayList<>();
        }
        List<ENTITY> list = mongoOperations.find(
                Querys.in(idProperty, ids),
                resourceDefinition.getEntityClass()
        );
        return Stream.of(ids)
                .map(id -> CollectionAdvice.getFirstMatch(list, id, Entity::getId).orElse(null))
                .collect(Collectors.toList());
    }

    public <T extends DTO> ByFieldMongoAddCheckExistsLogic<ENTITY, T, ID> byFieldAddCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldMongoAddCheckExistsLogic<>(resourceDefinition, mongoOperations, fields);
    }

    public <T extends DTO> ByFieldMongoModifyCheckExistsLogic<ENTITY, T, ID> byFieldModifyCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldMongoModifyCheckExistsLogic<>(resourceDefinition, mongoOperations, fields);
    }

    @PostConstruct
    public void setResourceHandler() {
        this.resourceHandler = new MongoResourceHandler<>(repository, resourceDefinition, mongoOperations);
        this.resourceOperateRegistry = new ResourceOperateRegistry<>(this);
    }

    /**
     * 获取AggregationOperation构建器
     *
     * @return
     */
    public final AggregationOperationBuilder aggregationOperationBuilder() {
        return new AggregationOperationBuilder(mongoOperations);
    }
}
