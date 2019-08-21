package com.github.developframework.resource.operate;

import com.github.developframework.resource.CheckExistsLogic;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.exception.DTOCastException;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-10.
 */
public abstract class ModifyUniqueResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends ModifyResourceOperate<ENTITY, DTO, ID> {

    private CheckExistsLogic<ENTITY, DTO, ID> logic;

    public ModifyUniqueResourceOperate(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler, Class<DTO> dtoClass) {
        super(resourceDefinition, resourceHandler, dtoClass);
        logic = configureCheckExistsLogic();
    }

    /**
     * 检查存在逻辑
     *
     * @return
     */
    abstract CheckExistsLogic<ENTITY, DTO, ID> configureCheckExistsLogic();

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
            if (before(dto)) {
                return resourceHandler
                        .queryById(id)
                        .map(entity -> {
                            if (logic.check(resourceHandler, dto)) {
                                throw logic.getResourceExistException(resourceDefinition.getResourceName());
                            }
                            merge(dto, entity);
                            prepare(dto, entity);
                            boolean success = resourceHandler.update(entity);
                            after(obj, entity);
                            return success;
                        })
                        .orElse(false);
            }
            return false;
        } else {
            throw new DTOCastException();
        }
    }
}
