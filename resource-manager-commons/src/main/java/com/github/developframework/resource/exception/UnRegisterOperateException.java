package com.github.developframework.resource.exception;

import develop.toolkit.base.exception.FormatRuntimeException;

/**
 * 未注册操作异常
 *
 * @author qiushui on 2019-08-16.
 */
public class UnRegisterOperateException extends FormatRuntimeException {

    public UnRegisterOperateException(Class<?> entityClass, String operate, Class<?> dtoClass) {
        super("Manager \"%s\" is not register %s resource operate for DTO \"%s\"", entityClass.getName(), operate, dtoClass.getName());
    }

    public UnRegisterOperateException(Class<?> entityClass, String operate) {
        super("Manager \"%s\" is not register %s resource operate", entityClass.getName(), operate);
    }
}
