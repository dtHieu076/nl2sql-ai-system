package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto;

public enum enumQueryIntent {
    GENERAL, // Câu hỏi thường, khái niệm, giao tiếp
    DB_TEXT, // Truy vấn DB nhưng chỉ cần con số/chữ (VD: "Tổng doanh thu là bao nhiêu?")
    DB_REPORT // Truy vấn DB và cần vẽ biểu đồ/báo cáo phân tích
}