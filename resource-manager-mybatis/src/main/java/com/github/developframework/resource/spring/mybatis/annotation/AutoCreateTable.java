package com.github.developframework.resource.spring.mybatis.annotation;

import com.github.developframework.resource.spring.mybatis.MPO;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiushui on 2020-06-01.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoCreateTable {

    Class<? extends MPO> value() default MPO.class;
}
