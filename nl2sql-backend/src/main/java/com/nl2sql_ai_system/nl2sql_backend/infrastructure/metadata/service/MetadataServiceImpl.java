package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.service;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.enumDataSourceStatus;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.ColumnMetadata;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableRelationship;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.introspection.SchemaIntrospectionService;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.introspection.SchemaSnapshot;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.repository.ColumnMetadataRepository;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.repository.TableMetadataRepository;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.repository.TableRelationshipRepository;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataSourceService;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.MetadataService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final DataSourceService dataSourceService;
    private final SchemaVectorService schemaVectorService;
    private final SchemaIntrospectionService schemaIntrospectionService;
    private final TableMetadataRepository tableMetadataRepository;
    private final ColumnMetadataRepository columnMetadataRepository;
    private final TableRelationshipRepository relationshipRepository;

    @Override
    public String select(String intent, String role, Long dataSourceId) {

        // gọi search mới (Gemini embedding + Qdrant native)
        List<String> relevantSchemas = schemaVectorService.search(intent, dataSourceId, 5);

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
    @Transactional
    public void sync(Long dataSourceId) {

        DataSource ds = dataSourceService.findById(dataSourceId);

        if (ds == null) {
            throw new RuntimeException("Datasource not found");
        }

        syncDatasource(ds);
    }

    @Override
    @Transactional
    public void syncAll() {

        List<DataSource> list = dataSourceService.findAll();

        for (DataSource ds : list) {
            syncDatasource(ds);
        }
    }

    @Transactional
    private void syncDatasource(DataSource ds) {
        log.info("Bắt đầu đồng bộ schema cho DataSource ID: {}", ds.getId());

        try {
            // 1. Extract full metadata (tables + columns + relationships)
            SchemaSnapshot snapshot = schemaIntrospectionService.extractFull(
                    ds.getJdbcUrl(),
                    ds.getUsername(),
                    ds.getPassword());

            List<TableMetadata> tables = snapshot.getTables();
            List<TableRelationship> relationships = snapshot.getRelationships();

            if (tables == null || tables.isEmpty()) {
                log.warn("Không tìm thấy bảng nào trong DataSource ID: {}", ds.getId());
                return;
            }

            // 2. XÓA DỮ LIỆU CŨ (đúng thứ tự)
            relationshipRepository.deleteByDataSourceId(ds.getId());

            List<TableMetadata> oldTables = tableMetadataRepository.findByDataSourceId(ds.getId());
            for (TableMetadata t : oldTables) {
                columnMetadataRepository.deleteAll(
                        columnMetadataRepository.findByTableId(t.getId()));
            }
            tableMetadataRepository.deleteAll(oldTables);

            // 3. GẮN DataSource + persist TABLE trước
            for (TableMetadata table : tables) {
                table.setDataSource(ds);
            }

            List<TableMetadata> savedTables = tableMetadataRepository.saveAll(tables);

            // 4. MAP lại table theo tên (để gán FK đúng)
            Map<String, TableMetadata> tableMap = savedTables.stream()
                    .collect(Collectors.toMap(TableMetadata::getTableName, t -> t));

            // 5. SAVE COLUMN (và build map column)
            Map<String, ColumnMetadata> columnMap = new HashMap<>();

            for (TableMetadata table : tables) {
                TableMetadata persistedTable = tableMap.get(table.getTableName());

                if (table.getColumns() != null) {
                    for (ColumnMetadata col : table.getColumns()) {
                        col.setTable(persistedTable);
                    }

                    List<ColumnMetadata> savedCols = columnMetadataRepository.saveAll(table.getColumns());

                    for (ColumnMetadata col : savedCols) {
                        String key = table.getTableName() + "." + col.getColumnName();
                        columnMap.put(key, col);
                    }
                }
            }

            // 6. SAVE RELATIONSHIP (phải map lại entity đã persist)
            for (TableRelationship rel : relationships) {
                rel.setDataSource(ds);

                TableMetadata sourceTable = tableMap.get(rel.getSourceTable().getTableName());
                TableMetadata targetTable = tableMap.get(rel.getTargetTable().getTableName());

                ColumnMetadata sourceColumn = columnMap.get(sourceTable.getTableName() + "." +
                        rel.getSourceColumn().getColumnName());

                ColumnMetadata targetColumn = columnMap.get(targetTable.getTableName() + "." +
                        rel.getTargetColumn().getColumnName());

                rel.setSourceTable(sourceTable);
                rel.setTargetTable(targetTable);
                rel.setSourceColumn(sourceColumn);
                rel.setTargetColumn(targetColumn);
            }

            relationshipRepository.saveAll(relationships);

            // 7. VECTOR INDEX (cực kỳ quan trọng: include relationship)
            schemaVectorService.deleteByDataSourceId(ds.getId());
            schemaVectorService.indexTablesWithRelations(savedTables, relationships, ds.getId());

            // 8. UPDATE trạng thái
            ds.setLastSyncAt(LocalDateTime.now());
            ds.setStatus(enumDataSourceStatus.ACTIVE);
            dataSourceService.update(ds);

            log.info("Đồng bộ thành công {} bảng và {} quan hệ cho DataSource ID: {}",
                    savedTables.size(), relationships.size(), ds.getId());

        } catch (Exception e) {
            log.error("Lỗi khi đồng bộ DataSource ID: {}", ds.getId(), e);

            try {
                ds.setStatus(enumDataSourceStatus.INACTIVE);
                dataSourceService.update(ds);
            } catch (Exception ex) {
                log.error("Không thể cập nhật trạng thái FAILED", ex);
            }

            throw new RuntimeException("Sync failed for DataSource " + ds.getId(), e);
        }
    }
}