package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.strategy.Impl;

import org.springframework.stereotype.Component;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.AiAnalysisResponse;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.ChatResult;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.enumQueryIntent;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.strategy.ChatStrategy;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClientService;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataAnalyzerService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DbReportStrategyImpl implements ChatStrategy {

    private final AiClientService aiClientService; // Inject Port
    private final DataAnalyzerService dataAnalyzerService;

    @Override
    public enumQueryIntent getSupportedIntent() {
        return enumQueryIntent.DB_REPORT;
    }

    @Override
    public ChatResult process(String userQuery) {
        // 1. Giao tiếp qua Port để lấy data thô
        String rawResponse = aiClientService.chatWithDbTool(userQuery);

        // 2. Phân tích ra Báo cáo + Biểu đồ
        AiAnalysisResponse analysis = dataAnalyzerService.analyze(userQuery, rawResponse);

        // 3. Đóng gói
        return ChatResult.builder()
                .type(enumQueryIntent.DB_REPORT.name())
                .message(analysis.markdownReport())
                .payload(analysis.chartConfig())
                .build();
    }
}