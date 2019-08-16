package com.github.developframework.resource.operate;

import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.exception.DTOCastException;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-10.
 */
public class ModifyResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends PersistResourceOperate<ENTITY, DTO, ID> {

    public ModifyResourceOperate(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler, Class<DTO> dtoClass) {
        super(resourceDefinition, resourceHandler, dtoClass);
    }

    /**
     * 在添加操作的第一步
     *
     * @param dto
     */
    protected boolean before(DTO dto) {
        // 默认无处理
        return true;
    }

    /**
     * 在save之前的一步
     *
     * @param dto
     * @param entity
     */
    protected void prepare(DTO dto, ENTITY entity) {
        // 默认无处理
    }

    /**
     * 数据合并
     *
     * @param dto
     * @param entity
     * @return
     */
    public void merge(DTO dto, ENTITY entity) {
        mapper.toENTITY(dto, entity);
    }

    /**
     * 根据ID修改资源
     *
     * @param obj
     * @param id
     */
    @SuppressWarnings("unchecked")
    public boolean modifyById(Object obj, ID id) {
        if (dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            if (before(dto)) {
                return resourceHandler
                        .queryById(id)
                        .map(entity -> {
                            merge(dto, entity);
                            prepare(dto, entity);
                            boolean success = resourceHandler.update(entity);
                            after(obj, entity);
                            return success;
                        }).orElse(false);
            }
            return false;
        } else {
            throw new DTOCastException();
        }
    }
}
