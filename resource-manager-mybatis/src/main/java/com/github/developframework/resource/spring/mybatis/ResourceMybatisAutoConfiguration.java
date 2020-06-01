package com.github.developframework.resource.spring.mybatis;

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
}
