package com.github.developframework.resource.spring.mybatis.typehandlers;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;
import java.util.Collections;
import java.util.List;

/**
 * @author qiushui on 2020-08-19.
 */
public class StringListTypeHandler implements TypeHandler<List<String>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.VARCHAR);
        } else {
            ps.setString(i, StringUtils.join(parameter, ","));
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        String string = rs.getString(columnName);
        if (string == null) {
            return null;
        } else if (string.isEmpty()) {
            return Collections.emptyList();
        } else {
            return List.of(string.split(","));
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String string = rs.getString(columnIndex);
        if (string == null) {
            return null;
        } else if (string.isEmpty()) {
            return Collections.emptyList();
        } else {
            return List.of(string.split(","));
        }
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String string = cs.getString(columnIndex);
        if (string == null) {
            return null;
        } else if (string.isEmpty()) {
            return Collections.emptyList();
        } else {
            return List.of(string.split(","));
        }
    }
}
