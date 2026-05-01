package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

public interface MetadataService {
    String select(String intent, String role, Long dataSourceId);

    void sync(Long dataSourceId);

    void syncAll();
}