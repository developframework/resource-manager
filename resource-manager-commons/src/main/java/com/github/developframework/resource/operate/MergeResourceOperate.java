package com.github.developframework.resource.operate;

import com.github.developframework.resource.BasicMapper;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.exception.DTOCastException;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * 合并资源操作
 *
 * @author qiushui on 2020-07-17.
 */
public abstract class MergeResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends PersistResourceOperate<ENTITY, DTO, ID> {

    public MergeResourceOperate(Class<DTO> dtoClass, Class<? extends BasicMapper<ENTITY, DTO>> mapperClass) {
        super(dtoClass, mapperClass);
    }

    /**
     * 在添加操作的第一步
     */
    protected boolean before(DTO dto) {
        // 默认无处理
        return true;
    }

    /**
     * 创建实体
     */
    protected ENTITY create(DTO dto) {
        if (mapper != null) {
            return mapper.toENTITY(dto);
        } else {
            try {
                return resourceDefinition.getEntityClass().getConstructor().newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 查询
     */
    protected abstract Optional<ENTITY> find(DTO dto);

    protected void merge(DTO dto, ENTITY entity) {
        if (mapper != null) {
            mapper.toENTITY(dto, entity);
        }
    }

    /**
     * 在save之前的一步
     */
    protected void prepare(DTO dto, ENTITY entity) {
        // 默认无处理
    }

    /**
     * 合并资源流程
     */
    @SuppressWarnings("unchecked")
    public Optional<ENTITY> mergeResource(Object obj) {
        if (dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            if (before(dto)) {
                Optional<ENTITY> optional = find(dto);
                ENTITY entity;
                if (optional.isPresent()) {
                    entity = optional.get();
                    merge(dto, entity);
                    prepare(dto, entity);
                    resourceHandler.update(entity);
                } else {
                    entity = create(dto);
                    merge(dto, entity);
                    prepare(dto, entity);
                    resourceHandler.insert(entity);
                }
                after(dto, entity);
                return Optional.of(entity);
            }
            return Optional.empty();
        } else {
            throw new DTOCastException();
        }
    }
}
