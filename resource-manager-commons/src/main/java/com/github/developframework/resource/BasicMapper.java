package com.github.developframework.resource;

import org.mapstruct.MappingTarget;

/**
 * 基础mapstruct mapper
 *
 * @author qiushui on 2018-08-28.
 * @since 0.1
 */
public interface BasicMapper<ENTITY extends Entity<?>, DTO extends com.github.developframework.resource.DTO> {

	ENTITY toENTITY(DTO dto);

	void toENTITY(DTO dto, @MappingTarget ENTITY entity);

}
