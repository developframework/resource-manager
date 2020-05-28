package com.github.developframework.resource.mybatis;

import develop.toolkit.base.struct.KeyValuePair;
import develop.toolkit.base.struct.KeyValuePairs;
import develop.toolkit.base.struct.TwoValues;
import develop.toolkit.base.utils.JavaBeanUtils;
import develop.toolkit.base.utils.ObjectAdvice;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * 基础Mapper SQL提供器
 *
 * @author qiushui on 2020-05-28.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class BaseMapperSqlProvider {

    public String insert(MPO entity) {
        Class entityClass = entity.getClass();
        KeyValuePairs<String, Object> keyValuePairs = ObjectAdvice.readAllFieldValue(entity);
        return new SQL() {{
            INSERT_INTO(MPO.getTableName(entityClass));
            keyValuePairs
                    .stream()
                    .filter(kv -> kv.getValue() != null)
                    .map(KeyValuePair::getKey)
                    .forEach(key -> VALUES(key, String.format("#{%s}", JavaBeanUtils.camelcaseToUnderline(key))));
        }}.toString();
    }

    public String update(MPO entity) {
        Class entityClass = entity.getClass();
        KeyValuePairs<String, Object> keyValuePairs = ObjectAdvice.readAllFieldValue(entity);
        String idFieldName = MPO.getIdFieldName(entityClass);
        return new SQL() {{
            UPDATE(MPO.getTableName(entityClass));
            keyValuePairs
                    .stream()
                    .filter(kv -> kv.getValue() != null)
                    .map(KeyValuePair::getKey)
                    .forEach(key -> SET(String.format("`%s` = #{%s}", JavaBeanUtils.camelcaseToUnderline(key), key)));
            WHERE(String.format("`%s` = #{%s}", JavaBeanUtils.camelcaseToUnderline(idFieldName), idFieldName));
        }}.toString();
    }

    public String deleteById(Map<String, Object> parameter) {
        Class<? extends MPO<?>> entityClass = (Class<? extends MPO<?>>) parameter.get("entityClass");
        String idFieldName = MPO.getIdFieldName(entityClass);
        return new SQL() {{
            DELETE_FROM(MPO.getTableName(entityClass));
            WHERE(String.format("`%s` = #{id}", JavaBeanUtils.camelcaseToUnderline(idFieldName)));
        }}.toString();
    }

    public String existsById(Map<String, Object> parameter) {
        Class<? extends MPO<?>> entityClass = (Class<? extends MPO<?>>) parameter.get("entityClass");
        String idFieldName = MPO.getIdFieldName(entityClass);
        return new SQL() {{
            SELECT("COUNT(1) > 0");
            FROM(MPO.getTableName(entityClass));
            WHERE(String.format("`%s` = #{id}", JavaBeanUtils.camelcaseToUnderline(idFieldName)));
        }}.toString();
    }

    public String findById(Map<String, Object> parameter) {
        Class<? extends MPO<?>> entityClass = (Class<? extends MPO<?>>) parameter.get("entityClass");
        String idFieldName = MPO.getIdFieldName(entityClass);
        return new SQL() {{
            SELECT("*");
            FROM(MPO.getTableName(entityClass));
            WHERE(String.format("`%s` = #{id}", JavaBeanUtils.camelcaseToUnderline(idFieldName)));
        }}.toString();
    }

    public String findList(Map<String, Object> parameter) {
        Class<? extends MPO<?>> entityClass = (Class<? extends MPO<?>>) parameter.get("entityClass");
        MybatisSearch<?> search = (MybatisSearch<?>) parameter.get("search");
        return new SQL() {{
            SELECT("*");
            FROM(MPO.getTableName(entityClass));
            String sql = search.whereSQL();
            if (StringUtils.isNotEmpty(sql)) {
                WHERE(sql);
            }
            OrderBy[] orderBy = (OrderBy[]) parameter.get("orderBy");
            if (orderBy != null) {
                ORDER_BY(StringUtils.join(orderBy, ","));
            }
            TwoValues<Integer, Integer> limit = (TwoValues<Integer, Integer>) parameter.get("limit");
            if (limit != null) {
                OFFSET(limit.getFirstValue());
                LIMIT(limit.getSecondValue());
            }
        }}.toString();
    }

    public String findListByWhere(Map<String, Object> parameter) {
        Class<? extends MPO<?>> entityClass = (Class<? extends MPO<?>>) parameter.get("entityClass");
        String whereSQL = (String) parameter.get("where");
        return new SQL() {{
            SELECT("*");
            FROM(MPO.getTableName(entityClass));
            if (StringUtils.isNotEmpty(whereSQL)) {
                WHERE(whereSQL);
            }
            OrderBy[] orderBy = (OrderBy[]) parameter.get("orderBy");
            if (orderBy != null) {
                ORDER_BY(StringUtils.join(orderBy, ","));
            }
            TwoValues<Integer, Integer> limit = (TwoValues<Integer, Integer>) parameter.get("limit");
            if (limit != null) {
                OFFSET(limit.getFirstValue());
                LIMIT(limit.getSecondValue());
            }
        }}.toString();
    }

    public String countBy(Map<String, Object> parameter) {
        Class<? extends MPO<?>> entityClass = (Class<? extends MPO<?>>) parameter.get("entityClass");
        MybatisSearch<?> search = (MybatisSearch<?>) parameter.get("search");
        return new SQL() {{
            SELECT("COUNT(1)");
            FROM(MPO.getTableName(entityClass));
            String sql = search.whereSQL();
            if (StringUtils.isNotEmpty(sql)) {
                WHERE(sql);
            }
        }}.toString();
    }
}
