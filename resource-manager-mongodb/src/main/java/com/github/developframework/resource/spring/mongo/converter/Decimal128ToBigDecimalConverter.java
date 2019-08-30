package com.github.developframework.resource.spring.mongo.converter;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;

/**
 * @author qiushui on 2019-01-09.
 */
@ReadingConverter
public enum Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {

    INSTANCE;

    @Override
    public BigDecimal convert(Decimal128 source) {
        return source == null ? null : source.bigDecimalValue();
    }
}
