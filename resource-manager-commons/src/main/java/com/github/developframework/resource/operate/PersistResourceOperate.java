package com.github.developframework.resource.operate;

import com.github.developframework.resource.*;
import org.mapstruct.factory.Mappers;

import java.io.Serializable;

/**
 * 持久化的资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public abstract class PersistResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends ResourceOperate<ENTITY, ID> {

    protected Class<DTO> dtoClass;

    protected BasicMapper<ENTITY, DTO> mapper;

    public PersistResourceOperate(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler, Class<DTO> dtoClass) {
        super(resourceDefinition, resourceHandler);
        this.dtoClass = dtoClass;
        this.mapper = getMapper(resourceDefinition.getEntityClass());
    }

    @SuppressWarnings("unchecked")
    private BasicMapper<ENTITY, DTO> getMapper(Class<ENTITY> entityClass) {
        try {
            Class<?> mapperClass = Class.forName(entityClass.getName() + "Mapper");
            return (BasicMapper<ENTITY, DTO>) Mappers.getMapper(mapperClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
