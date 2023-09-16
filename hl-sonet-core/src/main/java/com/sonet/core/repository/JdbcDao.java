package com.sonet.core.repository;

import com.sonet.core.configuration.jdbc.ReadOnlyDataSource;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@AllArgsConstructor
public class JdbcDao {

    private final ReadOnlyDataSource readOnlyDataSource;

    @SneakyThrows
    public void executeSelect(final String sql, ThrowingConsumer<PreparedStatement, SQLException> queryParamsConsumer) {
        @Cleanup Connection con = readOnlyDataSource.getConnection();
        @Cleanup PreparedStatement statement = con.prepareStatement(sql);
        if (queryParamsConsumer != null) {
            queryParamsConsumer.accept(statement);
        }
        statement.execute();
    }

    @SneakyThrows
    public <R> R  executeSelect(final String sql, ThrowingFunction<ResultSet, R, SQLException> resultMappingFunction) {
        @Cleanup Connection con = readOnlyDataSource.getConnection();
        @Cleanup PreparedStatement statement = con.prepareStatement(sql);
        @Cleanup ResultSet resultSet = statement.executeQuery();
        return resultMappingFunction.apply(resultSet);
    }

    @SneakyThrows
    public <R> R  executeSelect(final String sql, ThrowingFunction<ResultSet, R, SQLException> resultMappingFunction, int queryTimeout) {
        @Cleanup Connection con = readOnlyDataSource.getConnection();
        @Cleanup PreparedStatement statement = con.prepareStatement(sql);
        statement.setQueryTimeout(queryTimeout);
        @Cleanup ResultSet resultSet = statement.executeQuery();
        return resultMappingFunction.apply(resultSet);
    }

    @SneakyThrows
    public <R> R executeSelect(final String sql, ThrowingConsumer<PreparedStatement, SQLException> queryParamsConsumer, ThrowingFunction<ResultSet, R, SQLException> resultMappingFunction) {
        @Cleanup Connection con = readOnlyDataSource.getConnection();
        @Cleanup PreparedStatement statement = con.prepareStatement(sql);
        if (queryParamsConsumer != null) {
            queryParamsConsumer.accept(statement);
        }
        @Cleanup ResultSet resultSet = statement.executeQuery();
        return resultMappingFunction.apply(resultSet);
    }

    @SneakyThrows
    public void processNonAutocommit(ThrowingConsumer<Connection, SQLException> func) {
        @Cleanup Connection con = readOnlyDataSource.getConnection();
        if (con.getAutoCommit()) {
            con.setAutoCommit(false);
            try {
                func.accept(con);
            } finally {
                con.setAutoCommit(true);
            }
        } else {
            func.accept(con);
        }
    }
}
