package com.github.developframework.resource;

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
     * 根据ID验证存在
     *
     * @param id
     * @return
     */
    boolean existsById(ID id);

    /**
     * 插入资源
     *
     * @param entity
     * @return
     */
    ENTITY insert(ENTITY entity);

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
    void deleteById(ID id);

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
    Optional<ENTITY> queryById(ID id);

    /**
     * 查询列表
     *
     * @param search
     * @return
     */
    List<ENTITY> query(Search<ENTITY> search);
}
