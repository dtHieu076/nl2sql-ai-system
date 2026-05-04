package com.nl2sql_ai_system.nl2sql_backend.orchestrator.port;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.AiAnalysisResponse;

public interface FileExportService {
    /**
     * Xuất báo cáo ra định dạng file được yêu cầu.
     *
     * @param format Định dạng file (ví dụ: "excel", "pdf")
     * @param data   Dữ liệu báo cáo (bao gồm Markdown phân tích và cấu hình biểu
     *               đồ)
     * @return Mảng byte chứa nội dung file để Controller trả về cho người dùng tải
     *         xuống
     */
    byte[] exportReport(String format, AiAnalysisResponse data);
}