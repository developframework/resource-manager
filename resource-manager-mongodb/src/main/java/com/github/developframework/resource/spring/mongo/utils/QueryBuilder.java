package com.github.developframework.resource.spring.mongo.utils;

import develop.toolkit.base.constants.DateTimeConstants;
import develop.toolkit.base.utils.K;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     * @param field
     * @param id
     * @return
     */
    public QueryBuilder id(String field, String id) {
        if (id != null) {
            query.addCriteria(Criteria.where(field).is(new ObjectId(id)));
        }
        return this;
    }

    /**
     * 相等条件
     *
     * @param field
     * @param value
     * @return
     */
    public QueryBuilder equal(String field, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            query.addCriteria(Criteria.where(field).is(value));
        }
        return this;
    }

    /**
     * 不相等条件
     *
     * @param field
     * @param value
     * @return
     */
    public QueryBuilder notEqual(String field, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            query.addCriteria(Criteria.where(field).ne(value));
        }
        return this;
    }

    /**
     * 包含匹配条件
     *
     * @param field
     * @param value
     * @return
     */
    public QueryBuilder containsLike(String field, String value) {
        if (StringUtils.isNotBlank(value)) {
            query.addCriteria(Criteria.where(field).regex("^.*" + Querys.escapeRegex(value) + ".*$"));
        }
        return this;
    }

    /**
     * 开头匹配条件
     *
     * @param field
     * @param value
     * @return
     */
    public QueryBuilder startWith(String field, String value) {
        if (StringUtils.isNotBlank(value)) {
            query.addCriteria(Criteria.where(field).regex("^" + Querys.escapeRegex(value) + ".*$"));
        }
        return this;
    }

    /**
     * 结尾匹配条件
     *
     * @param field
     * @param value
     * @return
     */
    public QueryBuilder endWith(String field, String value) {
        if (StringUtils.isNotBlank(value)) {
            query.addCriteria(Criteria.where(field).regex("^.*" + Querys.escapeRegex(value) + "$"));
        }
        return this;
    }

    /**
     * 区间匹配条件
     *
     * @param field
     * @param min
     * @param max
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder isBetween(String field, Y min, Y max) {
        if (min != null) {
            Criteria criteria = Criteria.where(field).gte(min);
            if (max != null) {
                criteria = criteria.lte(max);
            }
            query.addCriteria(criteria);
        } else if (max != null) {
            query.addCriteria(Criteria.where(field).lte(max));
        }
        return this;
    }

    /**
     * 集合条件
     *
     * @param field
     * @param collection
     * @param <E>
     * @return
     */
    public <E> QueryBuilder in(String field, Collection<E> collection) {
        if (collection != null && !collection.isEmpty()) {
            query.addCriteria(Criteria.where(field).in(collection));
        }
        return this;
    }

    /**
     * 数组条件
     *
     * @param field
     * @param array
     * @param <E>
     * @return
     */
    public <E> QueryBuilder in(String field, E... array) {
        return in(field, array != null ? Arrays.asList(array) : null);
    }

    /**
     * 大于条件
     *
     * @param field
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder greaterThan(String field, Y value) {
        if (value != null) {
            query.addCriteria(Criteria.where(field).gt(value));
        }
        return this;
    }

    /**
     * 大于等于条件
     *
     * @param field
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder greaterThanOrEqualTo(String field, Y value) {
        if (value != null) {
            query.addCriteria(Criteria.where(field).gte(value));
        }
        return this;
    }

    /**
     * 小于条件
     *
     * @param field
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder lessThan(String field, Y value) {
        if (value != null) {
            query.addCriteria(Criteria.where(field).lt(value));
        }
        return this;
    }

    /**
     * 小于等于条件
     *
     * @param field
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> QueryBuilder lessThanOrEqualTo(String field, Y value) {
        if (value != null) {
            query.addCriteria(Criteria.where(field).lte(value));
        }
        return this;
    }

    /**
     * 日期范围
     *
     * @param field
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    public QueryBuilder dateRange(String field, String startDateStr, String endDateStr) {
        final LocalDate startDate = K.map(startDateStr, LocalDate::parse);
        final LocalDate endDate = K.map(endDateStr, LocalDate::parse);
        return dateRange(field, startDate, endDate);
    }

    /**
     * 日期范围
     *
     * @param field
     * @param startDate
     * @param endDate
     * @return
     */
    public QueryBuilder dateRange(String field, LocalDate startDate, LocalDate endDate) {
        return isBetween(
                field,
                K.map(startDate, LocalDate::atStartOfDay),
                K.map(endDate, s -> s.atTime(DateTimeConstants.LAST_SECOND))
        );
    }

    /**
     * 日期时间范围
     *
     * @param field
     * @param startTime
     * @param endTime
     * @return
     */
    public QueryBuilder dateTimeRange(String field, LocalDateTime startTime, LocalDateTime endTime) {
        return isBetween(field, startTime, endTime);
    }

    /**
     * 是否存在
     *
     * @param field
     * @param exists
     * @return
     */
    public QueryBuilder exists(String field, boolean exists) {
        query.addCriteria(Criteria.where(field).exists(exists));
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
