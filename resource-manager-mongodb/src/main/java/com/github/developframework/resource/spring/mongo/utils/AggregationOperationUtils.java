package com.github.developframework.resource.spring.mongo.utils;

import com.mongodb.BasicDBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.BasicBSONObject;
import org.bson.Document;
import org.bson.types.BasicBSONList;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Field;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Query;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author qiushui on 2018-12-25.
 */
public final class AggregationOperationUtils {

    /**
     * 添加依赖字段
     * 用于解决@DBRef字段不能在$lookup上使用$的问题
     * 参考于  https://stackoverflow.com/questions/40622714/mongo-how-to-lookup-with-dbref/41677055#41677055
     *
     * @param newFieldName
     * @param sourceFieldName
     * @param joinType
     * @return
     */
    public static AggregationOperation addRefFields(String newFieldName, String sourceFieldName, JoinType joinType) {
        return joinType == JoinType.OBJECT ? addRefFieldsForObject(newFieldName, sourceFieldName) : addRefFieldsForArray(newFieldName, sourceFieldName);
    }


    @SuppressWarnings("unchecked")
    private static AggregationOperation addRefFieldsForArray(String newFieldName, String sourceFieldName) {

        /*
            等价于：
            {
                $addFields:{
                    newFieldName: {
                        $map: {
                            input: {
                                $map: {
                                    input: "$sourceFieldName",
                                    in: {
                                        $arrayElemAt: [{$objectToArray: "$$this"}, 1]
                                    }
                                }
                            },
                            in: "$$this.v"
                        }
                    }
                }
            }
         */
        return context -> {
            BasicBSONList inList = new BasicBSONList();
            inList.add(new BasicBSONObject("$objectToArray", "$$this"));
            inList.add(1);
            BasicBSONObject addFields = new BasicBSONObject("$addFields",
                    new BasicBSONObject(newFieldName,
                            new BasicBSONObject("$map",
                                    new BasicBSONObject("input",
                                            new BasicBSONObject("$map",
                                                    new BasicBSONObject("input", "$" + sourceFieldName).append("in", new BasicDBObject("$arrayElemAt", inList))
                                            )
                                    ).append("in", "$$this.v")
                            )
                    )
            );
            return new Document(addFields.toMap());
        };
    }

    @SuppressWarnings("unchecked")
    private static AggregationOperation addRefFieldsForObject(String newFieldName, String sourceFieldName) {
        /*
            等价于：
            {
                $addFields:{
                    newFieldName: {
                        $let: {
                                vars: {
                                    myVar: {
                                        $arrayElemAt: [{$objectToArray: "$sourceFieldName"}, 1]
                                    }
                                },
                                in: "$$myVar.v"
                        }
                    }
                }
            }
         */
        return context -> {
            BasicBSONList inList = new BasicBSONList();
            inList.add(new BasicBSONObject("$objectToArray", "$" + sourceFieldName));
            inList.add(1);
            BasicBSONObject addFields = new BasicBSONObject("$addFields",
                    new BasicBSONObject(newFieldName,
                            new BasicDBObject("$let",
                                    new BasicBSONObject("vars",
                                            new BasicDBObject("myVar",
                                                    new BasicDBObject("$arrayElemAt", inList)
                                            )
                                    ).append("in", "$$myVar.v")
                            )
                    )
            );
            return new Document(addFields.toMap());
        };
    }


    /**
     * 从query中得到AggregationOperation
     *
     * @param query
     * @return
     */
    public static AggregationOperation matchForQuery(Query query) {
        return context -> new Document("$match", query.getQueryObject());
    }

    /**
     * 实现$addFields
     *
     * @param expressions
     * @return
     */
    public static AggregationOperation addFields(String... expressions) {
        return context -> {
            Document document = new Document();
            for (String expression : expressions) {
                String[] parts = expression.split(":");
                if (parts.length == 1) {
                    throw new IllegalArgumentException();
                }
                document.put(parts[0].trim(), parts[1].trim());
            }
            return new Document("$addFields", document);
        };
    }

    /**
     * 简化project的代码
     *
     * @param fieldStrs
     * @return
     */
    public static AggregationOperation project(String... fieldStrs) {
        final Field[] fields = Stream.of(fieldStrs).map(field -> {
            String[] parts = field.split(":");
            return parts.length == 1 ? Fields.field(parts[0]) : Fields.field(parts[0].trim(), parts[1].trim());
        }).toArray(Field[]::new);
        return Aggregation.project(Fields.from(fields));
    }

    /**
     * 组装AggregationOperation列表
     *
     * @param aggregationOperations
     * @return
     */
    public static List<AggregationOperation> aggregationOperations(AggregationOperation... aggregationOperations) {
        return new LinkedList<>(List.of(aggregationOperations));
    }

    /**
     *从@Document取得集合名称
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
