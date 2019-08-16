package com.github.developframework.resource;

import com.github.developframework.resource.exception.UnRegisterOperateException;
import com.github.developframework.resource.operate.AddResourceOperate;
import com.github.developframework.resource.operate.ModifyResourceOperate;
import com.github.developframework.resource.operate.RemoveResourceOperate;
import com.github.developframework.resource.operate.SearchResourceOperate;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

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
     * 扫描Manager类带@RegisterOperate注解的方法，识别并注册
     *
     * @param entityClass
     * @param manager
     */
    @SuppressWarnings("unchecked")
    ResourceOperateRegistry(Class<ENTITY> entityClass, AbstractResourceManager manager) {
        this.entityClass = entityClass;
        MethodUtils
                .getMethodsListWithAnnotation(manager.getClass(), RegisterOperate.class)
                .stream()
                .filter(method -> ResourceOperate.class.isAssignableFrom(method.getReturnType()) && method.getParameterTypes().length == 0)
                .forEach(method -> {
                    Class<? extends DTO> dtoClass = method.getAnnotation(RegisterOperate.class).value();
                    ResourceOperate<ENTITY, ID> resourceOperate;
                    try {
                        resourceOperate = (ResourceOperate<ENTITY, ID>) method.invoke(manager);
                        if (resourceOperate instanceof AddResourceOperate) {
                            register(dtoClass, (AddResourceOperate) resourceOperate);
                        } else if (resourceOperate instanceof ModifyResourceOperate) {
                            register(dtoClass, (ModifyResourceOperate) resourceOperate);
                        } else if (resourceOperate instanceof RemoveResourceOperate) {
                            register((RemoveResourceOperate) resourceOperate);
                        } else if (resourceOperate instanceof SearchResourceOperate) {
                            register((SearchResourceOperate) resourceOperate);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 注册添加操作
     *
     * @param dtoClass
     * @param addResourceOperate
     * @param <T>
     */
    public <T extends DTO> void register(Class<T> dtoClass, AddResourceOperate<ENTITY, T, ID> addResourceOperate) {
        if (addResourceOperateMap == null) {
            addResourceOperateMap = new HashMap<>();
        }
        addResourceOperateMap.put(dtoClass, addResourceOperate);
    }

    /**
     * 注册修改操作
     *
     * @param dtoClass
     * @param modifyResourceOperate
     * @param <T>
     */
    public <T extends DTO> void register(Class<T> dtoClass, ModifyResourceOperate<ENTITY, T, ID> modifyResourceOperate) {
        if (modifyResourceOperateMap == null) {
            modifyResourceOperateMap = new HashMap<>();
        }
        modifyResourceOperateMap.put(dtoClass, modifyResourceOperate);
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
