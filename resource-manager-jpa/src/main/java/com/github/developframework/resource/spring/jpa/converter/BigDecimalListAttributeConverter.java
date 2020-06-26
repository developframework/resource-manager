package com.github.developframework.resource.spring.jpa.converter;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 字符串数组属性转换器
 *
 * @author qiushui on 2019-08-13.
 */
public class BigDecimalListAttributeConverter implements AttributeConverter<List<BigDecimal>, String> {

    @Override
    public String convertToDatabaseColumn(List<BigDecimal> attribute) {
        return attribute == null ? null : StringUtils.join(attribute, ",");
    }

    @Override
    public List<BigDecimal> convertToEntityAttribute(String dbData) {
        return dbData != null ? Stream.of(dbData.split(",")).map(BigDecimal::new).collect(Collectors.toList()) : new ArrayList<>();
    }
}
