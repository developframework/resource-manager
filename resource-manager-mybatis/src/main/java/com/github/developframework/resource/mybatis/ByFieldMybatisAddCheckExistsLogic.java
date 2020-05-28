package com.github.developframework.resource.mybatis;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.resource.AddCheckExistsLogic;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.exception.ResourceExistException;
import develop.toolkit.base.struct.KeyValuePair;
import develop.toolkit.base.struct.KeyValuePairs;
import develop.toolkit.base.utils.ObjectAdvice;

import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 根据字段查重
 *
 * @author qiushui on 2020-05-28.
 */
public class ByFieldMybatisAddCheckExistsLogic<
        PO extends MPO<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > implements AddCheckExistsLogic<PO, DTO, ID> {

    private final String[] fields;

    private final ResourceDefinition<PO> resourceDefinition;

    private final BaseDaoMapper<PO, ID> daoMapper;

    public ByFieldMybatisAddCheckExistsLogic(ResourceDefinition<PO> resourceDefinition, BaseDaoMapper<PO, ID> daoMapper, String... fields) {
        this.fields = fields;
        this.resourceDefinition = resourceDefinition;
        this.daoMapper = daoMapper;
    }

    @Override
    public boolean check(DTO dto) {
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
