package com.github.developframework.resource.operate;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceOperate;

import java.io.Serializable;

/**
 * 查询资源操作
 *
 * @author qiushui on 2019-08-16.
 */
public class SearchResourceOperate<
        ENTITY extends Entity<ID>,
        ID extends Serializable
        > extends ResourceOperate<ENTITY, ID> {

    /**
     * 操作之后
     *
     * @param entity
     */
    public void after(ENTITY entity) {
        // 默认无处理
    }
}
