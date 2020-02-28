package com.github.developframework.resource.spring.mongo;

import com.github.developframework.resource.Entity;
import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-21.
 */
public interface DOC<ID extends Serializable> extends Entity<ID> {

    default ObjectId getObjectId() {
        final ID id = getId();
        if (id == null) {
            return null;
        } else if (id instanceof String) {
            return new ObjectId((String) id);
        } else if (id instanceof ObjectId) {
            return (ObjectId) id;
        } else {
            throw new IllegalArgumentException("Can not convert to ObjectId");
        }
    }
}
