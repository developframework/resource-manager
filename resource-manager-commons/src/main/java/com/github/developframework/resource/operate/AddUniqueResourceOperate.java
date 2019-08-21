package com.github.developframework.resource.operate;

import com.github.developframework.resource.CheckExistsLogic;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.exception.DTOCastException;

import java.io.Serializable;
import java.util.Optional;

/**
 * 添加唯一资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public abstract class AddUniqueResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends AddResourceOperate<ENTITY, DTO, ID> {

    private CheckExistsLogic<ENTITY, DTO, ID> logic;

    public AddUniqueResourceOperate(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler, Class<DTO> dtoClass) {
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
     * 添加资源流程
     *
     * @param obj
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<ENTITY> addResource(Object obj) {
        if (dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            if (before(dto)) {
                if (logic.check(resourceHandler, dto)) {
                    throw logic.getResourceExistException(resourceDefinition.getResourceName());
                }
                ENTITY entity = create(dto);
                prepare(dto, entity);
                after(obj, resourceHandler.insert(entity));
                return Optional.of(entity);
            }
            return Optional.empty();
        } else {
            throw new DTOCastException();
        }
    }
}
