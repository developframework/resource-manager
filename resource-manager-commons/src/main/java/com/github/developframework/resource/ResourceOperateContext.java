package com.github.developframework.resource;

import org.springframework.lang.NonNull;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 资源操作上下文
 *
 * @author qiushui on 2019-08-08.
 */
@SuppressWarnings("unchecked")
public class ResourceOperateContext extends ConcurrentHashMap<String, Object> {


    public <T> T getValue(@NonNull String name, Class<T> clazz) {
        return (T) get(name);
    }

    public <T> T getValue(@NonNull String name, Class<T> clazz, T defaultValue) {
        return (T) getOrDefault(name, defaultValue);
    }
}
