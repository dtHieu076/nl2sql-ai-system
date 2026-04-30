package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.service;

import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.SchemaSelector;
import org.springframework.stereotype.Component;

@Component
public class SchemaSelectorImpl implements SchemaSelector {
    @Override
    public String select(String intent, String role, Long dataSourceId) {

        return """
                tables:
                - employees(id, name, salary)
                - orders(id, amount, created_at)
                """;
    }
}