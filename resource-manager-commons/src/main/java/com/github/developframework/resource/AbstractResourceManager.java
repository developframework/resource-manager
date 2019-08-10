package com.github.developframework.resource;

import com.github.developframework.resource.exception.ResourceNotExistException;
import com.github.developframework.resource.operate.AddResourceOperate;
import com.github.developframework.resource.operate.ModifyResourceOperate;
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

    protected ResourceDefinition<ENTITY> resourceDefinition;

    protected ResourceHandler<ENTITY, ID> resourceHandler;

    protected AddResourceOperate<ENTITY, ? extends DTO, ID> addResourceOperate;

    protected ModifyResourceOperate<ENTITY, ? extends DTO, ID> modifyResourceOperate;

    public AbstractResourceManager(ResourceDefinition<ENTITY> resourceDefinition, ResourceHandler<ENTITY, ID> resourceHandler) {
        this.resourceDefinition = resourceDefinition;
        this.resourceHandler = resourceHandler;
    }

    @Override
    public boolean existsById(ID id) {
        return resourceHandler.existsById(id);
    }

    @Override
    public void assertExistsById(ID id) {
        if (!resourceHandler.existsById(id)) {
            throw new ResourceNotExistException(resourceDefinition.getResourceName()).addParameter("id", id);
        }
    }

    /**
     * 配置添加资源操作
     *
     * @return
     */
    public <T extends DTO> AddResourceOperate<ENTITY, T, ID> configureAddResourceOperate(Class<T> dtoClass) {
        return new AddResourceOperate<>(resourceDefinition, resourceHandler, dtoClass);
    }

    /**
     * 配置修改资源操作
     *
     * @param dtoClass
     * @param <T>
     * @return
     */
    public <T extends DTO> ModifyResourceOperate<ENTITY, T, ID> configureModifyResourceOperate(Class<T> dtoClass) {
        return new ModifyResourceOperate<>(resourceDefinition, resourceHandler, dtoClass);
    }

    /**
     * 添加资源
     *
     * @param dto
     * @return
     */
    public Optional<ENTITY> add(Object dto) {
        return addResourceOperate.addResource(dto);
    }

    /**
     * 根据ID修改资源
     *
     * @param id
     * @param dto
     * @return
     */
    public boolean modifyById(ID id, Object dto) {
        return modifyResourceOperate.modifyById(dto, id);
    }

    /**
     * 根据ID单查询
     *
     * @param id
     * @return
     */
    @Override
    public Optional<ENTITY> findOneById(ID id) {
        ENTITY entity = resourceHandler.queryById(id);
//        optional.ifPresent(searchOperate::after);
        return Optional.ofNullable(entity);
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
                .resourceExistAssertBuilder(resourceDefinition.getResourceName(), resourceHandler.queryById(id))
                .addParameter("id", id)
                .returnValue();
    }
}
