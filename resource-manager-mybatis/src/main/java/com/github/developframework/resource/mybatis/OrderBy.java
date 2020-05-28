package com.github.developframework.resource.mybatis;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author qiushui on 2020-05-28.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderBy {

    private final String property;

    private final Direction direction;

    @Override
    public String toString() {
        return property + " " + direction;
    }

    public static OrderBy asc(String property) {
        return new OrderBy(property, Direction.ASC);
    }

    public static OrderBy desc(String property) {
        return new OrderBy(property, Direction.DESC);
    }

    public enum Direction {
        ASC, DESC
    }
}
