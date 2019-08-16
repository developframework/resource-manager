package com.github.developframework.resource;

import com.github.developframework.resource.exception.ResourceExistException;

import java.io.Serializable;

/**
 * 添加资源检查存在逻辑接口
 *
 * @author qiushui on 2019-08-09.
 */
public interface CheckExistsLogic<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > {

    /**
     * 检查资源存在
     *
     * @param resourceHandler
     * @param dto
     * @return
     */
    boolean check(ResourceHandler<ENTITY, ID> resourceHandler, DTO dto);

    /**
     * 获取资源存在异常
     *
     * @param resourceName
     */
    default ResourceExistException getResourceExistException(String resourceName) {
        return new ResourceExistException(resourceName);
    }
}
