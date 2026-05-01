package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

public interface AiClientService {
    String chat(String userQuery);

    String generateSql(String userIntent, String schemaContext);
}
