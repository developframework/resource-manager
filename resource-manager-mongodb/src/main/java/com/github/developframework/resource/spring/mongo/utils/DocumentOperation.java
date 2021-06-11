package com.github.developframework.resource.spring.mongo.utils;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

/**
 * @author qiushui on 2021-06-11.
 */
@RequiredArgsConstructor
public class DocumentOperation implements AggregationOperation {

    private final Document document;

    @Override
    public Document toDocument(AggregationOperationContext context) {
        return context.getMappedObject(document);
    }
}
