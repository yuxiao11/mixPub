package com.ifeng.recom.mixrecall.common.dao.mysql;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author pandeng
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "weMdiaSource")
    @Qualifier("weMdiaSource")
    @Primary
    @ConfigurationProperties(prefix="spring.datasource.weMdia")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sansuSource")
    @Qualifier("sansuSource")
    @ConfigurationProperties(prefix="spring.datasource.sansu")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "weMdiaJdbcTemplate")
    public JdbcTemplate weMdiaJdbcTemplate(
            @Qualifier("weMdiaSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "sansuJdbcTemplate")
    public JdbcTemplate sansuJdbcTemplate(
            @Qualifier("sansuSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}