package com.github.developframework.resource.spring.jpa;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.resource.ModifyCheckExistsLogic;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.exception.ResourceExistException;
import com.github.developframework.resource.operate.CheckUniqueByFieldLogic;
import com.github.developframework.resource.spring.jpa.utils.JpaQueryHelper;
import develop.toolkit.base.struct.KeyValuePairs;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * 根据字段查重
 *
 * @author qiushui on 2019-08-26.
 */
public class ByFieldJpaModifyCheckExistsLogic<
        PO extends com.github.developframework.resource.spring.jpa.PO<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends CheckUniqueByFieldLogic<PO, DTO, ID> implements ModifyCheckExistsLogic<PO, DTO, ID> {

    private final String[] fields;

    private final ResourceDefinition<PO> resourceDefinition;

    private final EntityManager entityManager;

    public ByFieldJpaModifyCheckExistsLogic(ResourceDefinition<PO> resourceDefinition, EntityManager entityManager, String... fields) {
        this.fields = fields;
        this.resourceDefinition = resourceDefinition;
        this.entityManager = entityManager;
    }

    @Override
    public boolean check(DTO dto, PO entity) {
        KeyValuePairs<String, String> pairs = parseFieldPair(fields);
        if (!hasNewValue(dto, entity, pairs)) {
            return false;
        }
        return JpaQueryHelper.queryCount(entityManager, resourceDefinition.getEntityClass(), (root, query, builder) ->
                builder.and(
                        Stream
                                .of(fields)
                                .map(fieldName -> {
                                    Object value = ExpressionUtils.getValue(dto, fieldName);
                                    return builder.equal(root.get(fieldName), value);
                                })
                                .toArray(Predicate[]::new)
                )
        ) > 0;
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
