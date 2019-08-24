package com.github.developframework.resource.operate;

import com.github.developframework.resource.*;
import lombok.Getter;
import org.mapstruct.factory.Mappers;

import java.io.Serializable;

/**
 * 持久化的资源操作
 *
 * @author qiushui on 2019-08-08.
 */
@Getter
public abstract class PersistResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends ResourceOperate<ENTITY, ID> {

    protected Class<DTO> dtoClass;

    protected BasicMapper<ENTITY, DTO> mapper;

    public PersistResourceOperate(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler, Class<DTO> dtoClass, Class<? extends BasicMapper<ENTITY, DTO>> mapperClass) {
        super(resourceDefinition, resourceHandler);
        this.dtoClass = dtoClass;
        this.mapper = Mappers.getMapper(mapperClass);
    }

    /**
     * 操作之后
     *
     * @param dto
     * @param entity
     */
    public void after(Object dto, ENTITY entity) {
        // 默认无处理
    }
}
