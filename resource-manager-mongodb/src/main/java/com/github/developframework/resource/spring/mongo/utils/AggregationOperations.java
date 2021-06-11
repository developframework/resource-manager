package com.github.developframework.resource.spring.mongo.utils;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author qiushui on 2018-12-25.
 */
public final class AggregationOperations {

    /**
     * 从query中得到AggregationOperation
     *
     * @param query
     * @return
     */
    public static AggregationOperation matchForQuery(Query query) {
        return new DocumentOperation(new Document("$match", query.getQueryObject()));
    }

    /**
     * 把数组的第一个元素移出
     *
     * @param arrayField
     * @param as
     * @return
     */
    public static AggregationOperation addFieldsForArrayFirst(String arrayField, String as) {
        return Aggregation
                .addFields()
                .addFieldWithValue(
                        as,
                        ArrayOperators.ArrayElemAt.arrayOf(arrayField).elementAt(0)
                )
                .build();
    }

    /**
     * lookup关联DBRef字段
     *
     * @param from
     * @param dbRefField
     * @param lookupAs
     * @return
     */
    public static AggregationOperation lookupDBRef(String from, String dbRefField, String lookupAs) {
        return buildForMap(
                Map.of(
                        "$lookup",
                        Map.of(
                                "from", from,
                                "localField", dbRefField + ".$id",
                                "foreignField", Fields.UNDERSCORE_ID,
                                "as", lookupAs
                        )
                )
        );
    }

    /**
     * 通过Map构建AggregationOperation
     *
     * @param map
     * @return
     */
    public static AggregationOperation buildForMap(Map<String, Object> map) {
        return new DocumentOperation(new Document(map));
    }

    /**
     * 通过Map构建AggregationOperation
     *
     * @param json
     * @return
     */
    public static AggregationOperation buildForJson(String json) {
        return new DocumentOperation(Document.parse(json));
    }

    /**
     * 简化project的代码
     *
     * @param fieldStrs
     * @return
     */
    public static AggregationOperation project(String... fieldStrs) {
        return Aggregation.project(
                Fields.from(
                        Stream
                                .of(fieldStrs)
                                .map(field -> {
                                    String[] parts = field.split(":");
                                    return parts.length == 1 ? Fields.field(parts[0]) : Fields.field(parts[0].trim(), parts[1].trim());
                                })
                                .toArray(Field[]::new)
                )
        );
    }

    /**
     * 组装AggregationOperation列表
     *
     * @param aggregationOperations
     * @return
     */
    public static List<AggregationOperation> list(AggregationOperation... aggregationOperations) {
        return new LinkedList<>(List.of(aggregationOperations));
    }

    /**
     * 从@Document取得集合名称
     *
     * @param docClass
     * @return
     */
    protected static String collectionNameFormDocumentAnnotation(Class<?> docClass) {
        org.springframework.data.mongodb.core.mapping.Document annotation = AnnotationUtils.findAnnotation(docClass, org.springframework.data.mongodb.core.mapping.Document.class);
        if (annotation != null) {
            String collectionName = annotation.collection();
            if (StringUtils.isNotBlank(collectionName)) {
                return collectionName;
            }
        }
        throw new IllegalStateException();
    }
}
