package com.github.developframework.resource.spring.jpa.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.Set;

/**
 * 字符串列表属性转换器
 *
 * @author qiushui on 2019-08-13.
 */
public class StringSetAttributeConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> attribute) {
        return attribute == null ? null : StringUtils.join(attribute, ",");
    }

    @Override
    public Set<String> convertToEntityAttribute(String dbData) {
        return dbData != null ? Set.of(dbData.split(",")) : Set.of();
    }
}
