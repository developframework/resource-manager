package com.github.developframework.resource;

import java.io.Serializable;

/**
 * 实体
 *
 * @author qiushui on 2019-07-25.
 */
public interface Entity<ID extends Serializable> extends Serializable {

    ID getId();


}
