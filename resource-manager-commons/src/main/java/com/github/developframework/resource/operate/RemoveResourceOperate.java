package com.github.developframework.resource.operate;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceOperate;

import java.io.Serializable;

/**
 * 删除资源操作
 *
 * @author qiushui on 2019-08-16.
 */
public class RemoveResourceOperate<
        ENTITY extends Entity<ID>,
        ID extends Serializable
        > extends ResourceOperate<ENTITY, ID> {

    /**
     * 在删除操作的第一步
     *
     * @param entity
     */
    protected boolean before(ENTITY entity) {
        // 默认无处理
        return true;
    }

    /**
     * 删除操作之后
     *
     * @param entity
     */
    protected void after(ENTITY entity) {
        // 默认无处理
    }

    /**
     * 删除
     *
     * @param entity
     */
    public boolean removeResource(ENTITY entity) {
        if (entity.getId() != null) {
            if (before(entity)) {
                resourceHandler.delete(entity);
                after(entity);
                return true;
            }
        }
        return false;
    }

}
