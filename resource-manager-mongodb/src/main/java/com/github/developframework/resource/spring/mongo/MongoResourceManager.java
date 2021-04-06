package com.github.developframework.resource.spring.mongo;

import com.github.developframework.resource.DTO;
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
import java.util.Collection;
import java.util.List;

/**
 * MongoDB资源管理器
 *
 * @author qiushui on 2019-08-21.
 */
public class MongoResourceManager<
        DOC extends com.github.developframework.resource.spring.mongo.DOC<ID>,
        ID extends Serializable,
        REPOSITORY extends MongoRepository<DOC, ID>
        > extends SpringDataResourceManager<DOC, ID, REPOSITORY> {

    @Autowired
    protected MongoOperations mongoOperations;

    public MongoResourceManager(REPOSITORY repository, Class<DOC> entityClass, String resourceName) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
    }

    public MongoResourceManager(REPOSITORY repository, Class<DOC> entityClass, String resourceName, MongoResourceHandler<DOC, ID, REPOSITORY> resourceHandler) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
        this.resourceOperateRegistry = new ResourceOperateRegistry<>(this);
        this.mongoOperations = resourceHandler.getMongoOperations();
        this.resourceHandler = resourceHandler;
    }

    @Autowired
    public void setMongoTransactionManager(MongoTransactionManager mongoTransactionManager) {
        super.transactionTemplate = new TransactionTemplate(mongoTransactionManager);
    }

    public List<DOC> listForIds(ID[] ids) {
        return listForIds(Fields.UNDERSCORE_ID, ids);
    }

    @Override
    public List<DOC> listForIds(String idProperty, ID[] ids) {
        if (ids.length == 0) {
            return new ArrayList<>();
        }
        List<DOC> list = mongoOperations.find(
                Querys.in(idProperty, ids),
                resourceDefinition.getEntityClass()
        );
        return CollectionAdvice.sort(list, ids, (po, id) -> po.getId().equals(id));
    }

    public List<DOC> listForIds(Collection<ID> ids) {
        return listForIds(Fields.UNDERSCORE_ID, ids);
    }

    @Override
    public List<DOC> listForIds(String idProperty, Collection<ID> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<DOC> list = mongoOperations.find(
                Querys.in(idProperty, ids),
                resourceDefinition.getEntityClass()
        );
        return ids instanceof List ? CollectionAdvice.sort(list, ids, (po, id) -> po.getId().equals(id)) : list;
    }

    public <T extends DTO> ByFieldMongoAddCheckExistsLogic<DOC, T, ID> byFieldAddCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldMongoAddCheckExistsLogic<>(resourceDefinition, mongoOperations, fields);
    }

    public <T extends DTO> ByFieldMongoModifyCheckExistsLogic<DOC, T, ID> byFieldModifyCheck(Class<T> dtoClass, String... fields) {
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
