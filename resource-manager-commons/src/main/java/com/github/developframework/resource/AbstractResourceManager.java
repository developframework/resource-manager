package com.github.developframework.resource;

import com.github.developframework.resource.exception.ResourceNotExistException;
import com.github.developframework.resource.operate.AddResourceOperate;
import com.github.developframework.resource.utils.ResourceAssert;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;

/**
 * 抽象的资源管理器
 *
 * @author qiushui on 2019-07-25.
 */
@Getter
public abstract class AbstractResourceManager <
        ENTITY extends Entity<ID>,
        ID extends Serializable
        > implements ResourceManager<ENTITY, ID> {

    protected String resourceName;

    protected ResourceHandler<ENTITY, ID> resourceHandler;

    protected Class<ENTITY> entityClass;

    protected AddResourceOperate<ENTITY, ? extends DTO> addResourceOperate;

    public AbstractResourceManager(String resourceName, ResourceHandler<ENTITY, ID> resourceHandler, Class<ENTITY> entityClass) {
        this.resourceName = resourceName;
        this.resourceHandler = resourceHandler;
        this.entityClass = entityClass;
    }

    @Override
    public boolean existsById(ID id) {
        return resourceHandler.existsById(id);
    }

    @Override
    public void assertExistsById(ID id) {
        if (!resourceHandler.existsById(id)) {
            throw new ResourceNotExistException(resourceName).addParameter("id", id);
        }
    }

    /**
     * 配置添加资源操作
     *
     * @return
     */
    public <T extends DTO> AddResourceOperate<ENTITY, T> configureAddResourceOperate(Class<T> dtoClass) {
        return new AddResourceOperate<>(resourceHandler, entityClass, dtoClass);
    }

    /**
     * 添加资源
     *
     * @param dto
     * @return
     */
    public ENTITY add(Object dto) {
        return addResourceOperate.addResource(dto);
    }

    /**
     * 根据ID单查询
     *
     * @param id
     * @return
     */
    @Override
    public Optional<ENTITY> findOneById(ID id) {
        Optional<ENTITY> optional = resourceHandler.queryById(id);
//        optional.ifPresent(searchOperate::after);
        return optional;
    }

    /**
     * 根据ID单查询（必须有值）
     *
     * @param id
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public ENTITY findOneByIdRequired(ID id) {
        return (ENTITY) ResourceAssert
                .resourceExistAssertBuilder(resourceName, resourceHandler.queryById(id))
                .addParameter("id", id)
                .returnValue();
    }
}
