package com.github.developframework.resource.exception;

/**
 * @author qiushui on 2019-07-25.
 */
public class ResourceExistException extends ResourceException {

    public ResourceExistException(String resourceName) {
        super(resourceName);
    }
}
