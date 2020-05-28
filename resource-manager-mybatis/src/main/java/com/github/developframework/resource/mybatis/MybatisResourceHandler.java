package com.github.developframework.resource.mybatis;

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
    public boolean existsById(ID id) {
        return daoMapper.existsById(resourceDefinition.getEntityClass(), id);
    }

    @Override
    public void insert(PO entity) {
        daoMapper.insert(entity);
    }

    @Override
    public void insertAll(Collection<PO> pos) {

    }

    @Override
    public boolean update(PO entity) {
        return daoMapper.update(entity);
    }

    @Override
    public void deleteById(ID id) {
        daoMapper.deleteById(resourceDefinition.getEntityClass(), id);
    }

    @Override
    public void delete(PO entity) {
        daoMapper.deleteById(resourceDefinition.getEntityClass(), entity.getId());
    }

    @Override
    public Optional<PO> queryById(ID id) {
        return daoMapper.findById(resourceDefinition.getEntityClass(), id);
    }

    @Override
    public Optional<PO> queryByIdForUpdate(ID id) {
        return Optional.empty();
    }

    @Override
    public List<PO> query(Search<PO> search) {
        return daoMapper.findList(resourceDefinition.getEntityClass(), (MybatisSearch<PO>) search, null, null);
    }
}
