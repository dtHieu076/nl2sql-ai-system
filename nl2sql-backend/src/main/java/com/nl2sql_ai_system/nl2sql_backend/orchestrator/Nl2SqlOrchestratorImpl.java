package com.nl2sql_ai_system.nl2sql_backend.orchestrator;

import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClientService;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataSourceService;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.ExecutorService;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Nl2SqlOrchestratorImpl implements Nl2SqlOrchestrator {

    private final AiClientService aiClient;
    private final DataSourceService dataSourceService;
    private final MetadataService metadataService;
    private final ExecutorService executorService;

    @Override
    public String process(String intent, String role) {

        // 1. resolve datasource theo role
        Long dataSourceId = dataSourceService.resolve(role);

        // 2. lấy schema context
        String schema = metadataService.select(intent, role, dataSourceId);

        // 3. generate SQL (tạm thời stub)
        String sql = aiClient.generateSql(intent, schema);

        // 4. execute
        return executorService.execute(sql, dataSourceId);
    }
}