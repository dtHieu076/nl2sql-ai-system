package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

public interface DataSourceResolver {
    Long resolve(String role);
}