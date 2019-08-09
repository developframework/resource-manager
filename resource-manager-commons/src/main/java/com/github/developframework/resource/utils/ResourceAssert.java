package com.github.developframework.resource.utils;

import com.github.developframework.resource.exception.ResourceExistException;
import com.github.developframework.resource.exception.ResourceNotExistException;

import java.util.Objects;
import java.util.Optional;

/**
 * 断言
 *
 * @author qiushui on 2018-05-25.
 * @version 0.1
 */
public final class ResourceAssert {

    /**
     * 断言资源存在
     * @param resourceName 资源名称
     * @param resource 资源
     * @param <T> 资源类型
     * @return 资源
     */
    public static <T> T resourceExist(String resourceName, T resource) {
		if (Objects.nonNull(resource)) {
			return resource;
		} else {
			throw new ResourceNotExistException(resourceName);
		}
	}

	/**
	 * 断言资源存在
	 *
	 * @param resourceName 资源名称
	 * @param resource     资源
	 * @param <T>          资源类型
	 * @return 资源
	 */
	public static <T> HasReturnResourceAssertBuilder<T> resourceExistAssertBuilder(String resourceName, T resource) {
		if (Objects.nonNull(resource)) {
			return new HasReturnResourceAssertBuilder<>(resource);
		} else {
			return new HasReturnResourceAssertBuilder<>(new ResourceNotExistException(resourceName));
		}

    }

    /**
     * 断言资源存在
     * @param resourceName 资源名称
     * @param resourceOptional 资源
     * @param <T> 资源类型
     * @return 资源
     */
	public static <T> T resourceExist(String resourceName, Optional<T> resourceOptional) {
		return resourceOptional.orElseThrow(() -> new ResourceNotExistException(resourceName));
	}

	/**
	 * 断言资源存在
	 *
	 * @param resourceName     资源名称
	 * @param resourceOptional 资源
	 * @param <T>              资源类型
	 * @return 资源
	 */
	public static <T> HasReturnResourceAssertBuilder<T> resourceExistAssertBuilder(String resourceName, Optional<T> resourceOptional) {
		return resourceOptional
				.map(HasReturnResourceAssertBuilder::new)
				.orElseGet(() -> new HasReturnResourceAssertBuilder<>(new ResourceNotExistException(resourceName)));
	}

	/**
	 * 断言资源不存在
	 *
	 * @param resourceName 资源名称
	 * @param resource     资源
	 * @return 资源
	 */
	public static void resourceNotExist(String resourceName, Object resource) {
		if (Objects.nonNull(resource)) {
			throw new ResourceExistException(resourceName);
		}
	}

	/**
	 * 断言资源不存在
	 *
	 * @param resourceName 资源名称
	 * @param resource     资源
	 * @return 生成器
	 */
	public static ResourceAssertBuilder resourceNotExistAssertBuilder(String resourceName, Object resource) {
		if (Objects.nonNull(resource)) {
			return new ResourceAssertBuilder(new ResourceExistException(resourceName));
		}
		return new ResourceAssertBuilder();
	}

	/**
	 * 断言资源不存在
	 *
	 * @param resourceName     资源名称
	 * @param resourceOptional 资源
	 * @return 资源
	 */
	public static void resourceNotExist(String resourceName, Optional<Object> resourceOptional) {
		if (resourceOptional.isPresent()) {
			throw new ResourceExistException(resourceName);
		}
	}

	/**
	 * 断言资源不存在
	 *
	 * @param resourceName     资源名称
	 * @param resourceOptional 资源
	 * @return 资源
	 */
	public static ResourceAssertBuilder resourceNotExistAssertBuilder(String resourceName, Optional<Object> resourceOptional) {
		if (resourceOptional.isPresent()) {
			return new ResourceAssertBuilder(new ResourceExistException(resourceName));
		}
		return new ResourceAssertBuilder();
	}
}
