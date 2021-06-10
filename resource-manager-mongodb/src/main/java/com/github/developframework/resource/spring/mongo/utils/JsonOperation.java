package com.github.developframework.resource.spring.mongo.utils;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;

/**
 * @author qiushui on 2021-06-10.
 */
@RequiredArgsConstructor
class JsonOperation implements AggregationOperation {

    private final String json;

    @Override
    public Document toDocument(AggregationOperationContext context) {
        return context.getMappedObject(Document.parse(json));
    }
}
