package com.github.developframework.resource.spring.mongo.utils;

import com.github.developframework.resource.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * AggregationOperation构建器
 *
 * @author qiushui on 2019-02-25.
 */
public final class AggregationOperationBuilder {

    private final List<AggregationOperation> aggregationOperations;

    private final MongoOperations mongoOperations;

    private final AggregationOptions options;

    public AggregationOperationBuilder(MongoOperations mongoOperations) {
        this.aggregationOperations = new LinkedList<>();
        this.mongoOperations = mongoOperations;
        this.options = null;
    }

    public AggregationOperationBuilder(MongoOperations mongoOperations, AggregationOptions options) {
        this.aggregationOperations = new LinkedList<>();
        this.mongoOperations = mongoOperations;
        this.options = options;
    }

    public AggregationOperationBuilder(MongoOperations mongoOperations, List<AggregationOperation> aggregationOperations) {
        this.aggregationOperations = new LinkedList<>(aggregationOperations);
        this.mongoOperations = mongoOperations;
        this.options = null;
    }

    public AggregationOperationBuilder(MongoOperations mongoOperations, List<AggregationOperation> aggregationOperations, AggregationOptions options) {
        this.aggregationOperations = new LinkedList<>(aggregationOperations);
        this.mongoOperations = mongoOperations;
        this.options = options;
    }

    /**
     * 构建
     *
     * @return
     */
    public List<AggregationOperation> build() {
        return aggregationOperations;
    }

    /**
     * 关联
     */
    public AggregationOperationBuilder joinDBRef(String dbRefField, Class<?> foreignDocClass, String lookupAs) {
        return joinDBRef(
                dbRefField,
                AggregationOperations.collectionNameFormDocumentAnnotation(foreignDocClass),
                lookupAs
        );
    }

    /**
     * 关联
     */
    public AggregationOperationBuilder joinDBRef(String dbRefField, String from, String lookupAs) {
        /*
            {$lookup:{from: "",localField:".$id",foreignField:"_id",as:"Doc"}},
            {$addFields:{Doc:{$arrayElemAt:["$Doc", 0]}}},
         */

        // Aggregation.lookup 的localField内不能使用$id，所以只能使用原生json了
        aggregationOperations.add(AggregationOperations.lookupDBRef(from, dbRefField, lookupAs));
        aggregationOperations.add(AggregationOperations.addFieldsForArrayFirst(lookupAs, lookupAs));
        return this;
    }

    /**
     * lookup unwind
     *
     * @param from
     * @param localField
     * @param lookupAs
     * @param preserveNullAndEmptyArrays
     * @return
     */
    public AggregationOperationBuilder lookupAndUnwind(String from, String localField, String lookupAs, boolean preserveNullAndEmptyArrays) {
        aggregationOperations.add(Aggregation.lookup(from, localField, Fields.UNDERSCORE_ID, lookupAs));
        aggregationOperations.add(Aggregation.unwind(lookupAs, preserveNullAndEmptyArrays));
        return this;
    }

    /**
     * 过滤
     *
     * @param query
     * @return
     */
    public AggregationOperationBuilder match(Query query) {
        aggregationOperations.add(AggregationOperations.matchForQuery(query));
        return this;
    }

    /**
     * 过滤
     *
     * @param criteria
     * @return
     */
    public AggregationOperationBuilder match(Criteria criteria) {
        aggregationOperations.add(Aggregation.match(criteria));
        return this;
    }

    /**
     * 原生AggregationOperation
     *
     * @param aggregationOperations
     * @return
     */
    public AggregationOperationBuilder aggregations(List<AggregationOperation> aggregationOperations) {
        this.aggregationOperations.addAll(aggregationOperations);
        return this;
    }

    /**
     * 原生AggregationOperation
     *
     * @param aggregationOperations
     * @return
     */

    public AggregationOperationBuilder aggregations(AggregationOperation... aggregationOperations) {
        if (aggregationOperations.length == 1) {
            this.aggregationOperations.add(aggregationOperations[0]);
        } else {
            this.aggregationOperations.addAll(List.of(aggregationOperations));
        }
        return this;
    }

    /**
     * 原生AggregationOperation， 附加执行条件
     *
     * @param aggregationOperation
     * @return
     */
    public AggregationOperationBuilder aggregation(boolean predicate, AggregationOperation aggregationOperation) {
        if (predicate) {
            this.aggregationOperations.add(aggregationOperation);
        }
        return this;
    }

