package com.github.developframework.resource.mybatis.utils;

import develop.toolkit.base.utils.JavaBeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * where条件拼接器
 *
 * @author qiushui on 2020-05-28.
 */
public final class WhereBuilder {

    private final List<String> where = new LinkedList<>();

    public String build() {
        return StringUtils.join(where, " AND ");
    }

    /**
     * 复杂条件
     *
     * @param enable
     * @param sql
     * @return
     */
    public WhereBuilder complex(boolean enable, String sql) {
        if (enable) {
            where.add(sql);
        }
        return this;
    }

    /**
     * 相等条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public WhereBuilder equal(String attribute, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            where.add(
                    String.format("`%s` = #{search.%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 不相等条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public WhereBuilder notEqual(String attribute, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            where.add(
                    String.format("`%s` <> #{search.%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 模糊匹配条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public WhereBuilder containsLike(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            where.add(
                    String.format("`%s` LIKE CONCAT('%%', #{search.%s}, '%%')", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 开头匹配条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public WhereBuilder startWith(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            where.add(
                    String.format("`%s` LIKE CONCAT(#{search.%s}, '%%')", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 结尾匹配条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public WhereBuilder endWith(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            where.add(
                    String.format("`%s` LIKE CONCAT('%%', #{search.%s})", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 区间匹配条件
     *
     * @param attribute
     * @param min
     * @param max
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> WhereBuilder isBetween(String attribute, String minProperty, Y min, String maxProperty, Y max) {
        String part = null;
        if (min != null && max == null) {
            part = String.format("`%s` >= #{search.%s}", JavaBeanUtils.camelcaseToUnderline(attribute), minProperty);
        } else if (min == null && max != null) {
            part = String.format("`%s` <= #{search.%s}", JavaBeanUtils.camelcaseToUnderline(attribute), maxProperty);
        } else if (min != null) {
            part = String.format("`%s` BETWEEN #{search.%s} AND #{search.%s}", JavaBeanUtils.camelcaseToUnderline(attribute), minProperty, maxProperty);
        }
        if (part != null) {
            where.add(part);
        }
        return this;
    }

    /**
     * 集合条件
     *
     * @param attribute
     * @param collection
     * @param <E>
     * @return
     */
    public <E> WhereBuilder in(String attribute, Collection<E> collection) {
        if (collection != null && !collection.isEmpty()) {
            final String values = collection
                    .parallelStream()
                    .filter(Objects::nonNull)
                    .map(v -> v instanceof Number ? v.toString() : String.format("'%s'", v))
                    .collect(Collectors.joining(",", "(", ")"));
            where.add(
                    String.format("`%s` IN %s", JavaBeanUtils.camelcaseToUnderline(attribute), values)
            );
        }
        return this;
    }

    /**
     * 数组条件
     *
     * @param attribute
     * @param array
     * @param <E>
     * @return
     */
    public <E> WhereBuilder in(String attribute, E... array) {
        if (array != null && array.length != 0) {
            final String values = Stream
                    .of(array)
                    .parallel()
                    .filter(Objects::nonNull)
                    .map(v -> v instanceof Number ? v.toString() : String.format("'%s'", v))
                    .collect(Collectors.joining(",", "(", ")"));
            where.add(
                    String.format("`%s` IN %s", JavaBeanUtils.camelcaseToUnderline(attribute), values)
            );
        }
        return this;
    }

