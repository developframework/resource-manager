package com.github.developframework.resource.spring.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qiushui on 2020-05-29.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    int length() default 255;

    int precision() default 0;

    int scale() default 0;

    boolean unique() default false;

    boolean nullable() default true;

    String columnDefinition() default "";
}
