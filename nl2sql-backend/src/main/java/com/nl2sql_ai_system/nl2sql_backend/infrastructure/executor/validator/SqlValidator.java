package com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.validator;

import org.springframework.stereotype.Component;

@Component
public class SqlValidator {

    public void validate(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new SqlValidationException("Câu lệnh SQL không được để trống.");
        }

        String normalizedSql = sql.trim().toUpperCase();

        // 1. Chỉ cho phép lệnh SELECT
        if (!normalizedSql.startsWith("SELECT")) {
            throw new SqlValidationException("Chỉ cho phép thực thi câu lệnh SELECT.");
        }

        // 2. Chặn các từ khóa DML/DDL nguy hiểm
        String[] blacklistedKeywords = { "DROP", "DELETE", "UPDATE", "INSERT", "ALTER", "TRUNCATE", "GRANT", "REVOKE",
                "EXEC" };
        for (String keyword : blacklistedKeywords) {
            // Kiểm tra regex để đảm bảo đó là một từ độc lập (không phải tên cột như
            // 'drop_status')
            if (normalizedSql.matches(".*\\b" + keyword + "\\b.*")) {
                throw new SqlValidationException("Phát hiện từ khóa cấm trong câu lệnh SQL: " + keyword);
            }
        }
    }
}