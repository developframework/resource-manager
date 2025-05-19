package com.github.developframework.resource.spring.mybatis;

import com.github.developframework.resource.OwnerInfo;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.ResourceHandler;
import com.github.developframework.resource.Search;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * mybatis资源操作器
 *
 * @author qiushui on 2020-05-28.
 */
public class MybatisResourceHandler<
        PO extends MPO<ID>,
        ID extends Serializable,
        DAOMAPPER extends BaseDaoMapper<PO, ID>
        > implements ResourceHandler<PO, ID> {

    protected final DAOMAPPER daoMapper;

    protected final ResourceDefinition<PO> resourceDefinition;

    public MybatisResourceHandler(DAOMAPPER daoMapper, ResourceDefinition<PO> resourceDefinition) {
        this.daoMapper = daoMapper;
        this.resourceDefinition = resourceDefinition;
    }

    @Override
    public String primaryKeyFieldName() {
        return "";
    }

    @Override
    public String ownerFieldName(String ownerType) {
        return "";
    }

    @Override
    public boolean existsById(ID id, OwnerInfo ownerInfo) {
        return daoMapper.existsById(resourceDefinition.getEntityClass(), id);
    }

    @Override
    public void insert(PO entity) {
        daoMapper.insert(entity);
    }

    @Override
    public void insertAll(Collection<PO> entities) {
        daoMapper.insertAll(resourceDefinition.getEntityClass(), entities);
    }

    @Override
    public boolean update(PO entity) {
        return daoMapper.update(entity);
    }

    @Override
    public void deleteById(ID id, OwnerInfo ownerInfo) {
        daoMapper.deleteById(resourceDefinition.getEntityClass(), id);
    }

    @Override
    public void delete(PO entity) {
        daoMapper.deleteById(resourceDefinition.getEntityClass(), entity.getId());
    }

    @Override
    public Optional<PO> queryById(ID id, OwnerInfo ownerInfo) {
        return daoMapper.findById(resourceDefinition.getEntityClass(), id);
    }

    @Override
    public Optional<PO> queryByIdForUpdate(ID id, OwnerInfo ownerInfo) {
        return daoMapper.findByIdForUpdate(resourceDefinition.getEntityClass(), id);
    }

    @Override
    public List<PO> query(Search<PO> search) {
        return daoMapper.findList(resourceDefinition.getEntityClass(), (MybatisSearch<PO>) search, null, null);
    }
}
