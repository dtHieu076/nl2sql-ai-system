package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResult {
    private String type; // "GENERAL", "DB_TEXT", "DB_REPORT"
    private String message; // Nội dung text hoặc Markdown trả lời
    private Object payload; // Chứa dữ liệu mở rộng (như ChartConfigDTO) nếu có
}