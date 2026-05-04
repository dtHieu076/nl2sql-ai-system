package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

import java.util.List;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;

public interface DataSourceService {
    Long resolve(String role);

    DataSource create(DataSource ds);

    List<DataSource> findAll();

    DataSource findById(Long id);

    DataSource update(DataSource ds);
}