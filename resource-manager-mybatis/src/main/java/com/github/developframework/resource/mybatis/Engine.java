package com.github.developframework.resource.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置mysql 引擎
 *
 * @author qiushui on 2020-05-29.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Engine {

    MysqlEngine value() default MysqlEngine.InnoDB;
}
