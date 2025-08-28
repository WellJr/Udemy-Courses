package com.udemy.primeiroprojetospringbatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary // Indica que este é o banco default
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource") // --> Significa que essa configuração irá usar tudo que tem prefixo spring.datasource no application
    public DataSource springDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource appDataSource() {
        return DataSourceBuilder.create().build();
    }

}
