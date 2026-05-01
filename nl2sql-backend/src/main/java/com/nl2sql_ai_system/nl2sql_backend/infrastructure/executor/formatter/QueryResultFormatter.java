package com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class QueryResultFormatter {

    private final ObjectMapper objectMapper;
    private static final int MAX_ROWS = 50; // Giới hạn số dòng tối đa AI cần đọc

    public String format(String sql, Long dataSourceId, List<Map<String, Object>> rows) {
        try {
            boolean isTruncated = rows.size() > MAX_ROWS;
            List<Map<String, Object>> dataToReturn = isTruncated ? rows.subList(0, MAX_ROWS) : rows;

            ExecutionResult result = new ExecutionResult(
                    "success",
                    dataSourceId,
                    sql,
                    dataToReturn,
                    rows.size(),
                    isTruncated);
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return String.format("{\"status\": \"error\", \"message\": \"Lỗi format dữ liệu: %s\"}", e.getMessage());
        }
    }

    private record ExecutionResult(
            String status,
            Long dataSourceId,
            String sql,
            Object data,
            int totalFetched,
            boolean isTruncated) {
    }
}