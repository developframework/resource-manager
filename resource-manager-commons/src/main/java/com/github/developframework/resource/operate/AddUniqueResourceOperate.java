package com.github.developframework.resource.operate;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.exception.ResourceExistException;
import develop.toolkit.base.utils.JavaBeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * 添加唯一资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public abstract class AddUniqueResourceOperate<ENTITY extends Entity<?>, DTO extends com.github.developframework.resource.DTO> extends AddResourceOperate<ENTITY, DTO> {

    public AddUniqueResourceOperate(ResourceHandler<ENTITY, ?> resourceHandler, Class<ENTITY> entityClass, Class<DTO> dtoClass) {
        super(resourceHandler, entityClass, dtoClass);
    }

    /**
     * 检查存在逻辑
     *
     * @param dto
     * @return
     */
    public abstract boolean checkExistsLogic(DTO dto);

    /**
     * 获取资源存在异常
     *
     * @param resourceName
     * @param dto
     */
    public ResourceExistException getResourceExistException(String resourceName, DTO dto) {
        ResourceExistException e = new ResourceExistException(resourceName);
        for (String field : checkExistsFailedReason()) {
            try {
                Object value = FieldUtils.readDeclaredField(dto, field, true);
                e.addParameter(JavaBeanUtils.camelcaseToUnderline(field), value);
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            }
        }
        return e;
    }

    /**
     * 检查重复失败原因字段
     *
     * @return
     */
    protected String[] checkExistsFailedReason() {
        return new String[0];
    }

    /**
     * 添加资源流程
     *
     * @param dto
     * @return
     */
    public ENTITY addResource(DTO dto) {
        if (before(dto)) {
            ENTITY entity = createEntity(dto);
            prepare(dto, entity);
            after(resourceHandler.insert(entity));
            return entity;
        }
        return null;
    }
}