    /**
     * $unwind
     *
     * @param field
     * @param preserveNullAndEmptyArrays
     * @return
     */
    public AggregationOperationBuilder unwind(String field, boolean preserveNullAndEmptyArrays) {
        this.aggregationOperations.add(Aggregation.unwind(field, preserveNullAndEmptyArrays));
        return this;
    }

    /**
     * 原生json方式
     *
     * @param json
     * @deprecated 已过时 请使用nativeJson
     */
    @Deprecated
    public AggregationOperationBuilder json(String json) {
        return nativeJson(json);
    }

    public AggregationOperationBuilder nativeJson(String json) {
        this.aggregationOperations.add(AggregationOperations.buildForJson(json));
        return this;
    }

    /**
     * 原生map方式
     *
     * @param map
     */
    public AggregationOperationBuilder nativeMap(Map<String, Object> map) {
        this.aggregationOperations.add(AggregationOperations.buildForMap(map));
        return this;
    }

    /**
     * 去重
     */
    public AggregationOperationBuilder distinct(String... fields) {
        /*
            { $group: { _id: "$_id", uniqueValue: {$addToSet: "$$ROOT"} } },
            { $unwind: "$uniqueValue" },
            { $replaceRoot: {newRoot: "$uniqueValue"} }
         */
        final String key = "uniqueValue";
        this.aggregationOperations.add(
                Aggregation
                        .group(fields.length == 0 ? new String[]{Fields.UNDERSCORE_ID_REF} : fields)
                        .addToSet("$$ROOT").as(key)
        );
        this.aggregationOperations.add(Aggregation.unwind(key));
        this.aggregationOperations.add(Aggregation.replaceRoot(key));
        return this;
    }

    /**
     * 数量
     */
    public AggregationOperationBuilder count(String field) {
        this.aggregationOperations.add(Aggregation.count().as(field));
        return this;
    }

    public <ENTITY extends Entity<?>, OUT> Optional<OUT> one(Class<ENTITY> entityClass, Class<OUT> outputClass) {
        return AggregationQueryHelper.aggregationOne(mongoOperations, aggregationOperations, entityClass, outputClass);
    }

    public <OUT> Optional<OUT> one(String collectionName, Class<OUT> outputClass) {
        return AggregationQueryHelper.aggregationOne(mongoOperations, aggregationOperations, collectionName, outputClass);
    }

    public <ENTITY extends Entity<?>, OUT> List<OUT> list(Class<ENTITY> entityClass, Class<OUT> outputClass) {
        return AggregationQueryHelper.aggregationList(mongoOperations, aggregationOperations, entityClass, outputClass, options);
    }

    public <OUT> List<OUT> list(String collectionName, Class<OUT> outputClass) {
        return AggregationQueryHelper.aggregationList(mongoOperations, aggregationOperations, collectionName, outputClass, options);
    }

    public <ENTITY extends Entity<?>, OUT> Stream<OUT> stream(Class<ENTITY> entityClass, Class<OUT> outputClass) {
        return list(entityClass, outputClass).stream();
    }

    public <OUT> Stream<OUT> stream(String collectionName, Class<OUT> outputClass) {
        return list(collectionName, outputClass).stream();
    }

    public <ENTITY extends Entity<?>, OUT> Page<OUT> pager(Pageable pageable, Class<ENTITY> entityClass, Class<OUT> outputClass) {
        return AggregationQueryHelper.aggregationPager(mongoOperations, pageable, aggregationOperations, entityClass, outputClass);
    }

    public <OUT> Page<OUT> pager(Pageable pageable, String collectionName, Class<OUT> outputClass) {
        return AggregationQueryHelper.aggregationPager(mongoOperations, pageable, aggregationOperations, collectionName, outputClass);
    }

    public <ENTITY extends Entity<?>> int total(Class<ENTITY> entityClass, String countField) {
        return AggregationQueryHelper.aggregationCount(mongoOperations, aggregationOperations, entityClass, countField);
    }

    public int total(String collectionName, String countField) {
        return AggregationQueryHelper.aggregationCount(mongoOperations, aggregationOperations, collectionName, countField);
    }
}
