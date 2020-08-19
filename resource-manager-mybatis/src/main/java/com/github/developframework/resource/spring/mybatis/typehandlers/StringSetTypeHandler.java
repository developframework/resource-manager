package com.github.developframework.resource.spring.mybatis.typehandlers;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.util.Set;

/**
 * @author qiushui on 2020-08-19.
 */
public class StringSetTypeHandler implements TypeHandler<Set<String>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Set<String> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.VARCHAR);
        } else {
            ps.setString(i, StringUtils.join(parameter, ","));
        }
    }

    @Override
    public Set<String> getResult(ResultSet rs, String columnName) throws SQLException {
        String string = rs.getString(columnName);
        return string == null ? null : Set.of(string.split(","));
    }

    @Override
    public Set<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String string = rs.getString(columnIndex);
        return string == null ? null : Set.of(string.split(","));
    }

    @Override
    public Set<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String string = cs.getString(columnIndex);
        return string == null ? null : Set.of(string.split(","));
    }
}
