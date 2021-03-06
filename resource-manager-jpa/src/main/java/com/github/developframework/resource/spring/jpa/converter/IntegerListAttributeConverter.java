package com.github.developframework.resource.spring.jpa.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 字符串数组属性转换器
 *
 * @author qiushui on 2019-08-13.
 */
public class IntegerListAttributeConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        return attribute == null ? null : StringUtils.join(attribute, ",");
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        } else if (dbData.isEmpty()) {
            return new LinkedList<>();
        } else {
            return Stream.of(dbData.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        }
    }
}
