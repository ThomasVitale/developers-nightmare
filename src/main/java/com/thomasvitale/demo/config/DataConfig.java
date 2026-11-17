package com.thomasvitale.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;

@Configuration(proxyBeanMethods = false)
class DataConfig {

    @Bean
    JdbcPostgresDialect jdbcDialect() {
        return JdbcPostgresDialect.INSTANCE;
    }

}
