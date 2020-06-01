package com.github.developframework.resource.spring.mybatis;

import develop.toolkit.base.struct.KeyValuePairs;
import develop.toolkit.base.struct.TwoValues;
import org.apache.ibatis.annotations.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 基础Mapper
 *
 * @author qiushui on 2020-05-28.
 */
public interface BaseDaoMapper<PO extends MPO<ID>, ID extends Serializable> {

    Class<PO> getEntityClass();

    @UpdateProvider(type = BaseMapperMysqlProvider.class, method = "createTable")
    void createTable(Class<PO> entityClass);

    @InsertProvider(type = BaseMapperMysqlProvider.class, method = "insert")
    void insert(PO entity);

    @InsertProvider(type = BaseMapperMysqlProvider.class, method = "insertAll")
    void insertAll(@Param("entityClass") Class<PO> entityClass, @Param("entities") Collection<PO> entities);

    @UpdateProvider(type = BaseMapperMysqlProvider.class, method = "update")
    boolean update(PO entity);

    @DeleteProvider(type = BaseMapperMysqlProvider.class, method = "deleteById")
    void deleteById(@Param("entityClass") Class<PO> entityClass, @Param("id") ID id);

    @SelectProvider(type = BaseMapperMysqlProvider.class, method = "existsById")
    boolean existsById(@Param("entityClass") Class<PO> entityClass, @Param("id") ID id);

    @SelectProvider(type = BaseMapperMysqlProvider.class, method = "existsById")
    boolean existsByFields(@Param("entityClass") Class<PO> entityClass, @Param("fields") KeyValuePairs<String, Object> fields);

    @SelectProvider(type = BaseMapperMysqlProvider.class, method = "findById")
    Optional<PO> findById(@Param("entityClass") Class<PO> entityClass, @Param("id") ID id);

    @SelectProvider(type = BaseMapperMysqlProvider.class, method = "findByIdForUpdate")
    Optional<PO> findByIdForUpdate(@Param("entityClass") Class<PO> entityClass, @Param("id") ID id);

    @SelectProvider(type = BaseMapperMysqlProvider.class, method = "findList")
    List<PO> findList(
            @Param("entityClass") Class<PO> entityClass,
            @Param("search") MybatisSearch<PO> search,
            @Param("orderBy") OrderBy[] orderBy,
            @Param("limit") TwoValues<Integer, Integer> limit
    );

    @SelectProvider(type = BaseMapperMysqlProvider.class, method = "findListByWhere")
    List<PO> findListByWhere(
            @Param("entityClass") Class<PO> entityClass,
            @Param("where") String where,
            @Param("orderBy") OrderBy[] orderBy,
            @Param("limit") TwoValues<Integer, Integer> limit
    );

    @SelectProvider(type = BaseMapperMysqlProvider.class, method = "countBy")
    long countBy(@Param("entityClass") Class<PO> entityClass, @Param("search") MybatisSearch<PO> search);
}
