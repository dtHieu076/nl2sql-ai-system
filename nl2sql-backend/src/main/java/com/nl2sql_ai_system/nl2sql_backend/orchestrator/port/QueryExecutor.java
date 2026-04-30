package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

public interface QueryExecutor {
    String execute(String sql, Long dataSourceId);
}