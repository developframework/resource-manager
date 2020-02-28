package com.github.developframework.resource.spring.mongo;

import com.github.developframework.resource.spring.mongo.converter.BigDecimalToDecimal128Converter;
import com.github.developframework.resource.spring.mongo.converter.Decimal128ToBigDecimalConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

/**
 * @author qiushui on 2019-08-24.
 */
@EnableTransactionManagement
@Configuration
public class ResourceMongoAutoConfiguration {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                BigDecimalToDecimal128Converter.INSTANCE,
                Decimal128ToBigDecimalConverter.INSTANCE
        ));
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}
