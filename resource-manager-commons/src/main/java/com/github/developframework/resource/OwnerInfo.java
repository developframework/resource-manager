package com.github.developframework.resource;

import lombok.Getter;

/**
 * @author qiushui on 2025-05-16.
 */
@Getter
public class OwnerInfo {

    private final String ownerType;

    private final Object ownerId;

    public OwnerInfo(String ownerType, Object ownerId) {
        this.ownerType = ownerType;
        this.ownerId = ownerId;
    }
}
