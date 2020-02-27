package com.ifeng.recom.mixrecall.common.dao.mysql;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by lilg1 on 2018/3/30.
 */
@Configuration
public class MybatisConfig {

    @Bean
    public DataSource getDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://10.80.134.20:3306/al_basic_data");
        dataSource.setUsername("al_basic_data");
        dataSource.setPassword("RpJc4ujmnbrgdHGe");
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(getDataSource());
        return sessionFactory.getObject();
    }

    @Bean(name = "sqlSessionFactoryForSansu")
    public SqlSessionFactory sqlSessionFactoryForSansu() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(getDataSourceForSansu());
        return sessionFactory.getObject();
    }

    @Bean
    public DataSource getDataSourceForSansu() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://10.80.134.20:3310/NewsAssess");
        dataSource.setUsername("NewsAssess_r");
        dataSource.setPassword("9tFGxg6QQHcuxjKw");
        return dataSource;
    }
}
