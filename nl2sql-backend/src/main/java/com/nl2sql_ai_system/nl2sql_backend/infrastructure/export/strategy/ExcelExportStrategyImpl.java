package com.nl2sql_ai_system.nl2sql_backend.infrastructure.export.strategy;

import org.springframework.stereotype.Component;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.AiAnalysisResponse;

@Component
public class ExcelExportStrategyImpl implements FileExportStrategy {

    @Override
    public String getFileType() {
        return "excel";
    }

    @Override
    public byte[] export(AiAnalysisResponse data) {
        // Thực tế ở đây bạn sẽ dùng thư viện Apache POI (XSSFWorkbook)
        // để ghi data.markdownReport và cấu hình biểu đồ vào file .xlsx

        System.out.println("Đang tạo file Excel...");
        String dummyContent = "Nội dung file Excel cho báo cáo: " + data.markdownReport();
        return dummyContent.getBytes();
    }
}