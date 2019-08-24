package com.github.developframework.resource.spring.mongodb.utils;

import com.github.developframework.resource.Entity;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * AggregationOperation构建器
 *
 * @author qiushui on 2019-02-25.
 */
public class AggregationOperationBuilder<ENTITY extends Entity<ID>, ID extends Serializable> {

    private final static String REF_SUFFIX = "Id";

    private List<AggregationOperation> aggregationOperations;

    private MongoOperations mongoOperations;

    private Class<ENTITY> entityClass;

    public AggregationOperationBuilder(MongoOperations mongoOperations, Class<ENTITY> entityClass) {
        this.aggregationOperations = new LinkedList<>();
        this.mongoOperations = mongoOperations;
        this.entityClass = entityClass;
    }

    public AggregationOperationBuilder(MongoOperations mongoOperations, Class<ENTITY> entityClass, List<AggregationOperation> aggregationOperations) {
        this.aggregationOperations = new LinkedList<>(aggregationOperations);
        this.mongoOperations = mongoOperations;
        this.entityClass = entityClass;
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
     * 合并
     *
     * @param otherAggregationOperations
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> merge(List<AggregationOperation> otherAggregationOperations) {
        aggregationOperations.addAll(otherAggregationOperations);
        return this;
    }

    /**
     * 关联
     *
     * @param localField
     * @param lookupAs
     * @param foreignDocClass
     * @param joinType
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> join(String localField, String lookupAs, Class<?> foreignDocClass, JoinType joinType, boolean preserveNullAndEmptyArrays) {
        final String from = AggregationOperationUtils.collectionNameFormDocumentAnnotation(foreignDocClass);
        return join(localField, lookupAs, from, joinType, preserveNullAndEmptyArrays);
    }

    /**
     * 关联
     *
     * @param localField
     * @param lookupAs
     * @param from
     * @param joinType
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> join(String localField, String lookupAs, String from, JoinType joinType, boolean preserveNullAndEmptyArrays) {

        /*

         等价于
         {$addFields:{"fieldId":{$let:{vars:{myVar:{$arrayElemAt:[{$objectToArray:"$field"},1]}},in:"$$myVar.v"}}}},
         {$lookup:{from: "foreignDocClass", localField:"fieldId", foreignField:"_id", as:"lookupAs"}},
         {$unwind: {path: "$lookupAs", preserveNullAndEmptyArrays: true}}

         */

        final String localFieldId = localField + REF_SUFFIX;
        aggregationOperations.add(AggregationOperationUtils.addRefFields(localFieldId, localField, joinType));
        return lookupAndUnwind(from, localFieldId, lookupAs, preserveNullAndEmptyArrays);
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
    public AggregationOperationBuilder<ENTITY, ID> lookupAndUnwind(String from, String localField, String lookupAs, boolean preserveNullAndEmptyArrays) {
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
    public AggregationOperationBuilder<ENTITY, ID> match(Query query) {
        aggregationOperations.add(context -> new Document("$match", query.getQueryObject()));
        return this;
    }

    /**
     * 过滤
     *
     * @param criteria
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> match(Criteria criteria) {
        aggregationOperations.add(Aggregation.match(criteria));
        return this;
    }

    /**
     * 添加字段
     *
     * @param expressions
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> addFields(String... expressions) {
        aggregationOperations.add(AggregationOperationUtils.addFields(expressions));
        return this;
    }

    /**
     * 整理字段
     *
     * @param fields
     * @return
     */
    @Deprecated
    public AggregationOperationBuilder<ENTITY, ID> project(String... fields) {
        aggregationOperations.add(AggregationOperationUtils.project(fields));
        return this;
    }

    /**
     * 复杂AggregationOperation
     *
     * @param aggregationOperations
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> complex(AggregationOperation... aggregationOperations) {
        this.aggregationOperations.addAll(Arrays.asList(aggregationOperations));
        return this;
    }

    /**
     * 原生AggregationOperation
     *
     * @param aggregationOperation
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> aggregation(AggregationOperation aggregationOperation) {
        this.aggregationOperations.add(aggregationOperation);
        return this;
    }

    /**
     * $unwind
     *
     * @param field
     * @param preserveNullAndEmptyArrays
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> unwind(String field, boolean preserveNullAndEmptyArrays) {
        this.aggregationOperations.add(Aggregation.unwind(field, preserveNullAndEmptyArrays));
        return this;
    }

    /**
     * json方式
     *
     * @param json
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> json(String json) {
        this.aggregationOperations.add(context -> Document.parse(json));
        return this;
    }

    /**
     * 去重
     *
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> distinct(String... fields) {
        /*
            {
                $group: {
                    _id: "$_id",
                    uniqueValue: {$addToSet: "$$ROOT"}
                }
            },
            {
                $unwind: "$uniqueValue"
            },
            {
                $replaceRoot: {newRoot: "$uniqueValue"}
            }

         */
        final String key = "uniqueValue";
        this.aggregationOperations.add(Aggregation.group(fields.length == 0 ? new String[]{Fields.UNDERSCORE_ID_REF} : fields).addToSet("$$ROOT").as(key));
        this.aggregationOperations.add(Aggregation.unwind(key));
        this.aggregationOperations.add(Aggregation.replaceRoot(key));
        return this;
    }

    /**
     * 数量
     *
     * @param field
     * @return
     */
    public AggregationOperationBuilder<ENTITY, ID> count(String field) {
        this.aggregationOperations.add(Aggregation.group().count().as(field));
        return this;
    }

    public <T> Optional<T> one(Class<T> outputClass) {
        return AggregationQueryHelper.aggregationOne(mongoOperations, aggregationOperations, entityClass, outputClass);
    }

    public <T> Optional<T> one(String collectionName, Class<T> outputClass) {
        return AggregationQueryHelper.aggregationOne(mongoOperations, aggregationOperations, collectionName, outputClass);
    }

    public Optional<ENTITY> one() {
        return AggregationQueryHelper.aggregationOne(mongoOperations, aggregationOperations, entityClass, entityClass);
    }

    public <T> List<T> list(Class<T> outputClass) {
        return AggregationQueryHelper.aggregationList(mongoOperations, aggregationOperations, entityClass, outputClass);
    }

    public <T> List<T> list(String collectionName, Class<T> outputClass) {
        return AggregationQueryHelper.aggregationList(mongoOperations, aggregationOperations, collectionName, outputClass);
    }

    public List<ENTITY> list() {
        return AggregationQueryHelper.aggregationList(mongoOperations, aggregationOperations, entityClass, entityClass);
    }

    public <T> Stream<T> stream(Class<T> outputClass) {
        return list(outputClass).stream();
    }

    public <T> Stream<T> stream(String collectionName, Class<T> outputClass) {
        return list(collectionName, outputClass).stream();
    }

    public Stream<ENTITY> stream() {
        return list().stream();
    }

    public <T> Page<T> pager(Pageable pageable, Class<T> outputClass) {
        return AggregationQueryHelper.aggregationPager(mongoOperations, pageable, aggregationOperations, entityClass, outputClass);
    }

    public <T> Page<T> pager(Pageable pageable, String collectionName, Class<T> outputClass) {
        return AggregationQueryHelper.aggregationPager(mongoOperations, pageable, aggregationOperations, collectionName, outputClass);
    }

    public Page<ENTITY> pager(Pageable pageable) {
        return AggregationQueryHelper.aggregationPager(mongoOperations, pageable, aggregationOperations, entityClass);
    }

    public int total(String countField) {
        return AggregationQueryHelper.aggregationCount(mongoOperations, aggregationOperations, entityClass, countField);
    }

    public int total(String collectionName, String countField) {
        return AggregationQueryHelper.aggregationCount(mongoOperations, aggregationOperations, collectionName, countField);
    }
}
