package com.github.developframework.resource.spring.mongodb;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.Search;
import org.springframework.data.mongodb.core.query.Query;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-21.
 */
public interface MongoSearch<ENTITY extends Entity<? extends Serializable>> extends Search<ENTITY> {

    Query toQuery();
}
