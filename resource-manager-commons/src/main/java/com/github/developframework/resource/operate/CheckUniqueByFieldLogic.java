package com.github.developframework.resource.operate;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.resource.Entity;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-26.
 */
public abstract class CheckUniqueByFieldLogic<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > {

    protected boolean hasNewValue(DTO dto, ENTITY entity, String[] fields) {
        boolean hasNewValue = false;
        for (String field : fields) {
            Object dtoValue = ExpressionUtils.getValue(dto, field);
            Object entityValue = ExpressionUtils.getValue(entity, field);
            if (!dtoValue.equals(entityValue)) {
                hasNewValue = true;
                break;
            }
        }
        return hasNewValue;
    }
}