    /**
     * 大于条件
     *
     * @param attribute
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> WhereBuilder greaterThan(String attribute, Y value) {
        if (value != null) {
            where.add(
                    String.format("`%s` > #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 大于等于条件
     *
     * @param attribute
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> WhereBuilder greaterThanOrEqualTo(String attribute, Y value) {
        if (value != null) {
            where.add(
                    String.format("`%s` >= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 小于条件
     *
     * @param attribute
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> WhereBuilder lessThan(String attribute, Y value) {
        if (value != null) {
            where.add(
                    String.format("`%s` < #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 小于等于条件
     *
     * @param attribute
     * @param value
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> WhereBuilder lessThanOrEqualTo(String attribute, Y value) {
        if (value != null) {
            where.add(
                    String.format("`%s` <= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 日期相等条件
     *
     * @param attribute
     * @param date
     * @return
     */
    public WhereBuilder dateEqual(String attribute, Object date) {
        if (date != null) {
            where.add(
                    String.format("DATE_FORMAT(`%s`, 'yyyy-MM-dd') = #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 日期时间相等条件
     *
     * @param attribute
     * @param date
     * @return
     */
    public WhereBuilder dateTimeEqual(String attribute, Object date) {
        if (date != null) {
            where.add(
                    String.format("DATE_FORMAT(`%s`, 'yyyy-MM-dd HH:mm:ss') = #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 月相等条件
     *
     * @param attribute
     * @param month
     * @return
     */
    public WhereBuilder monthEqual(String attribute, String month) {
        if (month != null) {
            where.add(
                    String.format("DATE_FORMAT(`%s`, 'yyyy-MM') = #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 年相等条件
     *
     * @param attribute
     * @param year
     * @return
     */
    public WhereBuilder yearEqual(String attribute, Integer year) {
        if (year != null) {
            where.add(
                    String.format("YEAR(`%s`) = #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), attribute)
            );
        }
        return this;
    }

    /**
     * 日期范围条件
     *
     * @param attribute
     * @param start
     * @param end
     * @return
     */
    public WhereBuilder dateRange(String attribute, String startProperty, Object start, String endProperty, Object end) {
        String part = null;
        if (start != null && end == null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM-dd') >= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), startProperty);
        } else if (start == null && end != null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM-dd') <= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), endProperty);
        } else if (start != null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM-dd') BETWEEN #{%s} AND #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), startProperty, endProperty);
        }
        if (StringUtils.isNotEmpty(part)) {
            where.add(part);
        }
        return this;
    }

    /**
     * 日期时间范围条件
     *
     * @param attribute
     * @param start
     * @param end
     * @return
     */
    public WhereBuilder dateTimeRange(String attribute, String startProperty, Object start, String endProperty, Object end) {
        String part = null;
        if (start != null && end == null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM-dd HH:mm:ss') >= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), startProperty);
        } else if (start == null && end != null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM-dd HH:mm:ss') <= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), endProperty);
        } else if (start != null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM-dd HH:mm:ss') BETWEEN #{%s} AND #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), startProperty, endProperty);
        }
        if (StringUtils.isNotEmpty(part)) {
            where.add(part);
        }
        return this;
    }

    /**
     * 月范围条件
     *
     * @param attribute
     * @param start
     * @param end
     * @return
     */
    public WhereBuilder monthRange(String attribute, String startProperty, String start, String endProperty, String end) {
        String part = null;
        if (start != null && end == null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM') >= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), startProperty);
        } else if (start == null && end != null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM') <= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), endProperty);
        } else if (start != null) {
            part = String.format("DATE_FORMAT(`%s`, 'yyyy-MM') BETWEEN #{%s} AND #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), startProperty, endProperty);
        }
        if (StringUtils.isNotEmpty(part)) {
            where.add(part);
        }
        return this;
    }

    /**
     * 日期时间范围条件
     *
     * @param attribute
     * @param start
     * @param end
     * @return
     */
    public WhereBuilder yearRange(String attribute, String startProperty, Integer start, String endProperty, Integer end) {
        String part = null;
        if (start != null && end == null) {
            part = String.format("YEAR(`%s`) >= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), startProperty);
        } else if (start == null && end != null) {
            part = String.format("YEAR(`%s`) <= #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), endProperty);
        } else if (start != null) {
            part = String.format("YEAR(`%s`) BETWEEN #{%s} AND #{%s}", JavaBeanUtils.camelcaseToUnderline(attribute), startProperty, endProperty);
        }
        if (StringUtils.isNotEmpty(part)) {
            where.add(part);
        }
        return this;
    }
}
