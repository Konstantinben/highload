package com.sonet.core.configuration;

import com.sonet.core.configuration.jdbc.DataSourceType;
import com.sonet.core.configuration.jdbc.MasterSlaveRoutingDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfiguration {

    @Bean
    public DataSource dataSource() {
        DataSource defaultDataSource = dataSourceProperties().initializeDataSourceBuilder().build();
        DataSource readOnlyDataSource = readOnlyDataSourceProperties().initializeDataSourceBuilder().build();
        MasterSlaveRoutingDataSource masterSlaveRoutingDataSource = new MasterSlaveRoutingDataSource();
        masterSlaveRoutingDataSource.setTargetDataSources(
                Map.of(DataSourceType.UPDATABLE, defaultDataSource, DataSourceType.READONLY, readOnlyDataSource));
        // Set as all transaction point to master
        masterSlaveRoutingDataSource.setDefaultTargetDataSource(defaultDataSource);
        return masterSlaveRoutingDataSource;
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "readOnlyDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource-readonly")
    public DataSourceProperties readOnlyDataSourceProperties() {
        return new DataSourceProperties();
    }
}
