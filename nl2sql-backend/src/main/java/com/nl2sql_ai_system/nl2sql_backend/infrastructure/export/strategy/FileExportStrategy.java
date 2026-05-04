package com.nl2sql_ai_system.nl2sql_backend.infrastructure.export.strategy;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.AiAnalysisResponse;

public interface FileExportStrategy {
    // Mỗi loại file sẽ có một định danh (VD: "excel", "pdf")
    String getFileType();

    // Hàm thực hiện xuất file, trả về mảng byte để tải xuống
    byte[] export(AiAnalysisResponse data);
}