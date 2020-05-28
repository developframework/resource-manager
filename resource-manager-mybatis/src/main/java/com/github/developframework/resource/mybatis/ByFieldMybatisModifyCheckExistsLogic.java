package com.github.developframework.resource.mybatis;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.resource.ModifyCheckExistsLogic;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.exception.ResourceExistException;
import com.github.developframework.resource.operate.CheckUniqueByFieldLogic;
import develop.toolkit.base.struct.KeyValuePair;
import develop.toolkit.base.struct.KeyValuePairs;
import develop.toolkit.base.utils.ObjectAdvice;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 根据字段查重
 *
 * @author qiushui on 2019-08-26.
 */
public class ByFieldMybatisModifyCheckExistsLogic<
        PO extends MPO<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends CheckUniqueByFieldLogic<PO, DTO, ID> implements ModifyCheckExistsLogic<PO, DTO, ID> {

    private final String[] fields;

    private final ResourceDefinition<PO> resourceDefinition;

    private final BaseDaoMapper<PO, ID> daoMapper;

    public ByFieldMybatisModifyCheckExistsLogic(ResourceDefinition<PO> resourceDefinition, BaseDaoMapper<PO, ID> daoMapper, String... fields) {
        this.daoMapper = daoMapper;
        this.fields = fields;
        this.resourceDefinition = resourceDefinition;
    }

    @Override
    public boolean check(DTO dto, PO entity) {
        KeyValuePairs<String, String> pairs = parseFieldPair(fields);
        if (!hasNewValue(dto, entity, pairs)) {
            return false;
        }
        KeyValuePairs<String, Object> fields = KeyValuePairs.of(
                Stream
                        .of(this.fields)
                        .map(fieldName ->
                                KeyValuePair.of(
                                        fieldName,
                                        ObjectAdvice.get(dto, fieldName, true)
                                )
                        )
                        .collect(Collectors.toUnmodifiableList())
        );
        return daoMapper.existsByFields(resourceDefinition.getEntityClass(), fields);
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
