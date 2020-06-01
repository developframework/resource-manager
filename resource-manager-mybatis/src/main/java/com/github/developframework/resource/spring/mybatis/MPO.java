package com.github.developframework.resource.spring.mybatis;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.spring.mybatis.annotation.Id;
import com.github.developframework.resource.spring.mybatis.annotation.Table;
import develop.toolkit.base.utils.JavaBeanUtils;

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
        String name = annotation.name();
        return name.isEmpty() ? JavaBeanUtils.camelcaseToUnderline(entityClass.getSimpleName()) : annotation.name();
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
