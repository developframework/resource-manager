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

    protected ResourceOperateRegistry<ENTITY, ID> resourceOperateRegistry;

    protected OwnerProvider ownerProvider;

    public AbstractResourceManager(ResourceDefinition<ENTITY> resourceDefinition) {
        this.resourceDefinition = resourceDefinition;
    }

    /**
     * 根据ID查询是否存在
     */
    @Override
    public boolean existsById(ID id) {
        return resourceHandler.existsById(id, ownerProvider.provide());
    }

    /**
     * 根据ID断言存在
     */
    @Override
    public void assertExistsById(ID id) {
        if (!resourceHandler.existsById(id, ownerProvider.provide())) {
            throw new ResourceNotExistException(resourceDefinition.getResourceName()).addParameter("id", id);
        }
    }

    /**
     * 添加资源流程
     */
    @Override
    public Optional<ENTITY> add(Object dto) {
        return resourceOperateRegistry.getAddResourceOperate(dto.getClass()).addResource(dto);
    }

    /**
     * 根据ID修改资源流程
     */
    @Override
    public Optional<ENTITY> modifyById(ID id, Object dto) {
        final Optional<ENTITY> optional = findOneById(id);
        optional.ifPresent(entity -> modify(dto, entity));
        return optional;
    }

    /**
     * 修改资源流程
     */
    @Override
    public boolean modify(Object dto, ENTITY entity) {
        return resourceOperateRegistry.getModifyResourceOperate(dto.getClass()).modifyResource(dto, entity);
    }

    /**
     * 合并资源流程
     */
    @Override
    public Optional<ENTITY> merge(Object dto) {
        return resourceOperateRegistry.getMergeResourceOperate(dto.getClass()).mergeResource(dto);
    }

    /**
     * 删除资源流程
     */
    @Override
    public Optional<ENTITY> removeById(ID id) {
        final Optional<ENTITY> optional = findOneById(id);
        optional.ifPresent(this::remove);
        return optional;
    }

    /**
     * 删除资源流程
     */
    @Override
    public boolean remove(ENTITY entity) {
        return resourceOperateRegistry.getRemoveResourceOperate().removeResource(entity);
    }

    /**
     * 根据ID单查询
     */
    @Override
    public Optional<ENTITY> findOneById(ID id) {
        return resourceHandler.queryById(id, ownerProvider.provide()).map(this::execSearchOperate);
    }

    /**
     * 根据ID单查询（必须有值）
     */
    @Override
    @SuppressWarnings("unchecked")
    public ENTITY findOneByIdRequired(ID id) {
        return (ENTITY) ResourceAssert
                .resourceExistAssertBuilder(resourceDefinition.getResourceName(), findOneById(id))
                .addParameter("id", id)
                .returnValue();
    }

    protected final ENTITY execSearchOperate(ENTITY entity) {
        SearchResourceOperate<ENTITY, ID> searchResourceOperate = resourceOperateRegistry.getSearchResourceOperate();
        if (searchResourceOperate != null) {
            searchResourceOperate.after(entity);
        }
        return entity;
    }

    protected final <T extends Iterable<ENTITY>> T execSearchOperate(T entities) {
        SearchResourceOperate<ENTITY, ID> searchResourceOperate = resourceOperateRegistry.getSearchResourceOperate();
        if (searchResourceOperate != null) {
            entities.forEach(searchResourceOperate::after);
        }
        return entities;
    }

    public abstract <T extends DTO> AddCheckExistsLogic<ENTITY, T, ID> byFieldAddCheck(Class<T> dtoClass, String... fields);

    public abstract <T extends DTO> ModifyCheckExistsLogic<ENTITY, T, ID> byFieldModifyCheck(Class<T> dtoClass, String... fields);
}
