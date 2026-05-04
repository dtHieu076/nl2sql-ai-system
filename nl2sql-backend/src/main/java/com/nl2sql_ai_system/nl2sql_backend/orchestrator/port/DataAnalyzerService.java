package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.AiAnalysisResponse;

public interface DataAnalyzerService {
    /**
     * Nhận câu hỏi của user và dữ liệu thô từ DB, trả về báo cáo và biểu đồ.
     */
    AiAnalysisResponse analyze(String userIntent, String rawJsonData);
}