package com.github.developframework.resource.spring.jpa.utils;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Date;

/**
 * Specification代码简化工具包
 *
 * @author qiushui on 2018-08-30.
 * @since 0.1
 */
public final class Specifications {

    /**
     * 取得Path
     * <p>
     * 比如attribute可以为 aaa.bbb.ccc
     * 相等于 root.get("aaa").get("bbb").get("ccc")
     *
     * @param root
     * @param attribute
     * @param <T>
     * @param <Y>
     * @return
     */
    public static <T, Y> Path<Y> path(Root<T> root, String attribute) {
        String[] parts = attribute.split("\\.");
        Path<Y> path = root.get(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            path = path.get(parts[i]);
        }
        return path;
    }

    /**
     * 等值查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Specification<T> equal(String attribute, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get(attribute), value);
        };
    }

    /**
     * 不等值查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Specification<T> notEqual(String attribute, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.notEqual(root.get(attribute), value);
        };
    }

    /**
     * 模糊查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Specification<T> containsLike(String attribute, String value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.like(root.get(attribute), "%" + value + "%");
        };
    }

    /**
     * 开头匹配查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Specification<T> startWith(String attribute, String value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.like(root.get(attribute), value + "%");
        };
    }

    /**
     * 结尾匹配查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @return
     */
    public static <T> Specification<T> endWith(String attribute, String value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.like(root.get(attribute), "%" + value);
        };
    }

    /**
     * 区间查询
     *
     * @param attribute
     * @param min
     * @param max
     * @param <T>
     * @return
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> isBetween(String attribute, Y min, Y max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return cb.conjunction();
            } else if (min != null && max == null) {
                return cb.greaterThanOrEqualTo(root.get(attribute), min);
            } else if (min == null && max != null) {
                return cb.lessThanOrEqualTo(root.get(attribute), max);
            } else {
                return cb.between(root.get(attribute), min, max);
            }
        };
    }

    /**
     * 区间查询
     *
     * @param attribute
     * @param start
     * @param end
     * @param <T>
     * @return
     */
    public static <T> Specification<T> isBetween(String attribute, Date start, Date end) {
        return (root, query, cb) -> {
            if (start == null && end == null) {
                return cb.conjunction();
            } else if (start != null && end == null) {
                return cb.greaterThanOrEqualTo(root.get(attribute), start);
            } else if (start == null && end != null) {
                return cb.lessThanOrEqualTo(root.get(attribute), end);
            } else {
                return cb.between(root.get(attribute), start, end);
            }
        };
    }

    /**
     * in查询
     *
     * @param attribute
     * @param collection
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> Specification<T> in(String attribute, Collection<E> collection) {
        return (root, query, cb) -> {
            if (collection == null || collection.isEmpty()) {
                return cb.conjunction();
            }
            return root.get(attribute).in(collection);
        };
    }

    /**
     * in查询
     *
     * @param attribute
     * @param collection
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> Specification<T> in(String attribute, E[] collection) {
        return (root, query, cb) -> {
            if (collection == null || collection.length == 0) {
                return cb.conjunction();
            }
            return root.get(attribute).in(collection);
        };
    }

    /**
     * 大于查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @param <Y>
     * @return
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThan(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.greaterThan(root.get(attribute), value);
        };
    }

    /**
     * 大于等于查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @param <Y>
     * @return
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> greaterThanOrEqualTo(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get(attribute), value);
        };
    }

    /**
     * 小于查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @param <Y>
     * @return
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThan(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.lessThan(root.get(attribute), value);
        };
    }

    /**
     * 小于等于查询
     *
     * @param attribute
     * @param value
     * @param <T>
     * @param <Y>
     * @return
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> lessThanOrEqualTo(String attribute, Y value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get(attribute), value);
        };
    }
}
