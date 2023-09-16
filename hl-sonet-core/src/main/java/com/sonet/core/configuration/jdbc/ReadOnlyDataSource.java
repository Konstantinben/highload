package com.sonet.core.configuration.jdbc;


import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ReadOnlyDataSource {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private DataSource dataSource;

    public Connection getConnection () throws SQLException {
        if (dataSource == null) {
            final PGSimpleDataSource pgDataSource = new PGSimpleDataSource(); // connection = DriverManager.getConnection(JDBC_URL_FORMAT, POSTGRES_USERNAME, POSTGRES_PASSWORD);
            pgDataSource.setUrl(datasourceUrl); //final String url = "jdbc:postgresql://localhost:5432/bird_encyclopedia?user=dbadmin&password=my-secret-password";
            pgDataSource.setUser(username);
            pgDataSource.setPassword(password);
            dataSource = pgDataSource;
        }
        return dataSource.getConnection();
    }
}