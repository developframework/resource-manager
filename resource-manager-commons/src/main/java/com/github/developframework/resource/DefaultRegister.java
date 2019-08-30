package com.github.developframework.resource;

import java.io.Serializable;
import java.lang.annotation.*;

/**
 * 默认注册
 *
 * @author qiushui on 2019-08-28.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DefaultRegister {

    Class<? extends DTO> dtoClass();

    Class<? extends BasicMapper<? extends Entity<? extends Serializable>, ? extends DTO>> mapperClass();
}
