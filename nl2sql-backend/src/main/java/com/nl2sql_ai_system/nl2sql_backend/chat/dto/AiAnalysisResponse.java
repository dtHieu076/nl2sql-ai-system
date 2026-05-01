package com.nl2sql_ai_system.nl2sql_backend.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record AiAnalysisResponse(
        @JsonProperty(required = true) @JsonPropertyDescription("Báo cáo phân tích dữ liệu chi tiết, nêu bật xu hướng, so sánh. Trình bày bằng định dạng Markdown.") String markdownReport,

        @JsonProperty(required = false) @JsonPropertyDescription("Cấu hình biểu đồ NẾU câu hỏi ngụ ý cần xem biểu đồ (so sánh, tỷ lệ, xu hướng). Nếu không cần biểu đồ thì trả về null.") ChartConfigDTO chartConfig) {
}