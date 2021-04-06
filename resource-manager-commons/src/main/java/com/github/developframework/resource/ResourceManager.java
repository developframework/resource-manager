package com.github.developframework.resource;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 资源管理器
 *
 * @author qiushui on 2019-07-25.
 */
@SuppressWarnings("unused")
public interface ResourceManager<ENTITY extends Entity<ID>, ID extends Serializable> {

    /**
     * 根据ID查询是否存在
     */
    boolean existsById(ID id);

    /**
     * 根据ID断言存在 不存在则抛出ResourceNotExistException
     */
    void assertExistsById(ID id);

    /**
     * 单查询
     */
    Optional<ENTITY> findOneById(ID id);

    /**
     * 单查询 不存在则抛出ResourceNotExistException
     */
    ENTITY findOneByIdRequired(ID id);

    /**
     * 添加资源流程
     */
    Optional<ENTITY> add(Object dto);

    /**
     * 根据ID修改资源流程
     */
    Optional<ENTITY> modifyById(ID id, Object dto);

    /**
     * 修改资源流程
     */
    boolean modify(Object dto, ENTITY entity);

    /**
     * 合并资源流程
     */
    Optional<ENTITY> merge(Object dto);

    /**
     * 删除资源流程
     */
    Optional<ENTITY> removeById(ID id);

    /**
     * 删除资源流程
     */
    boolean remove(ENTITY entity);

    /**
     * 按ID批量查询
     */
    List<ENTITY> listForIds(String id, ID[] ids);

    /**
     * 按ID批量查询
     */
    List<ENTITY> listForIds(String id, Collection<ID> ids);
}
