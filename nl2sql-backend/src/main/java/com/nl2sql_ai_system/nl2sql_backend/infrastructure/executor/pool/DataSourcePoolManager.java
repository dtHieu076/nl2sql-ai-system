package com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.pool;

// Chỉ import DataSource của Entity
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataSourcePoolManager {

    private final ConcurrentHashMap<Long, HikariDataSource> poolCache = new ConcurrentHashMap<>();

    // Sử dụng tên đầy đủ javax.sql.DataSource ở đây để tránh trùng lặp
    public javax.sql.DataSource getDataSource(DataSource dsInfo) {
        return poolCache.computeIfAbsent(dsInfo.getId(), key -> {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(dsInfo.getDriverClassName());
            config.setJdbcUrl(dsInfo.getJdbcUrl());
            config.setUsername(dsInfo.getUsername());
            config.setPassword(dsInfo.getPassword());

            config.setMaximumPoolSize(5);
            config.setMinimumIdle(1);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);

            return new HikariDataSource(config);
        });
    }

    public void removeDataSource(Long dataSourceId) {
        HikariDataSource ds = poolCache.remove(dataSourceId);
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }
}