package com.github.developframework.resource;

import com.github.developframework.resource.exception.UnRegisterOperateException;
import com.github.developframework.resource.operate.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 资源操作注册器
 *
 * @author qiushui on 2019-08-16.
 */
@Slf4j
public class ResourceOperateRegistry<ENTITY extends Entity<ID>, ID extends Serializable> {

    private AbstractResourceManager<ENTITY, ID> manager;

    private Map<Class<?>, AddResourceOperate<ENTITY, ?, ID>> addResourceOperateMap;

    private Map<Class<?>, ModifyResourceOperate<ENTITY, ?, ID>> modifyResourceOperateMap;

    protected RemoveResourceOperate<ENTITY, ID> removeResourceOperate;

    protected SearchResourceOperate<ENTITY, ID> searchResourceOperate;

    @Getter
    private boolean uniqueEntity;

    /**
     * 扫描Manager类返回值为ResourceOperate的方法，识别并注册
     *
     * @param manager
     */
    @SuppressWarnings("unchecked")
    public ResourceOperateRegistry(AbstractResourceManager<ENTITY, ID> manager) {
        this.manager = manager;
        Class<? extends AbstractResourceManager> managerClass = manager.getClass();
        Stream
                .of(managerClass.getDeclaredMethods())
                .filter(method -> ResourceOperate.class.isAssignableFrom(method.getReturnType()) && method.getParameterTypes().length == 0)
                .forEach(method -> {
                    method.setAccessible(true);
                    ResourceOperate<ENTITY, ID> resourceOperate;
                    try {
                        resourceOperate = (ResourceOperate<ENTITY, ID>) method.invoke(manager);
                        resourceOperate.setManager(manager);
                        if (resourceOperate instanceof AddResourceOperate) {
                            register((AddResourceOperate) resourceOperate);
                        } else if (resourceOperate instanceof ModifyResourceOperate) {
                            register((ModifyResourceOperate) resourceOperate);
                        } else if (resourceOperate instanceof RemoveResourceOperate) {
                            register((RemoveResourceOperate) resourceOperate);
                        } else if (resourceOperate instanceof SearchResourceOperate) {
                            register((SearchResourceOperate) resourceOperate);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

        // 设置默认值
        if (managerClass.isAnnotationPresent(DefaultRegister.class)) {
            DefaultRegister annotation = managerClass.getAnnotation(DefaultRegister.class);
            if (addResourceOperateMap == null) {
                AddResourceOperate<ENTITY, DTO, ID> resourceOperate = new AddResourceOperate(annotation.dtoClass(), annotation.mapperClass());
                resourceOperate.setManager(manager);
                register(resourceOperate);
            }
            if (modifyResourceOperateMap == null) {
                ModifyResourceOperate<ENTITY, DTO, ID> resourceOperate = new ModifyResourceOperate(annotation.dtoClass(), annotation.mapperClass());
                resourceOperate.setManager(manager);
                register(resourceOperate);
            }
        }
        if (removeResourceOperate == null) {
            RemoveResourceOperate<ENTITY, ID> resourceOperate = new RemoveResourceOperate<>();
            resourceOperate.setManager(manager);
            register(resourceOperate);
        }

        uniqueEntity = addResourceOperateMap != null && addResourceOperateMap.values().stream().anyMatch(operate -> operate instanceof AddUniqueResourceOperate);
    }

    /**
     * 注册添加操作
     *
     * @param addResourceOperate
     * @param <T>
     */
    public <T extends DTO> void register(AddResourceOperate<ENTITY, T, ID> addResourceOperate) {
        if (addResourceOperateMap == null) {
            addResourceOperateMap = new HashMap<>();
        }
        addResourceOperateMap.put(addResourceOperate.getDtoClass(), addResourceOperate);
        log.info("register {} for {} add operate", addResourceOperate.getDtoClass().getSimpleName(), manager.getResourceDefinition().getEntityClass().getSimpleName());
    }

    /**
     * 注册修改操作
     *
     * @param modifyResourceOperate
     * @param <T>
     */
    public <T extends DTO> void register(ModifyResourceOperate<ENTITY, T, ID> modifyResourceOperate) {
        if (modifyResourceOperateMap == null) {
            modifyResourceOperateMap = new HashMap<>();
        }
        modifyResourceOperateMap.put(modifyResourceOperate.getDtoClass(), modifyResourceOperate);
        log.info("register {} for {} modify operate", modifyResourceOperate.getDtoClass().getSimpleName(), manager.getResourceDefinition().getEntityClass().getSimpleName());
    }

    /**
     * 注册删除操作
     *
     * @param removeResourceOperate
     */
    public void register(RemoveResourceOperate<ENTITY, ID> removeResourceOperate) {
        this.removeResourceOperate = removeResourceOperate;
        log.info("register {} remove operate", manager.getResourceDefinition().getEntityClass().getSimpleName());
    }

    /**
     * 注册查询操作
     *
     * @param searchResourceOperate
     */
    public void register(SearchResourceOperate<ENTITY, ID> searchResourceOperate) {
        this.searchResourceOperate = searchResourceOperate;
        log.info("register {} search operate", manager.getResourceDefinition().getEntityClass().getSimpleName());
    }

    /**
     * 获取添加操作
     *
     * @param dtoClass
     * @return
     */
    public AddResourceOperate<ENTITY, ?, ID> getAddResourceOperate(final Class<?> dtoClass) {
        Class<?> temp = dtoClass;
        do {
            if (addResourceOperateMap != null && addResourceOperateMap.containsKey(dtoClass)) {
                return addResourceOperateMap.get(dtoClass);
            }
            temp = temp.getSuperclass();
        } while (temp != Object.class);
        throw new UnRegisterOperateException(manager.getClass(), "add", dtoClass);
    }

    /**
     * 获取修改操作
     *
     * @param dtoClass
     * @return
     */
    public ModifyResourceOperate<ENTITY, ?, ID> getModifyResourceOperate(final Class<?> dtoClass) {
        Class<?> temp = dtoClass;
        do {
            if (modifyResourceOperateMap != null && modifyResourceOperateMap.containsKey(dtoClass)) {
                return modifyResourceOperateMap.get(dtoClass);
            }
            temp = temp.getSuperclass();
        } while (temp != Object.class);
        throw new UnRegisterOperateException(manager.getClass(), "modify", dtoClass);
    }

    /**
     * 获取删除操作
     *
     * @return
     */
    public RemoveResourceOperate<ENTITY, ID> getRemoveResourceOperate() {
        if (removeResourceOperate != null) {
            return removeResourceOperate;
        }
        throw new UnRegisterOperateException(manager.getClass(), "remove");
    }

    /**
     * 获取查询操作
     *
     * @return
     */
    public SearchResourceOperate<ENTITY, ID> getSearchResourceOperate() {
        return searchResourceOperate;
    }
}
