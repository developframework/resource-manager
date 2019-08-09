package com.github.developframework.resource.exception;

import develop.toolkit.base.struct.KeyValuePairs;
import lombok.Getter;

/**
 * @author qiushui on 2019-07-25.
 */
@Getter
public class ResourceException extends RuntimeException {

    private String resourceName;

    private KeyValuePairs<String, Object> parameters = new KeyValuePairs<>();

    public ResourceException(String resourceName) {
        this.resourceName = resourceName;
    }

    public ResourceException addParameter(String key, Object value) {
        parameters.addKeyValue(key, value);
        return this;
    }
}
