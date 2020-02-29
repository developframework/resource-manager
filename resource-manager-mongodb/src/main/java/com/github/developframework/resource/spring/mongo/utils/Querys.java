package com.github.developframework.resource.spring.mongo.utils;

import develop.toolkit.base.utils.K;
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

    public static Query id(String field, @Nullable String id) {
        Query query = new Query();
        K.let(id, v -> query.addCriteria(Criteria.where(field).is(new ObjectId(id))));
        return query;
    }

    public static Query is(String field, @Nullable Object value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).is(v)));
        return query;
    }

    public static Query ne(String field, @Nullable Object value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).ne(v)));
        return query;
    }

    public static Query gt(String field, @Nullable Object value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).gt(v)));
        return query;
    }

    public static Query gte(String field, @Nullable Object value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).gte(v)));
        return query;
    }

    public static Query lt(String field, @Nullable Object value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).lt(v)));
        return query;
    }

    public static Query lte(String field, @Nullable Object value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).lte(v)));
        return query;
    }

    public static Query regex(String field, @Nullable String keyword) {
        Query query = new Query();
        K.let(keyword, v -> query.addCriteria(Criteria.where(field).regex(escapeRegex(v))));
        return query;
    }

    public static Query in(String field, @Nullable Object... value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).in(v)));
        return query;
    }

    public static Query in(String field, @Nullable Collection<?> collection) {
        Query query = new Query();
        K.let(collection, v -> query.addCriteria(Criteria.where(field).in(v)));
        return query;
    }

    public static Query nin(String field, @Nullable Object... value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).nin(v)));
        return query;
    }

    public static Query nin(String field, @Nullable Collection<?> collection) {
        Query query = new Query();
        K.let(collection, v -> query.addCriteria(Criteria.where(field).nin(v)));
        return query;
    }

    public static Query exists(String field, @Nullable Boolean value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).exists(v)));
        return query;
    }

    public static Query type(String field, @Nullable Integer value) {
        Query query = new Query();
        K.let(value, v -> query.addCriteria(Criteria.where(field).type(v)));
        return query;
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
