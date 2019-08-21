package com.github.developframework.resource.spring.mongodb;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.Search;
import com.github.developframework.resource.spring.SpringDataPagingAndSortingResourceHandler;
import com.github.developframework.resource.spring.SpringDataResourceHandler;
import com.github.developframework.resource.spring.mongodb.utils.AggregationOperationBuilder;
import com.github.developframework.resource.spring.mongodb.utils.AggregationQueryHelper;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.Serializable;
import java.util.List;

/**
 * @author qiushui on 2019-08-21.
 */
public class MongoResourceHandler<ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends MongoRepository<ENTITY, ID>
        > extends SpringDataResourceHandler<ENTITY, ID, REPOSITORY> implements SpringDataPagingAndSortingResourceHandler<ENTITY, ID> {

    @Getter
    private MongoOperations mongoOperations;

    public MongoResourceHandler(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition, MongoOperations mongoOperations) {
        super(repository, resourceDefinition);
        this.mongoOperations = mongoOperations;
    }

    @Override
    public int update(ENTITY entity, Search<ENTITY> search) {
        return 0;
    }

    @Override
    public List<ENTITY> query(Search<ENTITY> search) {
        Query query = safeSearch(search);
        return mongoOperations.find(query, resourceDefinition.getEntityClass());
    }

    @Override
    public Page<ENTITY> queryPager(Pageable pageable, Search<ENTITY> search) {
        Query query = safeSearch(search);
        AggregationOperationBuilder builder = new AggregationOperationBuilder();
        if (query != null) {
            builder.match(query);
        }
        List<AggregationOperation> aggregationOperations = builder.build();
        return AggregationQueryHelper.aggregationPager(mongoOperations, pageable, aggregationOperations, resourceDefinition.getEntityClass());
    }

    @Override
    public List<ENTITY> query(Sort sort, Search<ENTITY> search) {
        Query query = safeSearch(search);
        AggregationOperationBuilder builder = new AggregationOperationBuilder();
        if (query != null) {
            builder.match(query).aggregation(Aggregation.sort(sort));
        }
        List<AggregationOperation> aggregationOperations = builder.build();
        return AggregationQueryHelper.aggregationList(mongoOperations, aggregationOperations, resourceDefinition.getEntityClass());
    }

    private Query safeSearch(Search<ENTITY> search) {
        return search != null ? ((MongoSearch<ENTITY>) search).toQuery() : null;
    }
}
