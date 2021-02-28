package com.github.developframework.resource.spring.mybatis.typehandlers;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.*;

/**
 * @author qiushui on 2020-08-19.
 */
public class StringArrayTypeHandler implements TypeHandler<String[]> {

    @Override
    public void setParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.VARCHAR);
        } else {
            ps.setString(i, StringUtils.join(parameter, ","));
        }
    }

    @Override
    public String[] getResult(ResultSet rs, String columnName) throws SQLException {
        String string = rs.getString(columnName);
        if (string == null) {
            return null;
        } else if (string.isEmpty()) {
            return new String[0];
        } else {
            return string.split(",");
        }
    }

    @Override
    public String[] getResult(ResultSet rs, int columnIndex) throws SQLException {
        String string = rs.getString(columnIndex);
        if (string == null) {
            return null;
        } else if (string.isEmpty()) {
            return new String[0];
        } else {
            return string.split(",");
        }
    }

    @Override
    public String[] getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String string = cs.getString(columnIndex);
        if (string == null) {
            return null;
        } else if (string.isEmpty()) {
            return new String[0];
        } else {
            return string.split(",");
        }
    }
}
