package com.github.developframework.resource;

import java.io.Serializable;

/**
 * 资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public abstract class ResourceOperate<ENTITY extends Entity<ID>, ID extends Serializable> {

    public final ResourceOperateContext context = new ResourceOperateContext();

    protected ResourceDefinition<ENTITY> resourceDefinition;

    protected ResourceHandler<ENTITY, ID> resourceHandler;

    protected AbstractResourceManager<ENTITY, ID> manager;

    public void setManager(AbstractResourceManager<ENTITY, ID> manager) {
        this.manager = manager;
        this.resourceDefinition = manager.getResourceDefinition();
        this.resourceHandler = manager.getResourceHandler();
    }
}
