package com.github.developframework.resource.mybatis;

import com.github.developframework.resource.*;
import com.github.developframework.resource.mybatis.utils.WhereBuilder;
import develop.toolkit.base.struct.Pager;
import develop.toolkit.base.struct.PagerResult;
import develop.toolkit.base.struct.TwoValues;
import develop.toolkit.base.utils.CollectionAdvice;
import lombok.Getter;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

/**
 * Mybatis资源管理器
 *
 * @author qiushui on 2020-05-28.
 */
public class MybatisResourceManager<
        PO extends MPO<ID>,
        ID extends Serializable,
        DAOMAPPER extends BaseDaoMapper<PO, ID>
        > extends AbstractResourceManager<PO, ID> {

    @Getter
    protected final DAOMAPPER daoMapper;

    public MybatisResourceManager(DAOMAPPER daoMapper, Class<PO> entityClass, String resourceName) {
        super(new ResourceDefinition<>(entityClass, resourceName));
        this.daoMapper = daoMapper;
    }

    @PostConstruct
    public void init() {
        this.resourceHandler = new MybatisResourceHandler<>(daoMapper, resourceDefinition);
        this.resourceOperateRegistry = new ResourceOperateRegistry<>(this);
    }

    @Override
    public <T extends DTO> AddCheckExistsLogic<PO, T, ID> byFieldAddCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldMybatisAddCheckExistsLogic<>(resourceDefinition, daoMapper, fields);
    }

    @Override
    public <T extends DTO> ModifyCheckExistsLogic<PO, T, ID> byFieldModifyCheck(Class<T> dtoClass, String... fields) {
        return new ByFieldMybatisModifyCheckExistsLogic<>(resourceDefinition, daoMapper, fields);
    }

    @Override
    public List<PO> listForIds(String id, ID[] ids) {
        List<PO> list = daoMapper.findListByWhere(
                resourceDefinition.getEntityClass(),
                new WhereBuilder().in(id, ids).build(),
                null,
                null
        );
        return CollectionAdvice.sort(list, ids, (po, i) -> po.getId().equals(i));
    }

    public <SEARCH extends MybatisSearch<PO>> List<PO> list(SEARCH search) {
        List<PO> list = resourceHandler.query(search);
        return execSearchOperate(list);
    }

    public <SEARCH extends MybatisSearch<PO>> List<PO> list(SEARCH search, OrderBy... orderByArray) {
        List<PO> list = daoMapper.findList(resourceDefinition.getEntityClass(), search, orderByArray, null);
        return execSearchOperate(list);
    }

    public <SEARCH extends MybatisSearch<PO>> PagerResult<PO> pager(Pager pager, SEARCH search, OrderBy... orderByArray) {
        TwoValues<Integer, Integer> limit = TwoValues.of(pager.getOffset(), pager.getSize());
        List<PO> list = daoMapper.findList(resourceDefinition.getEntityClass(), search, orderByArray, limit);
        long total = daoMapper.countBy(resourceDefinition.getEntityClass(), search);
        return new PagerResult<>(pager, execSearchOperate(list), total);
    }
}
