package com.github.developframework.resource.spring.jpa.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.util.stream.Stream;

/**
 * 字符串数组属性转换器
 *
 * @author qiushui on 2019-08-13.
 */
public class LongArrayAttributeConverter implements AttributeConverter<Long[], String> {

    @Override
    public String convertToDatabaseColumn(Long[] attribute) {
        return attribute == null ? null : StringUtils.join(attribute, ",");
    }

    @Override
    public Long[] convertToEntityAttribute(String dbData) {
        return dbData != null ? Stream.of(dbData.split(",")).map(Long::valueOf).toArray(Long[]::new) : new Long[0];
    }
}
