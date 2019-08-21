package com.github.developframework.resource.spring.mongodb.utils;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author qiushui on 2018-11-05.
 */
public final class QueryBuilder {

    private Query query;

    public QueryBuilder() {
        this.query = new Query();
    }

    public QueryBuilder(Query query) {
        this.query = query;
    }

    /**
     * 构建
     *
     * @return
     */
    public Query build() {
        return query;
    }

    /**
     * 匹配ID
     *
     * @param attribute
     * @param id
     * @return
     */
    public QueryBuilder id(String attribute, String id) {
        if (id != null) {
            query.addCriteria(Criteria.where(attribute).is(new ObjectId(id)));
        }
        return this;
    }

    /**
     * 相等条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public QueryBuilder equal(String attribute, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            query.addCriteria(Criteria.where(attribute).is(value));
        }
        return this;
    }

    /**
     * 不相等条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public QueryBuilder notEqual(String attribute, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            query.addCriteria(Criteria.where(attribute).ne(value));
        }
        return this;
    }

    /**
     * 包含匹配条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public QueryBuilder containsLike(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            query.addCriteria(Criteria.where(attribute).regex("^.*" + Querys.escapeRegex(value) + ".*$"));
        }
        return this;
    }

    /**
     * 开头匹配条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public QueryBuilder startWith(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            query.addCriteria(Criteria.where(attribute).regex("^" + Querys.escapeRegex(value) + ".*$"));
        }
        return this;
    }

    /**
     * 结尾匹配条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public QueryBuilder endWith(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            query.addCriteria(Criteria.where(attribute).regex("^.*" + Querys.escapeRegex(value) + "$"));
        }
        return this;
    }

    /**
     * 区间匹配条件
     *
     * @param attribute
     * @param min
     * @param max
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder isBetween(String attribute, Y min, Y max) {
        if (min != null) {
            Criteria criteria = Criteria.where(attribute).gte(min);
            if (max != null) {
                criteria = criteria.lte(max);
            }
            query.addCriteria(criteria);
        } else if (max != null) {
            query.addCriteria(Criteria.where(attribute).lte(max));
        }
        return this;
    }

    /**
     * 集合条件
     *
     * @param attribute
     * @param collection
     * @param <E>
     * @return
     */
    public <E> QueryBuilder in(String attribute, Collection<E> collection) {
        if (collection != null && !collection.isEmpty()) {
            query.addCriteria(Criteria.where(attribute).in(collection));
        }
        return this;
    }

    /**
     * 数组条件
     *
     * @param attribute
     * @param array
     * @param <E>
     * @return
     */
    public <E> QueryBuilder in(String attribute, E... array) {
        return in(attribute, array != null ? Arrays.asList(array) : null);
    }

    /**
     * 大于条件
     *
     * @param attribute
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder greaterThan(String attribute, Y value) {
        if (value != null) {
            query.addCriteria(Criteria.where(attribute).gt(value));
        }
        return this;
    }

    /**
     * 大于等于条件
     *
     * @param attribute
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder greaterThanOrEqualTo(String attribute, Y value) {
        if (value != null) {
            query.addCriteria(Criteria.where(attribute).gte(value));
        }
        return this;
    }

    /**
     * 小于条件
     *
     * @param attribute
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder lessThan(String attribute, Y value) {
        if (value != null) {
            query.addCriteria(Criteria.where(attribute).lt(value));
        }
        return this;
    }

    /**
     * 小于等于条件
     *
     * @param attribute
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder lessThanOrEqualTo(String attribute, Y value) {
        if (value != null) {
            query.addCriteria(Criteria.where(attribute).lte(value));
        }
        return this;
    }

    /**
     * 是否存在
     *
     * @param attribute
     * @param exists
     * @return
     */
    public QueryBuilder exists(String attribute, boolean exists) {
        query.addCriteria(Criteria.where(attribute).exists(exists));
        return this;
    }

    /**
     * 复杂Query
     *
     * @param complexQuery
     * @return
     */
    public QueryBuilder complexQuery(ComplexQuery complexQuery) {
        complexQuery.complex(query);
        return this;
    }

    /**
     * 复杂Query
     */
    public interface ComplexQuery {

        void complex(Query query);
    }
}
