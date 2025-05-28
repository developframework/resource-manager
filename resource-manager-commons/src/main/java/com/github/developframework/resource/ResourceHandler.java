package com.github.developframework.resource;

import develop.toolkit.base.struct.TwoValues;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 资源操作接口
 *
 * @author qiushui on 2019-08-09.
 */
public interface ResourceHandler<ENTITY extends Entity<ID>, ID extends Serializable> {

    /**
     * 获取主键字段名称
     *
     * @return
     */
    String primaryKeyFieldName();

    /**
     * 获取物主字段名称
     *
     * @return
     */
    TwoValues<String, ? extends Class<?>> ownerField(String ownerType);

    /**
     * 根据ID验证存在
     *
     * @param id
     * @return
     */
    boolean existsById(ID id, OwnerInfo ownerInfo);

    /**
     * 插入资源
     *
     * @param entity
     * @return
     */
    void insert(ENTITY entity);

    /**
     * 批量插入资源
     *
     * @param entities
     * @return
     */
    void insertAll(Collection<ENTITY> entities);

    /**
     * 修改资源
     *
     * @param entity
     */
    boolean update(ENTITY entity);

    /**
     * 根据ID删除资源
     *
     * @param id
     * @return
     */
    void deleteById(ID id, OwnerInfo ownerInfo);

    /**
     * 删除资源
     *
     * @param entity
     */
    void delete(ENTITY entity);

    /**
     * 根据ID查询单个资源
     *
     * @param id
     * @return
     */
    Optional<ENTITY> queryById(ID id, OwnerInfo ownerInfo);

    /**
     * 根据ID查询单个资源（悲观锁模式）
     *
     * @param id
     * @return
     */
    Optional<ENTITY> queryByIdForUpdate(ID id, OwnerInfo ownerInfo);

    /**
     * 查询列表
     *
     * @param search
     * @return
     */
    List<ENTITY> query(Search<ENTITY> search);
}
