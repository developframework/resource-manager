package com.github.developframework.resource.spring.mybatis;

import develop.toolkit.base.components.SnowflakeIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiushui on 2020-06-01.
 */
@Configuration
public class ResourceMybatisAutoConfiguration {

    @Bean
    public AutoCreateTableListener autoCreateRableListener() {
        return new AutoCreateTableListener();
    }

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker(
            @Value("${snowflake.workerId:1}") long workerId,
            @Value("${snowflake.datacenterId:1}") long datacenterId
    ) {
        return new SnowflakeIdWorker(workerId, datacenterId);
    }
}
