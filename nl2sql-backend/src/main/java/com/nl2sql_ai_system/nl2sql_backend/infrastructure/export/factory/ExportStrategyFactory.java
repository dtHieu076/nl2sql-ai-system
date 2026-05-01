package com.nl2sql_ai_system.nl2sql_backend.infrastructure.export.factory;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.export.strategy.FileExportStrategy;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ExportStrategyFactory {

    private final Map<String, FileExportStrategy> strategies;

    // Spring tự động inject tất cả các class implements FileExportStrategy vào List
    // này
    public ExportStrategyFactory(List<FileExportStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(FileExportStrategy::getFileType, strategy -> strategy));
    }

    public FileExportStrategy getStrategy(String fileType) {
        FileExportStrategy strategy = strategies.get(fileType.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Không hỗ trợ định dạng xuất file: " + fileType);
        }
        return strategy;
    }
}