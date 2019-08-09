package com.github.developframework.resource;

import lombok.Getter;

/**
 * 资源操作
 *
 * @author qiushui on 2019-08-08.
 */
@Getter
public abstract class ResourceOperate<ENTITY extends Entity<?>> {

    protected ResourceOperateContext context = new ResourceOperateContext();

    protected ResourceHandler<ENTITY, ?> resourceHandler;

    public ResourceOperate(ResourceHandler<ENTITY, ?> resourceHandler) {
        this.resourceHandler = resourceHandler;
    }

    /**
     * 操作之后
     *
     * @param entity
     */
    public void after(ENTITY entity) {
        // 默认无处理
    }
}
