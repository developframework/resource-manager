package com.github.developframework.resource.spring.mongo;

import com.github.developframework.resource.DTO;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceOperateRegistry;
import com.github.developframework.resource.spring.SpringDataResourceManager;
import com.github.developframework.resource.spring.mongo.utils.Querys;
import develop.toolkit.base.utils.CollectionAdvice;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MongoDB资源管理器
 *
 * @author qiushui on 2019-08-21.
 */
@SuppressWarnings("unchecked")
public class MongoResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends MongoRepository<ENTITY, ID>
        > extends SpringDataResourceManager<ENTITY, ID, REPOSITORY> {

    protected MongoOperations mongoOperations;

    public MongoResourceManager(REPOSITORY repository, MongoOperations mongoOperations, Class<ENTITY> entityClass, String resourceName) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
        this.mongoOperations = mongoOperations;
        this.resourceHandler = new MongoResourceHandler<>(repository, resourceDefinition, mongoOperations);
        this.resourceOperateRegistry = new ResourceOperateRegistry(resourceDefinition.getEntityClass(), this);
    }

    public MongoResourceManager(REPOSITORY repository, Class<ENTITY> entityClass, String resourceName, MongoResourceHandler<ENTITY, ID, REPOSITORY> resourceHandler) {
        super(repository, new ResourceDefinition<>(entityClass, resourceName));
        this.mongoOperations = resourceHandler.getMongoOperations();
        this.resourceHandler = resourceHandler;
        this.resourceOperateRegistry = new ResourceOperateRegistry(resourceDefinition.getEntityClass(), this);
    }

    @Override
    public List<ENTITY> listForIds(ID[] ids) {
        if (ids.length == 0) {
            return new ArrayList<>();
        }
        List<ENTITY> list = mongoOperations.find(
                Querys.in(Fields.UNDERSCORE_ID, ids),
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
}
