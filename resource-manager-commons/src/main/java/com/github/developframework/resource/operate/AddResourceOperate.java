package com.github.developframework.resource.operate;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.exception.DTOCastException;

/**
 * 添加资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public class AddResourceOperate<ENTITY extends Entity<?>, DTO extends com.github.developframework.resource.DTO> extends PersistResourceOperate<ENTITY, DTO> {



    public AddResourceOperate(ResourceHandler<ENTITY, ?> resourceHandler, Class<ENTITY> entityClass, Class<DTO> dtoClass) {
        super(resourceHandler, entityClass, dtoClass);
    }

    /**
     * 在添加操作的第一步
     *
     * @param dto
     */
    public boolean before(DTO dto) {
        // 默认无处理
        return true;
    }

    /**
     * 在save之前的一步
     *
     * @param dto
     * @param entity
     */
    public void prepare(DTO dto, ENTITY entity) {
        // 默认无处理
    }

    /**
     * 创建实体
     *
     * @param dto
     * @return
     */
    public ENTITY createEntity(DTO dto) {
        return mapper.toENTITY(dto);
    }

    /**
     * 添加资源流程
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public ENTITY addResource(Object obj) {
        if(dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            if (before(dto)) {
                ENTITY entity = createEntity(dto);
                prepare(dto, entity);
                after(resourceHandler.insert(entity));
                return entity;
            }
            return null;
        } else {
            throw new DTOCastException();
        }
    }
}
