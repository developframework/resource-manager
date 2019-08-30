package com.github.developframework.resource.spring.mongo;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.resource.AddCheckExistsLogic;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.exception.ResourceExistException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;

/**
 * 根据字段查重
 *
 * @author qiushui on 2019-08-21.
 */
public class ByFieldMongoAddCheckExistsLogic<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > implements AddCheckExistsLogic<ENTITY, DTO, ID> {

    private String[] fields;

    private ResourceDefinition<ENTITY> resourceDefinition;

    private MongoOperations mongoOperations;

    public ByFieldMongoAddCheckExistsLogic(ResourceDefinition<ENTITY> resourceDefinition, MongoOperations mongoOperations, String... fields) {
        this.fields = fields;
        this.resourceDefinition = resourceDefinition;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public boolean check(DTO dto) {
        Criteria criteria = Criteria.where(fields[0]).is(ExpressionUtils.getValue(dto, fields[0]));
        for (int i = 1; i < fields.length; i++) {
            criteria.and(fields[i]).is(ExpressionUtils.getValue(dto, fields[i]));
        }
        return mongoOperations.exists(Query.query(criteria), resourceDefinition.getEntityClass());
    }

    @Override
    public ResourceExistException getResourceExistException(DTO dto, String resourceName) {
        ResourceExistException exception = new ResourceExistException(resourceName);
        for (String field : fields) {
            exception.addParameter(field, ExpressionUtils.getValue(dto, field));
        }
        return exception;
    }
}
