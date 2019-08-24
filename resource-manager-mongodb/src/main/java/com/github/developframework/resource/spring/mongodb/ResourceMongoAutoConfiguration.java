package com.github.developframework.resource.spring.mongodb;

import com.github.developframework.resource.spring.mongodb.converter.BigDecimalToDecimal128Converter;
import com.github.developframework.resource.spring.mongodb.converter.Decimal128ToBigDecimalConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

/**
 * @author qiushui on 2019-08-24.
 */
@Configuration
public class ResourceMongoAutoConfiguration {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                BigDecimalToDecimal128Converter.INSTANCE,
                Decimal128ToBigDecimalConverter.INSTANCE
        ));
    }
}
