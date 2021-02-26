package com.github.developframework.resource.spring.mybatis;

import develop.toolkit.base.struct.Pager;

/**
 * @author qiushui on 2020-12-23.
 */
public class MybatisPager extends Pager {

    public MybatisPager() {
        super();
    }

    public MybatisPager(int index, int size) {
        super(index, size);
    }

    /**
     * 生成 LIMIT 语句
     *
     * @return LIMIT 语句
     */
    public String limitSQL() {
        return String.format("LIMIT %d, %d", page * size, size);
    }
}
