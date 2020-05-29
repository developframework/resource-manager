package com.github.developframework.resource.mybatis;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.mybatis.annotation.Id;
import com.github.developframework.resource.mybatis.annotation.Table;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.stream.Stream;

/**
 * MyBatis实体
 *
 * @author qiushui on 2020-05-28.
 */
public interface MPO<ID extends Serializable> extends Entity<ID> {

    static String getTableName(Class<?> entityClass) {
        Table annotation = entityClass.getAnnotation(Table.class);
        return annotation.name();
    }

    static String getIdFieldName(Class<?> entityClass) {
        return Stream
                .of(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(Field::getName)
                .findFirst()
                .orElseThrow();
    }
}
