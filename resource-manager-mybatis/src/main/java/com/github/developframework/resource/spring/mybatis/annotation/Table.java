package com.github.developframework.resource.spring.mybatis.annotation;

import com.github.developframework.resource.spring.mybatis.MysqlEngine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiushui on 2020-05-29.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {

    String name() default "";

    MysqlEngine engine() default MysqlEngine.InnoDB;
}
