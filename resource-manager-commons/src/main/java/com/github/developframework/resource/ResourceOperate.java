package com.github.developframework.resource;

import java.io.Serializable;

/**
 * 资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public abstract class ResourceOperate<ENTITY extends Entity<ID>, ID extends Serializable> {

    protected ResourceOperateContext context = new ResourceOperateContext();

    protected ResourceDefinition<ENTITY> resourceDefinition;

    protected ResourceHandler<ENTITY, ID> resourceHandler;

    public ResourceOperate(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler) {
        this.resourceDefinition = resourceDefinition;
        this.resourceHandler = resourceHandler;
    }
}
