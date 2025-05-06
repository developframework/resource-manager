package com.github.developframework.resource.spring.jpa.utils;

import develop.toolkit.base.components.SnowflakeIdWorker;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * @author qiushui on 2021-08-05.
 */
public class SnowflakeId implements IdentifierGenerator {

    private final SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(1, 1);

    @Override
    public Serializable generate(SharedSessionContractImplementor implementor, Object o) throws HibernateException {
        return snowflakeIdWorker.nextId();
    }
}
