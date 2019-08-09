package com.github.developframework.resource.utils;

import com.github.developframework.resource.exception.ResourceException;

/**
 * @author qiushui on 2018-10-12.
 */
public class ResourceAssertBuilder {

    private ResourceException resourceException;

    public ResourceAssertBuilder() {
    }

    public ResourceAssertBuilder(ResourceException resourceException) {
        this.resourceException = resourceException;
    }

    /**
     * 添加原因
     *
     * @param key
     * @param value
     * @return
     */
    public ResourceAssertBuilder addParameter(String key, Object value) {
        if (resourceException != null) {
            resourceException.addParameter(key, value);
        }
        return this;
    }

    /**
     * 生成
     *
     * @return
     */
    public void over() {
        if (resourceException != null) {
            throw resourceException;
        }
    }
}
