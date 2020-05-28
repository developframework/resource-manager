package com.github.developframework.resource.spring.mongo;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ModifyCheckExistsLogic;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.exception.ResourceExistException;
import com.github.developframework.resource.operate.CheckUniqueByFieldLogic;
import develop.toolkit.base.struct.KeyValuePair;
import develop.toolkit.base.struct.KeyValuePairs;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;

/**
 * 根据字段查重
 *
 * @author qiushui on 2019-08-26.
 */
public class ByFieldMongoModifyCheckExistsLogic<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends CheckUniqueByFieldLogic<ENTITY, DTO, ID> implements ModifyCheckExistsLogic<ENTITY, DTO, ID> {

    private final String[] fields;

    private final ResourceDefinition<ENTITY> resourceDefinition;

    private final MongoOperations mongoOperations;

    public ByFieldMongoModifyCheckExistsLogic(ResourceDefinition<ENTITY> resourceDefinition, MongoOperations mongoOperations, String... fields) {
        this.fields = fields;
        this.resourceDefinition = resourceDefinition;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public boolean check(DTO dto, ENTITY entity) {
        KeyValuePairs<String, String> fieldPairs = parseFieldPair(fields);
        if (!hasNewValue(dto, entity, fieldPairs)) {
            return false;
        }
        KeyValuePair<String, String> tempPair = fieldPairs.get(0);
        Criteria criteria = Criteria.where(tempPair.getValue()).is(ExpressionUtils.getValue(dto, tempPair.getKey()));
        for (int i = 1; i < fieldPairs.size(); i++) {
            tempPair = fieldPairs.get(i);
            criteria.and(tempPair.getValue()).is(ExpressionUtils.getValue(dto, tempPair.getKey()));
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
