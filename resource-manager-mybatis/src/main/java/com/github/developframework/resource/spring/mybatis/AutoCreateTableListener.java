package com.github.developframework.resource.spring.mybatis;

import com.github.developframework.resource.spring.mybatis.annotation.Table;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * @author qiushui on 2020-06-01.
 */
@Slf4j
public class AutoCreateTableListener implements ApplicationListener<ContextRefreshedEvent> {

    @SneakyThrows
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, BaseDaoMapper> beansOfType = context.getBeansOfType(BaseDaoMapper.class);
        if (beansOfType.isEmpty()) {
            return;
        }
        for (BaseDaoMapper mapper : beansOfType.values()) {
            Class<?> entityClass = mapper.getEntityClass();
            if (MPO.class.isAssignableFrom(entityClass) && entityClass.isAnnotationPresent(Table.class)) {
                Table table = entityClass.getAnnotation(Table.class);
                mapper.createTable(entityClass);
                log.info("create table \"{}\"", table.name());
            }
        }
    }
}
