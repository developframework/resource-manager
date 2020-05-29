package com.github.developframework.resource.mybatis;

import develop.toolkit.base.struct.TwoValues;
import develop.toolkit.base.utils.JavaBeanUtils;
import develop.toolkit.base.utils.ObjectAdvice;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基础Mapper SQL提供器
 *
 * @author qiushui on 2020-05-28.
 */
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class BaseMapperMysqlProvider {

    public String createTable(Class<MPO> entityClass) {
        Engine engine = entityClass.getAnnotation(Engine.class);
        return new StringBuilder(String.format("CREATE TABLE IF NOT EXISTS `%s` ", MPO.getTableName(entityClass)))
                .append(
                        FieldUtils
                                .getAllFieldsList(entityClass)
                                .parallelStream()
                                .filter(f -> !f.isAnnotationPresent(Transient.class))
                                .map(this::columnDefinition)
                                .collect(Collectors.joining(",", "(", ")"))
                )
                .append("ENGINE =")
                .append(engine == null ? MysqlEngine.InnoDB : engine.value())
                .toString();
    }

    public String insert(MPO entity) {
        Class entityClass = entity.getClass();
        final Map<Field, Object> fieldMap = ObjectAdvice.readAllFieldValue(entity);
        return new SQL() {{
            INSERT_INTO(MPO.getTableName(entityClass));
            fieldMap
                    .entrySet()
                    .parallelStream()
                    .filter(entry -> {
                        Field field = entry.getKey();
                        if (field.isAnnotationPresent(Id.class)) {
                            GeneratedValue annotation = field.getAnnotation(GeneratedValue.class);
                            if (annotation != null) {
                                return annotation.strategy() != GenerationType.IDENTITY;
                            }
                        }
                        return !field.isAnnotationPresent(Transient.class);
                    })
                    .map(Map.Entry::getKey)
                    .forEach(field -> VALUES(field.getName(), String.format("#{%s}", JavaBeanUtils.camelcaseToUnderline(field.getName()))));
        }}.toString();
    }

    public String insertAll(Map<String, Object> parameter) {
        Class<? extends MPO<?>> entityClass = (Class<? extends MPO<?>>) parameter.get("entityClass");
        Collection<MPO> entities = (List<MPO>) parameter.get("entities");
        final String idFieldName = MPO.getIdFieldName(entityClass);
        final List<String> acceptFieldNames = FieldUtils
                .getAllFieldsList(entityClass)
                .stream()
                .filter(f -> {
                    if (f.isAnnotationPresent(Id.class)) {
                        GeneratedValue annotation = f.getAnnotation(GeneratedValue.class);
                        if (annotation != null) {
                            return annotation.strategy() != GenerationType.IDENTITY;
                        }
                    }
                    return !f.isAnnotationPresent(Transient.class);
                })
                .map(Field::getName)
                .collect(Collectors.toUnmodifiableList());
        List<String> values = new LinkedList<>();
        for (int i = 0; i < entities.size(); i++) {
            String[] items = new String[acceptFieldNames.size()];
            for (int j = 0; j < acceptFieldNames.size(); j++) {
                items[j] = String.format("#{entities[%d].%s}", i, acceptFieldNames.get(j));
            }
            values.add(Stream.of(items).collect(Collectors.joining(",", "(", ")")));
        }
        return new StringBuilder()
                .append(String.format("INSERT INTO `%s`", MPO.getTableName(entityClass)))
                .append(
                        acceptFieldNames
                                .parallelStream()
                                .map(JavaBeanUtils::camelcaseToUnderline)
                                .collect(Collectors.joining(",", "(", ")"))
                )
                .append(" VALUES")
                .append(String.join(",", values))
                .toString();
    }

    public String update(MPO entity) {
        Class entityClass = entity.getClass();
        Map<Field, Object> fieldMap = ObjectAdvice.readAllFieldValue(entity);
        String idFieldName = MPO.getIdFieldName(entityClass);
        return new SQL() {{
            UPDATE(MPO.getTableName(entityClass));
            fieldMap
                    .entrySet()
                    .stream()
                    .filter(kv -> kv.getValue() != null)
                    .map(Map.Entry::getKey)
                    .forEach(field -> SET(String.format("`%s` = #{%s}", JavaBeanUtils.camelcaseToUnderline(field.getName()), field.getName())));
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

    public String findByIdForUpdate(Map<String, Object> parameter) {
        return findById(parameter) + " FOR UPDATE";
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

    private String columnDefinition(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        String name = null, type;
        int length = 0, precision = 10, scale = 2;
        boolean nullable = true, unique = false;
        if (annotation != null) {
            String columnDefinition = annotation.columnDefinition();
            if (columnDefinition.isEmpty()) {
                name = annotation.name();
                length = annotation.length();
                precision = annotation.precision();
                scale = annotation.scale();
                nullable = annotation.nullable();
                unique = annotation.unique();
            } else {
                return columnDefinition;
            }
        }
        if (StringUtils.isEmpty(name)) {
            name = JavaBeanUtils.camelcaseToUnderline(field.getName());
        }
        type = columnTypeForFieldType(field.getType(), length, precision, scale);
        return String.join(
                " ",
                new String[]{
                        String.format("`%s`", name),
                        type,
                        nullable ? "NULL" : "NOT NULL",
                        unique ? "UNIQUE" : "",
                        field.isAnnotationPresent(Id.class) ? "PRIMARY KEY" : ""
                }
        );
    }

    private String columnTypeForFieldType(Class<?> clazz, int length, int precision, int scale) {
        if (clazz == String.class) {
            return String.format("VARCHAR(%d)", length == 0 ? 255 : length);
        } else if (clazz == int.class || clazz == Integer.class) {
            return String.format("INT(%d)", length == 0 ? 11 : length);
        } else if (clazz == long.class || clazz == Long.class || clazz == Instant.class) {
            return String.format("BIGINT(%d)", length == 0 ? 20 : length);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return "TINYINT(1)";
        } else if (clazz == BigDecimal.class) {
            return String.format("DECIMAL(%d, %d)", precision, scale);
        } else if (clazz == float.class || clazz == Float.class) {
            return String.format("FLOAT(%d, %d)", precision, scale);
        } else if (clazz == double.class || clazz == Double.class) {
            return String.format("DOUBLE(%d, %d)", precision, scale);
        } else if (clazz == LocalDateTime.class || clazz == OffsetDateTime.class || clazz == java.util.Date.class) {
            return "DATETIME";
        } else if (clazz == LocalDate.class || clazz == java.sql.Date.class) {
            return "DATE";
        } else if (clazz == java.sql.Timestamp.class) {
            return "TIMESTAMP";
        } else {
            return String.format("VARCHAR(%d)", length == 0 ? 255 : length);
        }
    }
}
