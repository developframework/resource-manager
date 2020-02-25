package com.github.developframework.resource.spring.mongo.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * aggregate方式查询助手
 *
 * @author qiushui on 2019-01-15.
 */
public final class AggregationQueryHelper {

    /**
     * 按照aggregate方式查询单个
     *
     * @param mongoOperations
     * @param aggregationOperations
     * @param collectionName
     * @param outputClass
     * @param <T>
     * @return
     */
    public static <T> Optional<T> aggregationOne(MongoOperations mongoOperations, List<AggregationOperation> aggregationOperations, String collectionName, Class<T> outputClass) {
        List<T> list = aggregationList(mongoOperations, aggregationOperations, collectionName, outputClass);
        return Optional.ofNullable(list.isEmpty() ? null : list.get(0));
    }

    /**
     * 按照aggregate方式查询单个
     *
     * @param mongoOperations
     * @param aggregationOperations
     * @param docClass
     * @param outputClass
     * @param <T>
     * @return
     */
    public static <T> Optional<T> aggregationOne(MongoOperations mongoOperations, List<AggregationOperation> aggregationOperations, Class<?> docClass, Class<T> outputClass) {
        List<T> list = aggregationList(mongoOperations, aggregationOperations, docClass, outputClass);
        return Optional.ofNullable(list.isEmpty() ? null : list.get(0));
    }

    /**
     * 按照aggregate方式查询列表
     *
     * @param mongoOperations
     * @param aggregationOperations
     * @param collectionName
     * @param outputClass
     * @param <T>
     * @return
     */
    public static <T> List<T> aggregationList(MongoOperations mongoOperations, List<AggregationOperation> aggregationOperations, String collectionName, Class<T> outputClass) {
        return mongoOperations.aggregate(
                Aggregation.newAggregation(aggregationOperations),
                collectionName,
                outputClass
        ).getMappedResults();
    }

    /**
     * 按照aggregate方式查询列表
     *
     * @param mongoOperations
     * @param aggregationOperations
     * @param docClass
     * @param outputClass
     * @param <T>
     * @return
     */
    public static <T> List<T> aggregationList(MongoOperations mongoOperations, List<AggregationOperation> aggregationOperations, Class<?> docClass, Class<T> outputClass) {
        String collectionName = AggregationOperationUtils.collectionNameFormDocumentAnnotation(docClass);
        return aggregationList(mongoOperations, aggregationOperations, collectionName, outputClass);
    }

    /**
     * 按照aggregate方式查询分页
     *
     * @param mongoOperations
     * @param pageable
     * @param aggregationOperations
     * @param collectionName
     * @param outputClass
     * @param <T>
     * @return
     */
    public static <T> Page<T> aggregationPager(MongoOperations mongoOperations, Pageable pageable, List<AggregationOperation> aggregationOperations, String collectionName, Class<T> outputClass) {

        // 查询总条数
        List<AggregationOperation> queryCountAggregationOperation = new LinkedList<>(aggregationOperations);
        queryCountAggregationOperation.add(Aggregation.count().as("count"));
        final AggregationResults<Map> countResults = mongoOperations.aggregate(
                Aggregation.newAggregation(queryCountAggregationOperation),
                collectionName,
                Map.class
        );

        // 查询列表
        List<AggregationOperation> queryListAggregationOperation = new LinkedList<>(aggregationOperations);
        queryListAggregationOperation.add(context -> Aggregation.sort(pageable.getSort()).toDocument(Aggregation.DEFAULT_CONTEXT));
        queryListAggregationOperation.add(Aggregation.skip(pageable.getOffset()));
        queryListAggregationOperation.add(Aggregation.limit(pageable.getPageSize()));
        AggregationResults<T> results = mongoOperations.aggregate(
                Aggregation.newAggregation(queryListAggregationOperation),
                collectionName,
                outputClass
        );

        // 构建Page
        return PageableExecutionUtils.getPage(results.getMappedResults(), pageable, () -> {
            Map uniqueMappedResult = countResults.getUniqueMappedResult();
            return uniqueMappedResult == null ? 0 : (Integer) uniqueMappedResult.get("count");
        });
    }

    /**
     * 按照aggregate方式查询分页
     *
     * @param mongoOperations
     * @param pageable
     * @param aggregationOperations
     * @param docClass
     * @param outputClass
     * @param <T>
     * @return
     */
    public static <T> Page<T> aggregationPager(MongoOperations mongoOperations, Pageable pageable, List<AggregationOperation> aggregationOperations, Class<?> docClass, Class<T> outputClass) {
        String collectionName = AggregationOperationUtils.collectionNameFormDocumentAnnotation(docClass);
        return aggregationPager(mongoOperations, pageable, aggregationOperations, collectionName, outputClass);
    }

    /**
     * 按照aggregate方式查询分页
     *
     * @param mongoOperations
     * @param pageable
     * @param aggregationOperations
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Page<T> aggregationPager(MongoOperations mongoOperations, Pageable pageable, List<AggregationOperation> aggregationOperations, Class<T> clazz) {
        String collectionName = AggregationOperationUtils.collectionNameFormDocumentAnnotation(clazz);
        return aggregationPager(mongoOperations, pageable, aggregationOperations, collectionName, clazz);
    }

    /**
     * 按照aggregate方式查询数量
     *
     * @param mongoOperations
     * @param aggregationOperations
     * @param collectionName
     * @param countField
     * @return
     */
    public static int aggregationCount(MongoOperations mongoOperations, List<AggregationOperation> aggregationOperations, String collectionName, String countField) {
        return aggregationOne(mongoOperations, aggregationOperations, collectionName, Map.class)
                .map(map -> (Integer) map.get(countField)).orElse(0);
    }

    /**
     * 按照aggregate方式查询数量
     *
     * @param mongoOperations
     * @param aggregationOperations
     * @param docClass
     * @param countField
     * @return
     */
    public static int aggregationCount(MongoOperations mongoOperations, List<AggregationOperation> aggregationOperations, Class<?> docClass, String countField) {
        String collectionName = AggregationOperationUtils.collectionNameFormDocumentAnnotation(docClass);
        return aggregationCount(mongoOperations, aggregationOperations, collectionName, countField);
    }

}
