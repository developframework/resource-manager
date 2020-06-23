package com.github.developframework.resource.spring.mongo;

import com.github.developframework.resource.Search;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-21.
 */
public interface MongoSearch<DOC extends com.github.developframework.resource.spring.mongo.DOC<? extends Serializable>> extends Search<DOC> {

    Query toQuery();
}
