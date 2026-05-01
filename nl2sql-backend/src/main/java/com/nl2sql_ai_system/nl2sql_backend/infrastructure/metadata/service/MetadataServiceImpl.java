package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.service;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataSourceService;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.MetadataService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final DataSourceService dataSourceService;
    private final SchemaVectorService schemaVectorService;

    @Override
    public String select(String intent, String role, Long dataSourceId) {
        // 1. Truy vấn Vector DB lấy ra top 5 bảng liên quan nhất đến câu hỏi
        List<String> relevantSchemas = schemaVectorService.findRelevantSchemas(intent, dataSourceId, 5);

        // 2. Gộp chúng lại thành một chuỗi context để trả về cho Orchestrator
        if (relevantSchemas.isEmpty()) {
            return "No relevant tables found.";
        }

        StringBuilder contextBuilder = new StringBuilder("tables:\n");
        for (String schema : relevantSchemas) {
            contextBuilder.append(schema).append("\n");
        }

        return contextBuilder.toString();
    }

    @Override
    public void sync(Long dataSourceId) {

        DataSource ds = dataSourceService.findById(dataSourceId);

        if (ds == null) {
            throw new RuntimeException("Datasource not found");
        }

        syncDatasource(ds);
    }

    @Override
    public void syncAll() {

        List<DataSource> list = dataSourceService.findAll();

        for (DataSource ds : list) {
            syncDatasource(ds);
        }
    }

    @Transactional // Rất quan trọng để đảm bảo Rollback nếu có lỗi xảy ra giữa chừng
    private void syncDatasource(DataSource ds) {
        // log.info("Bắt đầu đồng bộ schema cho DataSource ID: {}", ds.getId());

        // try {
        //     // 1. Lấy schema từ DB thực tế
        //     List<TableMetadata> tables = schemaIntrospectionService.extract(
        //             ds.getJdbcUrl(),
        //             ds.getUsername(),
        //             ds.getPassword());

        //     if (tables == null || tables.isEmpty()) {
        //         log.warn("Không tìm thấy bảng nào trong DataSource ID: {}", ds.getId());
        //         return;
        //     }

        //     // 2. Lưu vào DB PostgreSQL của hệ thống (Xóa cũ, lưu mới)
        //     tableMetadataRepository.deleteByDataSourceId(ds.getId());
        //     tables.forEach(t -> t.setDataSource(ds));
        //     tableMetadataRepository.saveAll(tables);

        //     // 3. Ghi vào Vector DB (Qdrant)
        //     // Xóa các vector cũ của DataSource này trước để tránh trùng lặp khi sync lại
        //     schemaVectorService.deleteByDataSourceId(ds.getId());
        //     // Index các vector mới
        //     schemaVectorService.indexTables(tables, ds.getId());

        //     // 4. Cập nhật thời gian sync
        //     ds.setLastSyncAt(LocalDateTime.now());
        //     ds.setStatus(DataSourceStatus.ACTIVE); // Giả sử bạn có Enum trạng thái
        //     dataSourceRepository.save(ds);

        //     log.info("Đồng bộ thành công {} bảng cho DataSource ID: {}", tables.size(), ds.getId());

        // } catch (Exception e) {
        //     log.error("Lỗi khi đồng bộ DataSource ID: {}", ds.getId(), e);

        //     // Có thể update trạng thái DataSource thành FAILED ở đây nếu cần
        //     // ds.setStatus(DataSourceStatus.FAILED);
        //     // dataSourceRepository.save(ds);

        //     throw new RuntimeException("Sync failed for DataSource " + ds.getId(), e);
        // }
    }
}