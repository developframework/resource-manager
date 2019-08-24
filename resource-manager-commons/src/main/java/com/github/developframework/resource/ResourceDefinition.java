package com.github.developframework.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-10.
 */
@Getter
@AllArgsConstructor
public class ResourceDefinition<ENTITY extends Entity<? extends Serializable>> {

    private Class<ENTITY> entityClass;

    private String resourceName;
}
