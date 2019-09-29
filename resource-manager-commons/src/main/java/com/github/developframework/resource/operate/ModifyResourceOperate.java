package com.github.developframework.resource.operate;

import com.github.developframework.resource.BasicMapper;
import com.github.developframework.resource.Entity;
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

    public ModifyResourceOperate(Class<DTO> dtoClass, Class<? extends BasicMapper<ENTITY, DTO>> mapperClass) {
        super(dtoClass, mapperClass);
    }

    /**
     * 在添加操作的第一步
     *
     * @param dto
     * @param entity
     */
    protected boolean before(DTO dto, ENTITY entity) {
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
    protected void merge(DTO dto, ENTITY entity) {
        if (mapper != null) {
            mapper.toENTITY(dto, entity);
        }
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
            return resourceHandler
                    .queryById(id)
                    .map(entity -> {
                        if (before(dto, entity)) {
                            merge(dto, entity);
                            prepare(dto, entity);
                            boolean success = resourceHandler.update(entity);
                            after(dto, entity);
                            return success;
                        } else {
                            return false;
                        }
                    }).orElse(false);
        } else {
            throw new DTOCastException();
        }
    }

    /**
     * 修改资源
     *
     * @param obj
     * @param entity
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean modify(Object obj, ENTITY entity) {
        if (entity.getId() == null) {
            return false;
        }
        if (dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            if (before(dto, entity)) {
                merge(dto, entity);
                prepare(dto, entity);
                boolean success = resourceHandler.update(entity);
                after(dto, entity);
                return success;
            } else {
                return false;
            }
        } else {
            throw new DTOCastException();
        }
    }
}
