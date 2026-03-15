package com.thomasvitale.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;

@Configuration(proxyBeanMethods = false)
public class DataConfig {

    // See: https://github.com/spring-projects/spring-boot/issues/48240
    @Bean
    JdbcPostgresDialect jdbcDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }

}
