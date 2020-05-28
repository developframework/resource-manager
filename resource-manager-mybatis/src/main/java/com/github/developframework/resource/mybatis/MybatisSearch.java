package com.github.developframework.resource.mybatis;

import com.github.developframework.resource.Search;

import java.io.Serializable;

/**
 * @author qiushui on 2020-05-28.
 */
public interface MybatisSearch<PO extends MPO<? extends Serializable>> extends Search<PO> {

    String whereSQL();
}
