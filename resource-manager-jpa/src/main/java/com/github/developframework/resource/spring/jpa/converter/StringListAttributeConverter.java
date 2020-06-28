package com.github.developframework.resource.spring.jpa.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.LinkedList;
import java.util.List;

/**
 * 字符串列表属性转换器
 *
 * @author qiushui on 2019-08-13.
 */
public class StringListAttributeConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        return attribute == null ? null : StringUtils.join(attribute, ",");
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.isEmpty()) {
            return new LinkedList<>();
        } else {
            return new LinkedList<>(List.of(dbData.split(",")));
        }
    }
}
