package com.github.developframework.resource.operate;

import com.github.developframework.resource.*;
import com.github.developframework.resource.exception.DTOCastException;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-10.
 */
public abstract class ModifyUniqueResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends ModifyResourceOperate<ENTITY, DTO, ID> {

    @Getter
    private ModifyCheckExistsLogic<ENTITY, DTO, ID> logic;

    public ModifyUniqueResourceOperate(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler, Class<DTO> dtoClass, Class<? extends BasicMapper<ENTITY, DTO>> mapperClass) {
        super(resourceDefinition, resourceHandler, dtoClass, mapperClass);
        logic = configureCheckExistsLogic();
    }

    /**
     * 检查存在逻辑
     *
     * @return
     */
    public abstract ModifyCheckExistsLogic<ENTITY, DTO, ID> configureCheckExistsLogic();

    /**
     * 根据ID修改资源
     *
     * @param obj
     * @param id
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean modifyById(Object obj, ID id) {
        if (dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            return resourceHandler
                    .queryById(id)
                    .map(entity -> {
                        if (before(dto, entity)) {
                            if (logic.check(dto, entity)) {
                                throw logic.getResourceExistException(dto, resourceDefinition.getResourceName());
                            }
                            merge(dto, entity);
                            prepare(dto, entity);
                            boolean success = resourceHandler.update(entity);
                            after(obj, entity);
                            return success;
                        } else {
                            return false;
                        }
                    })
                    .orElse(false);
        } else {
            throw new DTOCastException();
        }
    }
}
