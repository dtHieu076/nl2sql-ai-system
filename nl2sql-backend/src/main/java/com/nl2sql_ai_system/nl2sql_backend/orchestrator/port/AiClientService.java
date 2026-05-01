package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

import com.nl2sql_ai_system.nl2sql_backend.chat.strategy.enumQueryIntent;

public interface AiClientService {
    String chat(String userQuery); // Trả lời chay (General)

    String chatWithDbTool(String userQuery); // Trả lời có kèm Tool gọi DB

    String generateSql(String userIntent, String schemaContext);

    enumQueryIntent classifyIntent(String userQuery); // AI phân loại câu hỏi
}