package com.github.developframework.resource.spring;

import com.github.developframework.resource.AbstractResourceManager;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.Search;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * spring-data资源管理器
 *
 * @author qiushui on 2019-08-15.
 */
public abstract class SpringDataResourceManager<
        ENTITY extends Entity<ID>,
        ID extends Serializable,
        REPOSITORY extends PagingAndSortingRepository<ENTITY, ID>
        > extends AbstractResourceManager<ENTITY, ID> {

    @Getter
    protected REPOSITORY repository;

    protected TransactionTemplate transactionTemplate;

    public SpringDataResourceManager(REPOSITORY repository, ResourceDefinition<ENTITY> resourceDefinition) {
        super(resourceDefinition);
        this.repository = repository;
    }

    @Override
    public Optional<ENTITY> add(Object dto) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.add(dto));
            }
        } else {
            return transactionTemplate.execute(transactionStatus -> super.add(dto));
        }
    }

    @Override
    public Optional<ENTITY> modifyById(ID id, Object dto) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.modifyById(id, dto));
            }
        } else {
            return transactionTemplate.execute(transactionStatus -> super.modifyById(id, dto));
        }
    }

    @Override
    public boolean remove(ENTITY entity) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                final Boolean execute = transactionTemplate.execute(transactionStatus -> super.remove(entity));
                return execute != null ? execute : false;
            }
        } else {
            final Boolean execute = transactionTemplate.execute(transactionStatus -> super.remove(entity));
            return execute != null ? execute : false;
        }
    }

    @Override
    public Optional<ENTITY> removeById(ID id) {
        if (resourceOperateRegistry.isUniqueEntity()) {
            synchronized (this) {
                return transactionTemplate.execute(transactionStatus -> super.removeById(id));
            }
        } else {
            return super.removeById(id);
        }

    }

    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(SEARCH search) {
        List<ENTITY> list = resourceHandler.query(search);
        return execSearchOperate(list);
    }

    public <SEARCH extends Search<ENTITY>> List<ENTITY> list(Sort sort, SEARCH search) {
        List<ENTITY> list = ((SpringDataResourceHandler<ENTITY, ID, REPOSITORY>) resourceHandler).query(sort, search);
        return execSearchOperate(list);
    }

    public <SEARCH extends Search<ENTITY>> Page<ENTITY> pager(Pageable pageable, SEARCH search) {
        Page<ENTITY> page = ((SpringDataResourceHandler<ENTITY, ID, REPOSITORY>) resourceHandler).queryPager(pageable, search);
        return execSearchOperate(page);
    }
}
