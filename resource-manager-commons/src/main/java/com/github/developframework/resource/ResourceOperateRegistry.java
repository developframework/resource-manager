package com.github.developframework.resource;

import com.github.developframework.resource.exception.UnRegisterOperateException;
import com.github.developframework.resource.operate.AddResourceOperate;
import com.github.developframework.resource.operate.ModifyResourceOperate;
import com.github.developframework.resource.operate.RemoveResourceOperate;
import com.github.developframework.resource.operate.SearchResourceOperate;

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
public class ResourceOperateRegistry<ENTITY extends Entity<ID>, ID extends Serializable> {

    private Class<ENTITY> entityClass;

    private Map<Class<?>, AddResourceOperate<ENTITY, ?, ID>> addResourceOperateMap;

    private Map<Class<?>, ModifyResourceOperate<ENTITY, ?, ID>> modifyResourceOperateMap;

    protected RemoveResourceOperate<ENTITY, ID> removeResourceOperate;

    protected SearchResourceOperate<ENTITY, ID> searchResourceOperate;

    /**
     * 扫描Manager类返回值为ResourceOperate的方法，识别并注册
     *
     * @param entityClass
     * @param manager
     */
    @SuppressWarnings("unchecked")
    public ResourceOperateRegistry(Class<ENTITY> entityClass, AbstractResourceManager manager) {
        this.entityClass = entityClass;
        Stream
                .of(manager.getClass().getDeclaredMethods())
                .filter(method -> ResourceOperate.class.isAssignableFrom(method.getReturnType()) && method.getParameterTypes().length == 0)
                .forEach(method -> {
                    method.setAccessible(true);
                    ResourceOperate<ENTITY, ID> resourceOperate;
                    try {
                        resourceOperate = (ResourceOperate<ENTITY, ID>) method.invoke(manager);
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
    }

    /**
     * 注册查询操作
     *
     * @param removeResourceOperate
     */
    public void register(RemoveResourceOperate<ENTITY, ID> removeResourceOperate) {
        this.removeResourceOperate = removeResourceOperate;
    }

    /**
     * 注册查询操作
     *
     * @param searchResourceOperate
     */
    public void register(SearchResourceOperate<ENTITY, ID> searchResourceOperate) {
        this.searchResourceOperate = searchResourceOperate;
    }

    /**
     * 获取添加操作
     *
     * @param dtoClass
     * @return
     */
    public AddResourceOperate<ENTITY, ?, ID> getAddResourceOperate(Class<?> dtoClass) {
        if (addResourceOperateMap.containsKey(dtoClass)) {
            return addResourceOperateMap.get(dtoClass);
        }
        throw new UnRegisterOperateException(entityClass, "add", dtoClass);
    }

    /**
     * 获取修改操作
     *
     * @param dtoClass
     * @return
     */
    public ModifyResourceOperate<ENTITY, ?, ID> getModifyResourceOperate(Class<?> dtoClass) {
        if (addResourceOperateMap.containsKey(dtoClass)) {
            return modifyResourceOperateMap.get(dtoClass);
        }
        throw new UnRegisterOperateException(entityClass, "modify", dtoClass);
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
        throw new UnRegisterOperateException(entityClass, "remove");
    }

    /**
     * 获取查询操作
     *
     * @return
     */
    public SearchResourceOperate<ENTITY, ID> getSearchResourceOperate() {
        if (searchResourceOperate != null) {
            return searchResourceOperate;
        }
        throw new UnRegisterOperateException(entityClass, "search");
    }
}
