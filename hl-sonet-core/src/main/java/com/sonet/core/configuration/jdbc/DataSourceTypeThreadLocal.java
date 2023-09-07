package com.sonet.core.configuration.jdbc;

public class DataSourceTypeThreadLocal {
    private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

    public static void set(DataSourceType dataSourceType) {
        CONTEXT.set(dataSourceType);
    }

    public static DataSourceType getDataSourceType() {
        return CONTEXT.get();
    }

    public static void reset() {
        CONTEXT.set(DataSourceType.UPDATABLE);
    }
}
