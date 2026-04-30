package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

public interface SchemaSelector {
    String select(String intent, String role, Long dataSourceId);
}