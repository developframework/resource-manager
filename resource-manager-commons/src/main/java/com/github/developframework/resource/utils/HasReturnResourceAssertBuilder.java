package com.github.developframework.resource.utils;

import com.github.developframework.resource.exception.ResourceException;

/**
 * @author qiushui on 2018-10-12.
 * @since 0.1
 */
public class HasReturnResourceAssertBuilder<T> {

    private T object;

    private ResourceException businessException;

    public HasReturnResourceAssertBuilder(T object) {
        this.object = object;
    }

    public HasReturnResourceAssertBuilder(ResourceException businessException) {
        this.businessException = businessException;
    }

    /**
     * 添加原因参数
     *
     * @param parameter
     * @param value
     * @return
     */
    public HasReturnResourceAssertBuilder addParameter(String parameter, Object value) {
        if (businessException != null) {
            businessException.addParameter(parameter, value);
        }
        return this;
    }

    /**
     * 生成
     *
     * @return
     */
    public T returnValue() {
        if (businessException != null) {
            throw businessException;
        }
        return object;
    }
}
