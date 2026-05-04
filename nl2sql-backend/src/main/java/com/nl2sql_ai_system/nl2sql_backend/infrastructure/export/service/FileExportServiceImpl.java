package com.nl2sql_ai_system.nl2sql_backend.infrastructure.export.service;

import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.FileExportService;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.AiAnalysisResponse;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.export.factory.ExportStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileExportServiceImpl implements FileExportService {

    private final ExportStrategyFactory factory;

    @Override
    public byte[] exportReport(String format, AiAnalysisResponse data) {
        // Lấy đúng strategy (excel hoặc pdf) và gọi hàm export
        return factory.getStrategy(format).export(data);
    }
}