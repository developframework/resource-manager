package com.github.developframework.resource;

import com.github.developframework.resource.exception.ResourceNotExistException;
import com.github.developframework.resource.operate.SearchResourceOperate;
import com.github.developframework.resource.utils.ResourceAssert;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Optional;

/**
 * 抽象的资源管理器
 *
 * @author qiushui on 2019-07-25.
 */
@Getter
@Slf4j
@SuppressWarnings("unchecked")
public abstract class AbstractResourceManager <
        ENTITY extends Entity<ID>,
        ID extends Serializable
        > implements ResourceManager<ENTITY, ID> {

    protected ResourceDefinition<ENTITY> resourceDefinition;

    protected ResourceHandler<ENTITY, ID> resourceHandler;

    protected ResourceOperateRegistry resourceOperateRegistry;

    public AbstractResourceManager(ResourceDefinition<ENTITY> resourceDefinition) {
        this.resourceDefinition = resourceDefinition;
    }

    /**
     * 根据ID查询是否存在
     *
     * @param id
     * @return
     */
    @Override
    public boolean existsById(ID id) {
        return resourceHandler.existsById(id);
    }

    /**
     * 根据ID断言存在
     *
     * @param id
     */
    @Override
    public void assertExistsById(ID id) {
        if (!resourceHandler.existsById(id)) {
            throw new ResourceNotExistException(resourceDefinition.getResourceName()).addParameter("id", id);
        }
    }

    /**
     * 添加资源流程
     *
     * @param dto
     * @return
     */
    @Override
    public Optional<ENTITY> add(Object dto) {
        return resourceOperateRegistry.getAddResourceOperate(dto.getClass()).addResource(dto);
    }

    /**
     * 修改资源流程
     *
     * @param id
     * @param dto
     * @return
     */
    @Override
    public boolean modifyById(ID id, Object dto) {
        return resourceOperateRegistry.getModifyResourceOperate(dto.getClass()).modifyById(dto, id);
    }

    /**
     * 删除资源流程
     *
     * @param id
     * @return
     */
    @Override
    public ENTITY removeById(ID id) {
        return (ENTITY) resourceOperateRegistry.getRemoveResourceOperate().removeById(id);
    }

    /**
     * 删除资源流程
     *
     * @param entity
     */
    @Override
    public void remove(ENTITY entity) {
        resourceOperateRegistry.getRemoveResourceOperate().remove(entity);
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
        return execSearchOperate(optional);
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
        ENTITY entity = (ENTITY) ResourceAssert
                .resourceExistAssertBuilder(resourceDefinition.getResourceName(), resourceHandler.queryById(id))
                .addParameter("id", id)
                .returnValue();
        return execSearchOperate(entity);
    }

    protected final Optional<ENTITY> execSearchOperate(Optional<ENTITY> optional) {
        SearchResourceOperate searchResourceOperate = resourceOperateRegistry.getSearchResourceOperate();
        if (searchResourceOperate != null) {
            optional.ifPresent(searchResourceOperate::after);
        }
        return optional;
    }

    protected final ENTITY execSearchOperate(ENTITY entity) {
        SearchResourceOperate searchResourceOperate = resourceOperateRegistry.getSearchResourceOperate();
        if (searchResourceOperate != null) {
            searchResourceOperate.after(entity);
        }
        return entity;
    }

    protected final <T extends Iterable<ENTITY>> T execSearchOperate(T entities) {
        SearchResourceOperate searchResourceOperate = resourceOperateRegistry.getSearchResourceOperate();
        if (searchResourceOperate != null) {
            entities.forEach(searchResourceOperate::after);
        }
        return entities;
    }
}
