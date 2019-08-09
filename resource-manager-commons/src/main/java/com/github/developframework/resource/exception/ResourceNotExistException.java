package com.github.developframework.resource.exception;

/**
 * @author qiushui on 2019-07-25.
 */
public class ResourceNotExistException extends ResourceException {

    public ResourceNotExistException(String resourceName) {
        super(resourceName);
    }
}
