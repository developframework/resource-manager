package com.github.developframework.resource.operate;

import com.github.developframework.resource.BasicMapper;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.ResourceOperate;
import org.mapstruct.factory.Mappers;

/**
 * 持久化的资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public abstract class PersistResourceOperate<ENTITY extends Entity<?>, DTO extends com.github.developframework.resource.DTO> extends ResourceOperate<ENTITY> {

    protected Class<ENTITY> entityClass;

    protected Class<DTO> dtoClass;

    protected BasicMapper<ENTITY, DTO> mapper;

    public PersistResourceOperate(ResourceHandler<ENTITY, ?> resourceHandler, Class<ENTITY> entityClass, Class<DTO> dtoClass) {
        super(resourceHandler);
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
        this.mapper = getMapper(entityClass);
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
