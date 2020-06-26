package com.github.developframework.resource.spring.jpa.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 字符串数组属性转换器
 *
 * @author qiushui on 2019-08-13.
 */
public class IntegerSetAttributeConverter implements AttributeConverter<Set<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Integer> attribute) {
        return attribute == null ? null : StringUtils.join(attribute, ",");
    }

    @Override
    public Set<Integer> convertToEntityAttribute(String dbData) {
        return dbData != null ? Stream.of(dbData.split(",")).map(Integer::parseInt).collect(Collectors.toSet()) : new HashSet<>();
    }
}
