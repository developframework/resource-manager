package com.github.developframework.resource.operate;

import com.github.developframework.resource.AbstractResourceManager;
import com.github.developframework.resource.AddCheckExistsLogic;
import com.github.developframework.resource.BasicMapper;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.exception.DTOCastException;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;

/**
 * 添加唯一资源操作
 *
 * @author qiushui on 2019-08-08.
 */
public abstract class AddUniqueResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends AddResourceOperate<ENTITY, DTO, ID> {

    @Getter
    private AddCheckExistsLogic<ENTITY, DTO, ID> logic;

    public AddUniqueResourceOperate(Class<DTO> dtoClass, Class<? extends BasicMapper<ENTITY, DTO>> mapperClass) {
        super(dtoClass, mapperClass);
    }

    @Override
    public void setManager(AbstractResourceManager<ENTITY, ID> manager) {
        super.setManager(manager);
        logic = configureCheckExistsLogic();
    }

    /**
     * 检查存在逻辑
     *
     * @return
     */
    public abstract AddCheckExistsLogic<ENTITY, DTO, ID> configureCheckExistsLogic();

    /**
     * 添加资源流程
     *
     * @param obj
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<ENTITY> addResource(Object obj) {
        if (dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            if (before(dto)) {
                if (logic.check(dto)) {
                    throw logic.getResourceExistException(dto, resourceDefinition.getResourceName());
                }
                ENTITY entity = create(dto);
                prepare(dto, entity);
                resourceHandler.insert(entity);
                after(dto, entity);
                return Optional.of(entity);
            }
            return Optional.empty();
        } else {
            throw new DTOCastException();
        }
    }

    public AddCheckExistsLogic<ENTITY, DTO, ID> byField(String... fields) {
        return manager.byFieldAddCheck(dtoClass, fields);
    }
}
