package com.github.developframework.resource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * @author qiushui on 2019-08-10.
 */
@Getter
@RequiredArgsConstructor
public class ResourceDefinition<ENTITY extends Entity<? extends Serializable>> {

    private final Class<ENTITY> entityClass;

    private final String resourceName;
}
