package com.github.developframework.resource.spring.mongo.converter;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

/**
 * @author qiushui on 2019-01-09.
 */
@WritingConverter
public enum BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {

    INSTANCE;

    @Override
    public Decimal128 convert(BigDecimal source) {
        return source == null ? null : new Decimal128(source);
    }
}
