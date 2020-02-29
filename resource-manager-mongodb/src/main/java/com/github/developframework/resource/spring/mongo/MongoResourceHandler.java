package com.github.developframework.resource.spring.mongo;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.Search;
import com.github.developframework.resource.spring.SpringDataResourceHandler;
import com.github.developframework.resource.spring.mongo.utils.AggregationOperationBuilder;
import develop.toolkit.base.utils.K;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB资源操作器
 *
 * @author qiushui on 2019-08-21.
 */
public class MongoResourceHandler<ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends MongoRepository<ENTITY, ID>
        > extends SpringDataResourceHandler<ENTITY, ID, REPOSITORY> {

    @Getter
    private MongoOperations mongoOperations;

    public MongoResourceHandler(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition, MongoOperations mongoOperations) {
        super(repository, resourceDefinition);
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Optional<ENTITY> queryByIdForUpdate(ID id) {
        return queryById(id);
    }

    @Override
    public List<ENTITY> query(Search<ENTITY> search) {
        Query query = safeSearch(search);
        return query != null ? mongoOperations.find(query, resourceDefinition.getEntityClass()) : mongoOperations.findAll(resourceDefinition.getEntityClass());
    }

    @Override
    public <SEARCH extends Search<ENTITY>> List<ENTITY> query(Sort sort, SEARCH search) {
        Query query = safeSearch(search);
        AggregationOperationBuilder builder = new AggregationOperationBuilder(mongoOperations);
        K.let(query, builder::match);
        return builder.aggregation(sort != null, Aggregation.sort(sort)).list(resourceDefinition.getEntityClass(), resourceDefinition.getEntityClass());
    }

    @Override
    public <SEARCH extends Search<ENTITY>> Page<ENTITY> queryPager(Pageable pageable, SEARCH search) {
        Query query = safeSearch(search);
        AggregationOperationBuilder builder = new AggregationOperationBuilder(mongoOperations);
        K.let(query, builder::match);
        return builder.pager(pageable, resourceDefinition.getEntityClass(), resourceDefinition.getEntityClass());
    }

    private Query safeSearch(Search<ENTITY> search) {
        return K.map(search, s -> ((MongoSearch<ENTITY>) s).toQuery());
    }
}
