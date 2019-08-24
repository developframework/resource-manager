package com.github.developframework.resource.operate;

import com.github.developframework.resource.BasicMapper;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.exception.DTOCastException;

import java.io.Serializable;
import java.util.Optional;

/**
 * 添加资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public class AddResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends PersistResourceOperate<ENTITY, DTO, ID> {


    public AddResourceOperate(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler, Class<DTO> dtoClass, Class<? extends BasicMapper<ENTITY, DTO>> mapperClass) {
        super(resourceDefinition, resourceHandler, dtoClass, mapperClass);
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
     * 创建实体
     *
     * @param dto
     * @return
     */
    protected ENTITY create(DTO dto) {
        return mapper.toENTITY(dto);
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
     * 添加资源流程
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public Optional<ENTITY> addResource(Object obj) {
        if (dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            if (before(dto)) {
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
