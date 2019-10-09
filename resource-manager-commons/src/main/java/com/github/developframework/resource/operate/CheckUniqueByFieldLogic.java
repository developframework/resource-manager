package com.github.developframework.resource.operate;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.resource.Entity;
import develop.toolkit.base.struct.KeyValuePair;
import develop.toolkit.base.struct.KeyValuePairs;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-26.
 */
public abstract class CheckUniqueByFieldLogic<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > {

    /**
     * 解析字段对  DTO字段名: ENTITY字段名
     *
     * @param fields
     * @return
     */
    protected KeyValuePairs<String, String> parseFieldPair(String[] fields) {
        KeyValuePairs<String, String> fieldPairs = new KeyValuePairs<>();
        for (String field : fields) {
            final String[] parts = field.split(":\\s*");
            fieldPairs.addKeyValue(parts[0], parts.length == 1 ? parts[0] : parts[1]);
        }
        return fieldPairs;
    }

    protected boolean hasNewValue(DTO dto, ENTITY entity, KeyValuePairs<String, String> fieldPairs) {
        boolean hasNewValue = false;
        for (KeyValuePair<String, String> pair : fieldPairs) {
            Object dtoValue = ExpressionUtils.getValue(dto, pair.getKey());
            Object entityValue = ExpressionUtils.getValue(entity, pair.getValue());
            if (!dtoValue.equals(entityValue)) {
                hasNewValue = true;
                break;
            }
        }
        return hasNewValue;
    }
}
