package com.github.developframework.resource.spring.jpa;

import develop.toolkit.base.components.SnowflakeIdWorker;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;

/**
 * @author qiushui on 2019-08-24.
 */
@Configuration
public class ResourceJpaAutoConfiguration {

    @Primary
    @Bean
    public JpaTransactionManager transactionManager(ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
        return transactionManager;
    }

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker(
            @Value("${snowflake.workerId:1}") long workerId,
            @Value("${snowflake.datacenterId:1}") long datacenterId
    ) {
        return new SnowflakeIdWorker(workerId, datacenterId);
    }
}
