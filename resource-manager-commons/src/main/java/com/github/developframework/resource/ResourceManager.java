package com.github.developframework.resource;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 资源管理器
 *
 * @author qiushui on 2019-07-25.
 */
public interface ResourceManager<ENTITY extends Entity<ID>, ID extends Serializable> {

    /**
     * 根据ID查询是否存在
     *
     * @param id
     * @return
     */
    boolean existsById(ID id);

    /**
     * 根据ID断言存在
     *
     * @param id
     */
    void assertExistsById(ID id);

    /**
     * 单查询
     *
     * @param id
     * @return
     */
    Optional<ENTITY> findOneById(ID id);

    /**
     * 单查询 不存在则抛出ResourceNotExistException
     *
     * @param id
     * @return
     */
    ENTITY findOneByIdRequired(ID id);

    /**
     * 添加资源流程
     *
     * @param dto
     * @return
     */
    Optional<ENTITY> add(Object dto);

    /**
     * 修改资源流程
     *
     * @param id
     * @param dto
     * @return
     */
    boolean modifyById(ID id, Object dto);

    /**
     * 删除资源流程
     *
     * @param id
     * @return
     */
    ENTITY removeById(ID id);

    /**
     * 删除资源流程
     *
     * @param entity
     */
    void remove(ENTITY entity);

    /**
     * 按ID批量查询
     *
     * @param ids
     * @return
     */
    List<ENTITY> listForIds(ID[] ids);

    /**
     * 查询全部
     *
     * @param search
     * @return
     */
    <SEARCH extends Search<ENTITY>> List<ENTITY> list(SEARCH search);
}
