package com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.service;

import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.ExecutorService;
import org.springframework.stereotype.Component;

@Component
public class ExecutorServiceImpl implements ExecutorService {

    @Override
    public String execute(String sql, Long dataSourceId) {

        return """
                {
                  "status": "success",
                  "dataSourceId": %d,
                  "sql": "%s",
                  "data": [{"mock": true}]
                }
                """.formatted(dataSourceId, sql);
    }
}