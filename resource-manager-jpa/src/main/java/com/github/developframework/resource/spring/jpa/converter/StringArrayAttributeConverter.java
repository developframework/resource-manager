package com.github.developframework.resource.spring.jpa.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;

/**
 * 字符串数组属性转换器
 *
 * @author qiushui on 2019-08-13.
 */
public class StringArrayAttributeConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        return attribute == null ? null : StringUtils.join(attribute, ",");
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.isEmpty()) {
            return new String[0];
        } else {
            return dbData.split(",");
        }
    }
}
