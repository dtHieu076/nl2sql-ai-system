package com.nl2sql_ai_system.nl2sql_backend.orchestrator;

import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataSourceResolver;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.QueryExecutor;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.SchemaSelector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Nl2SqlOrchestratorImpl implements Nl2SqlOrchestrator {

    private final DataSourceResolver dataSourceResolver;
    private final SchemaSelector schemaSelector;
    private final QueryExecutor queryExecutor;

    @Override
    public String process(String intent, String role) {

        // 1. resolve datasource theo role
        Long dataSourceId = dataSourceResolver.resolve(role);

        // 2. lấy schema context
        String schema = schemaSelector.select(intent, role, dataSourceId);

        // 3. generate SQL (tạm thời stub)
        String sql = generateSql(intent, schema);

        // 4. execute
        return queryExecutor.execute(sql, dataSourceId);
    }

    private String generateSql(String intent, String schema) {

        if (intent.toLowerCase().contains("doanh thu")) {
            return "SELECT SUM(amount) AS total_revenue FROM orders";
        }

        return "SELECT * FROM employees LIMIT 5";
    }
}