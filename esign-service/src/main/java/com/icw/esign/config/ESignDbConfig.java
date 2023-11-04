package com.icw.esign.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "eSignEntityManagerFactory",
        transactionManagerRef = "eSignTransactionManager",
        basePackages ="com.icw.esign.repository")
public class ESignDbConfig {

    @Bean("eSignDataSource")
    @ConfigurationProperties(prefix="spring.esign.datasource")
    public DataSource geteSignDataSource(){
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "eSignEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean eSignEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("eSignDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.icw.esign.dao")
                .build();
    }

    @Bean(name = "eSignTransactionManager")
    public PlatformTransactionManager eSignTransactionManager(@Qualifier("eSignEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
