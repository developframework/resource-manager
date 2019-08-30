package com.github.developframework.resource.spring.mongo.utils;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.lang.Nullable;

import java.util.Collection;

/**
 * @author qiushui on 2018-08-30.
 * @since 0.1
 */
public final class Querys {

    public static Query id(String field, String id) {
        return is(field, new ObjectId(id));
    }

    public static Query is(String field, @Nullable Object value) {
        return new Query().addCriteria(Criteria.where(field).is(value));
    }

    public static Query ne(String field, @Nullable Object value) {
        return new Query().addCriteria(Criteria.where(field).ne(value));
    }

    public static Query gt(String field, Object value) {
        return new Query().addCriteria(Criteria.where(field).gt(value));
    }

    public static Query gte(String field, Object value) {
        return new Query().addCriteria(Criteria.where(field).gte(value));
    }

    public static Query lt(String field, Object value) {
        return new Query().addCriteria(Criteria.where(field).lt(value));
    }

    public static Query lte(String field, @Nullable Object value) {
        return new Query().addCriteria(Criteria.where(field).lte(value));
    }

    public static Query regex(String field, String keyword) {
        return new Query().addCriteria(Criteria.where(field).regex(escapeRegex(keyword)));
    }

    public static Query in(String field, Object... value) {
        return new Query().addCriteria(Criteria.where(field).in(value));
    }

    public static Query in(String field, Collection<?> collection) {
        return new Query().addCriteria(Criteria.where(field).in(collection));
    }

    public static Query nin(String field, Object... value) {
        return new Query().addCriteria(Criteria.where(field).nin(value));
    }

    public static Query nin(String field, Collection<?> collection) {
        return new Query().addCriteria(Criteria.where(field).nin(collection));
    }

    public static Query exists(String field, boolean value) {
        return new Query().addCriteria(Criteria.where(field).exists(value));
    }

    public static Query type(String field, int value) {
        return new Query().addCriteria(Criteria.where(field).type(value));
    }

    /**
     * 正则关键符号转义
     *
     * @param keyword
     * @return
     */
    public static String escapeRegex(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }
}
