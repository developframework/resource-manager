package com.github.developframework.resource.spring.jpa.utils;

import develop.toolkit.base.constants.DateFormatConstants;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Predicate链式构建器
 *
 * @author qiushui on 2018-11-01.
 */
public final class PredicateBuilder<T> {

    private final List<Predicate> predicates;

    private final Root<T> root;

    private final CriteriaBuilder criteriaBuilder;

    public PredicateBuilder(Root<T> root, CriteriaBuilder criteriaBuilder) {
        this.root = root;
        this.criteriaBuilder = criteriaBuilder;
        this.predicates = new LinkedList<>();
    }

    /**
     * 构建
     *
     * @return
     */
    public Predicate[] build() {
        return predicates.toArray(new Predicate[0]);
    }

    /**
     * 添加进where语句
     *
     * @param query
     */
    public void where(CriteriaQuery<?> query) {
        query.where(build());
    }

    /**
     * 自定义
     *
     * @param complexPredicate
     * @return
     */
    public PredicateBuilder<T> complexPredicate(ComplexPredicate<T> complexPredicate) {
        Predicate predicate = complexPredicate.toPredicate(root, criteriaBuilder);
        if (predicate != null) {
            predicates.add(predicate);
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
    public PredicateBuilder<T> equal(String attribute, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            predicates.add(criteriaBuilder.equal(Specifications.path(root, attribute), value));
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
    public PredicateBuilder<T> notEqual(String attribute, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            predicates.add(criteriaBuilder.notEqual(Specifications.path(root, attribute), value));
        }
        return this;
    }

    /**
     * 等于函数
     *
     * @param function
     * @param value
     * @return
     */
    public PredicateBuilder<T> equalFunction(Expression<?> function, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).isEmpty()) {
                return this;
            }
            predicates.add(criteriaBuilder.equal(function, value));
        }
        return this;
    }

    /**
     * 包含匹配条件
     *
     * @param attribute
     * @param value
     * @return
     */
    public PredicateBuilder<T> containsLike(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(criteriaBuilder.like(Specifications.path(root, attribute), "%" + value + "%"));
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
    public PredicateBuilder<T> startWith(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(criteriaBuilder.like(Specifications.path(root, attribute), value + "%"));
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
    public PredicateBuilder<T> endWith(String attribute, String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(criteriaBuilder.like(Specifications.path(root, attribute), "%" + value));
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
    public <Y extends Comparable<? super Y>> PredicateBuilder<T> isBetween(String attribute, Y min, Y max) {
        if (min != null && max == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(Specifications.path(root, attribute), min));
        } else if (min == null && max != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(Specifications.path(root, attribute), max));
        } else if (min != null) {
            predicates.add(criteriaBuilder.between(Specifications.path(root, attribute), min, max));
        }
        return this;
    }

    /**
     * 区间匹配条件
     *
     * @param function
     * @param min
     * @param max
     * @param <Y>
     * @return
     */
    public <Y extends Comparable<? super Y>> PredicateBuilder<T> isBetweenFunction(Expression<Y> function, Y min, Y max) {
        if (min != null && max == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, min));
        } else if (min == null && max != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, max));
        } else if (min != null) {
            predicates.add(criteriaBuilder.between(function, min, max));
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
    public <E> PredicateBuilder<T> in(String attribute, Collection<E> collection) {
        if (collection != null && !collection.isEmpty()) {
            predicates.add(Specifications.path(root, attribute).in(collection));
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
    public <E> PredicateBuilder<T> in(String attribute, E... array) {
        if (array != null && array.length != 0) {
            predicates.add(Specifications.path(root, attribute).in(array));
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
    public <Y extends Comparable<? super Y>> PredicateBuilder<T> greaterThan(String attribute, Y value) {
        if (value != null) {
            predicates.add(criteriaBuilder.greaterThan(Specifications.path(root, attribute), value));
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
    public <Y extends Comparable<? super Y>> PredicateBuilder<T> greaterThanOrEqualTo(String attribute, Y value) {
        if (value != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(Specifications.path(root, attribute), value));
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
    public <Y extends Comparable<? super Y>> PredicateBuilder<T> lessThan(String attribute, Y value) {
        if (value != null) {
            predicates.add(criteriaBuilder.lessThan(Specifications.path(root, attribute), value));
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
    public <Y extends Comparable<? super Y>> PredicateBuilder<T> lessThanOrEqualTo(String attribute, Y value) {
        if (value != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(Specifications.path(root, attribute), value));
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
    public PredicateBuilder<T> dateEqual(String attribute, LocalDate date) {
        if (date != null) {
            predicates.add(
                    criteriaBuilder.equal(
                            criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATE)),
                            date.format(DateFormatConstants.DATE_FORMATTER)
                    )
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
    public PredicateBuilder<T> dateEqual(String attribute, Date date) {
        if (date != null) {
            predicates.add(
                    criteriaBuilder.equal(
                            criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATE)),
                            new SimpleDateFormat(DateFormatConstants.DATE).format(date)
                    )
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
    public PredicateBuilder<T> dateEqual(String attribute, String date) {
        if (date != null) {
            predicates.add(
                    criteriaBuilder.equal(
                            criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATE)),
                            date
                    )
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
    public PredicateBuilder<T> monthEqual(String attribute, String month) {
        if (month != null) {
            predicates.add(
                    criteriaBuilder.equal(
                            criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_MONTH)),
                            month
                    )
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
    public PredicateBuilder<T> yearEqual(String attribute, Integer year) {
        Expression<String> function = criteriaBuilder.function("YEAR", String.class, Specifications.path(root, attribute));
        if (year != null) {
            predicates.add(criteriaBuilder.equal(function, year));
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
    public PredicateBuilder<T> dateRange(String attribute, LocalDate start, LocalDate end) {
        Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATE));
        if (start != null && end == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, start.format(DateFormatConstants.DATE_FORMATTER)));
        } else if (start == null && end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, end.format(DateFormatConstants.DATE_FORMATTER)));
        } else if (start != null) {
            predicates.add(criteriaBuilder.between(function, start.format(DateFormatConstants.DATE_FORMATTER), end.format(DateFormatConstants.DATE_FORMATTER)));
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
    public PredicateBuilder<T> dateRange(String attribute, Date start, Date end) {
        Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATE));
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormatConstants.DATE);
        if (start != null && end == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, sdf.format(start)));
        } else if (start == null && end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, sdf.format(end)));
        } else if (start != null) {
            predicates.add(criteriaBuilder.between(function, sdf.format(start), sdf.format(end)));
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
    public PredicateBuilder<T> dateRange(String attribute, String start, String end) {
        Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATE));
        if (start != null && end == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, start));
        } else if (start == null && end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, end));
        } else if (start != null) {
            predicates.add(criteriaBuilder.between(function, start, end));
        }
        return this;
    }

    /**
     * 日期时间相等条件
     *
     * @param attribute
     * @param dateTime
     * @return
     */
    public PredicateBuilder<T> dateTimeEqual(String attribute, LocalDateTime dateTime) {
        if (dateTime != null) {
            Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATETIME));
            predicates.add(criteriaBuilder.equal(function, dateTime.format(DateFormatConstants.STANDARD_FORMATTER)));
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
    public PredicateBuilder<T> dateTimeEqual(String attribute, Date date) {
        if (date != null) {
            Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATETIME));
            predicates.add(criteriaBuilder.equal(function, new SimpleDateFormat(DateFormatConstants.STANDARD).format(date)));
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
    public PredicateBuilder<T> dateTimeEqual(String attribute, String date) {
        if (date != null) {
            Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATETIME));
            predicates.add(criteriaBuilder.equal(function, date));
        }
        return this;
    }

    /**
     * 日期时间范围
     *
     * @param attribute
     * @param start
     * @param end
     * @return
     */
    public PredicateBuilder<T> dateTimeRange(String attribute, LocalDateTime start, LocalDateTime end) {
        Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATETIME));
        if (start != null && end == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, start.format(DateFormatConstants.STANDARD_FORMATTER)));
        } else if (start == null && end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, end.format(DateFormatConstants.STANDARD_FORMATTER)));
        } else if (start != null) {
            predicates.add(criteriaBuilder.between(function, start.format(DateFormatConstants.STANDARD_FORMATTER), end.format(DateFormatConstants.STANDARD_FORMATTER)));
        }
        return this;
    }

    /**
     * 日期时间范围
     *
     * @param attribute
     * @param start
     * @param end
     * @return
     */
    public PredicateBuilder<T> dateTimeRange(String attribute, Date start, Date end) {
        Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATETIME));
        SimpleDateFormat sdf = new SimpleDateFormat(DateFormatConstants.STANDARD);
        if (start != null && end == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, sdf.format(start)));
        } else if (start == null && end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, sdf.format(end)));
        } else if (start != null) {
            predicates.add(criteriaBuilder.between(function, sdf.format(start), sdf.format(end)));
        }
        return this;
    }

    /**
     * 日期时间范围
     *
     * @param attribute
     * @param start
     * @param end
     * @return
     */
    public PredicateBuilder<T> dateTimeRange(String attribute, String start, String end) {
        Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_DATETIME));
        if (start != null && end == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, start));
        } else if (start == null && end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, end));
        } else if (start != null) {
            predicates.add(criteriaBuilder.between(function, start, end));
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
    public PredicateBuilder<T> monthRange(String attribute, String start, String end) {
        Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal(DateFormatConstants.MYSQL_FORMAT_MONTH));
        if (start != null && end == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, start));
        } else if (start == null && end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, end));
        } else if (start != null) {
            predicates.add(criteriaBuilder.between(function, start, end));
        }
        return this;
    }

    /**
     * 年范围条件
     *
     * @param attribute
     * @param start
     * @param end
     * @return
     */
    public PredicateBuilder<T> yearRange(String attribute, String start, String end) {
        Expression<String> function = criteriaBuilder.function("DATE_FORMAT", String.class, Specifications.path(root, attribute), criteriaBuilder.literal("%Y"));
        if (start != null && end == null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(function, start));
        } else if (start == null && end != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(function, end));
        } else if (start != null) {
            predicates.add(criteriaBuilder.between(function, start, end));
        }
        return this;
    }

    /**
     * 复杂Predicate
     *
     * @param <T>
     */
    public interface ComplexPredicate<T> {

        Predicate toPredicate(Root<T> root, CriteriaBuilder criteriaBuilder);
    }
}
