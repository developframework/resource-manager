package com.github.developframework.resource.operate;

import com.github.developframework.resource.AbstractResourceManager;
import com.github.developframework.resource.BasicMapper;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ModifyCheckExistsLogic;
import com.github.developframework.resource.exception.DTOCastException;
import lombok.Getter;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author qiushui on 2019-08-10.
 */
public abstract class ModifyUniqueResourceOperate<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > extends ModifyResourceOperate<ENTITY, DTO, ID> {

    @Getter
    private ModifyCheckExistsLogic<ENTITY, DTO, ID> logic;

    public ModifyUniqueResourceOperate(Class<DTO> dtoClass, Class<? extends BasicMapper<ENTITY, DTO>> mapperClass) {
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
    public abstract ModifyCheckExistsLogic<ENTITY, DTO, ID> configureCheckExistsLogic();

    /**
     * 根据ID修改资源
     *
     * @param obj
     * @param id
     */
    @Override
    @SuppressWarnings("unchecked")
    public Optional<ENTITY> modifyById(Object obj, ID id) {
        if (dtoClass.isAssignableFrom(obj.getClass())) {
            DTO dto = (DTO) obj;
            return resourceHandler
                    .queryByIdForUpdate(id)
                    .map(entity -> {
                        if (before(dto, entity)) {
                            if (logic.check(dto, entity)) {
                                throw logic.getResourceExistException(dto, resourceDefinition.getResourceName());
                            }
                            merge(dto, entity);
                            prepare(dto, entity);
                            boolean success = resourceHandler.update(entity);
                            after(dto, entity);
                            return success ? entity : null;
                        } else {
                            return null;
                        }
                    });
        } else {
            throw new DTOCastException();
        }
    }


    public ModifyCheckExistsLogic<ENTITY, DTO, ID> byField(String... fields) {
        return manager.byFieldModifyCheck(dtoClass, fields);
    }
}
