package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

public record ChartConfigDTO(
        @JsonPropertyDescription("Loại biểu đồ: 'bar' (cột), 'pie' (tròn), 'line' (đường)") String type,

        @JsonPropertyDescription("Danh sách các nhãn trục X (ví dụ: các tháng, các phòng ban)") List<String> labels,

        @JsonPropertyDescription("Dữ liệu của biểu đồ") List<DatasetDTO> datasets) {
    public record DatasetDTO(
            @JsonPropertyDescription("Tên của tập dữ liệu (ví dụ: Doanh thu, Chi phí)") String label,

            @JsonPropertyDescription("Mảng các giá trị số tương ứng với các nhãn") List<Double> data) {
    }
}