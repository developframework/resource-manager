package com.github.developframework.resource.spring.mongodb;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.spring.SpringDataResourceManager;
import com.github.developframework.resource.spring.mongodb.utils.Querys;
import develop.toolkit.base.utils.CollectionAdvice;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author qiushui on 2019-08-21.
 */
public class MongoResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends MongoRepository<ENTITY, ID>
        > extends SpringDataResourceManager<ENTITY, ID, REPOSITORY> {

    protected MongoOperations mongoOperations;

    public MongoResourceManager(REPOSITORY repository, MongoOperations mongoOperations, ResourceDefinition<ENTITY> resourceDefinition) {
        super(repository, resourceDefinition, new MongoResourceHandler<>(repository, resourceDefinition, mongoOperations));
        this.mongoOperations = mongoOperations;
    }

    public MongoResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition, MongoResourceHandler<ENTITY, ID, REPOSITORY> resourceHandler) {
        super(repository, resourceDefinition, resourceHandler);
        this.mongoOperations = resourceHandler.getMongoOperations();
    }

    @Override
    public List<ENTITY> listForIds(ID[] ids) {
        List<ENTITY> list = mongoOperations.find(
                Querys.in(Fields.UNDERSCORE_ID, (Object[]) ids),
                resourceDefinition.getEntityClass()
        );
        return Stream.of(ids)
                .map(id -> CollectionAdvice.getFirstMatch(list, id, Entity::getId).orElse(null))
                .collect(Collectors.toList());
    }
}
