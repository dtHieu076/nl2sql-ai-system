package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.service;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableRelationship;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.qdrant.client.grpc.JsonWithInt.Value;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaVectorService {

        private final QdrantClient qdrantClient;
        private final GeminiEmbeddingClient embeddingClient;

        // Nên đưa collection name vào application.yml nếu có thể, nhưng để hằng số vẫn
        // ổn
        private static final String COLLECTION = "schema_metadata";

        /**
         * Index schema (Đã tối ưu dùng Batch Insert)
         */
        public void indexTables(List<TableMetadata> tables, Long dataSourceId) {
                if (tables == null || tables.isEmpty()) {
                        log.warn("No tables provided to index.");
                        return;
                }

                List<PointStruct> pointsToUpsert = new ArrayList<>();

                for (TableMetadata table : tables) {
                        String content = "Table " + table.getTableName();
                        List<Float> vector = embeddingClient.embed(content);
                        String uuid = UUID.randomUUID().toString();

                        PointStruct point = PointStruct.newBuilder()
                                        .setId(PointId.newBuilder().setUuid(uuid).build())
                                        .setVectors(Vectors.newBuilder()
                                                        .setVector(Vector.newBuilder().addAllData(vector).build())
                                                        .build())
                                        .putPayload("dataSourceId",
                                                        Value.newBuilder().setIntegerValue(dataSourceId).build())
                                        .putPayload("tableName",
                                                        Value.newBuilder().setStringValue(table.getTableName()).build())
                                        .build();

                        pointsToUpsert.add(point);
                }

                try {
                        // ✅ FIX 1: Upsert tất cả point trong 1 lần gọi (Batch operation)
                        qdrantClient.upsertAsync(COLLECTION, pointsToUpsert).get();
                        log.info("Successfully indexed {} tables for dataSourceId: {}", tables.size(), dataSourceId);
                } catch (InterruptedException | ExecutionException e) {
                        log.error("Failed to index tables to Qdrant", e);
                        Thread.currentThread().interrupt(); // Khôi phục trạng thái ngắt
                        throw new RuntimeException("Lỗi khi lưu vector vào Qdrant", e);
                }
        }

        /**
         * Search
         */
        public List<String> search(String query, Long dataSourceId, int topK) {
                List<Float> vector = embeddingClient.embed(query);

                Filter filter = Filter.newBuilder()
                                .addMust(Condition.newBuilder()
                                                .setField(FieldCondition.newBuilder()
                                                                .setKey("dataSourceId")
                                                                .setMatch(Match.newBuilder()
                                                                                .setInteger(dataSourceId)
                                                                                .build())
                                                                .build())
                                                .build())
                                .build();

                SearchPoints search = SearchPoints.newBuilder()
                                .setCollectionName(COLLECTION)
                                .addAllVector(vector)
                                .setFilter(filter)
                                .setLimit(topK)
                                .build();

                try {
                        List<ScoredPoint> results = qdrantClient.searchAsync(search).get();

                        for (ScoredPoint p : results) {
                                Map<String, Value> payload = p.getPayloadMap();

                                log.info("PointId: {}", p.getId());
                                log.info("Payload keys: {}", payload.keySet());
                                log.info("Payload full: {}", payload);
                        }
                        return results.stream()
                                        .<String>map(p -> { // Chỉ định rõ kiểu trả về là String
                                                Map<String, Value> payload = p.getPayloadMap();

                                                if (payload.containsKey("tableName")) {
                                                        return payload.get("tableName").getStringValue();
                                                }

                                                log.warn("Point {} missing 'tableName' payload", p.getId());
                                                return null;
                                        })
                                        .filter(name -> name != null)
                                        .toList();

                } catch (InterruptedException | ExecutionException e) {
                        log.error("Failed to search in Qdrant", e);
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Lỗi khi tìm kiếm vector", e);
                }
        }

        public void indexTablesWithRelations(List<TableMetadata> tables,
                        List<TableRelationship> relationships,
                        Long dataSourceId) {

                if (tables == null || tables.isEmpty()) {
                        log.warn("No tables provided to index.");
                        return;
                }

                List<PointStruct> pointsToUpsert = new ArrayList<>();

                for (TableMetadata table : tables) {

                        // 🔥 Build content giàu ngữ cảnh
                        StringBuilder content = new StringBuilder();
                        content.append("Table: ").append(table.getTableName());

                        // columns
                        if (table.getColumns() != null && !table.getColumns().isEmpty()) {
                                content.append(" | Columns: ");
                                table.getColumns().forEach(c -> content.append(c.getColumnName()).append(", "));
                        }

                        // relationships (join info)
                        content.append(" | Relations: ");
                        for (TableRelationship rel : relationships) {

                                if (rel.getSourceTable().getTableName().equals(table.getTableName())) {
                                        content.append(rel.getSourceTable().getTableName())
                                                        .append(".")
                                                        .append(rel.getSourceColumn().getColumnName())
                                                        .append(" -> ")
                                                        .append(rel.getTargetTable().getTableName())
                                                        .append(".")
                                                        .append(rel.getTargetColumn().getColumnName())
                                                        .append(" ; ");
                                }
                        }

                        // embedding
                        List<Float> vector = embeddingClient.embed(content.toString());
                        log.info("Vector size: {}", vector.size());
                        String uuid = UUID.randomUUID().toString();

                        PointStruct point = PointStruct.newBuilder()
                                        .setId(PointId.newBuilder().setUuid(uuid).build())
                                        .setVectors(Vectors.newBuilder()
                                                        .setVector(Vector.newBuilder().addAllData(vector).build())
                                                        .build())
                                        .putPayload("dataSourceId",
                                                        Value.newBuilder().setIntegerValue(dataSourceId).build())
                                        .putPayload("tableName",
                                                        Value.newBuilder().setStringValue(table.getTableName()).build())
                                        .putPayload("content",
                                                        Value.newBuilder().setStringValue(content.toString()).build())
                                        .build();

                        pointsToUpsert.add(point);
                }

                try {
                        qdrantClient.upsertAsync(COLLECTION, pointsToUpsert).get();
                        log.info("Indexed {} tables with relationships for dataSourceId: {}",
                                        tables.size(), dataSourceId);

                } catch (InterruptedException | ExecutionException e) {
                        log.error("Failed to index tables with relations", e);
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Lỗi khi index vector", e);
                }
        }

        public void deleteByDataSourceId(Long dataSourceId) {
                try {
                        Filter filter = Filter.newBuilder()
                                        .addMust(Condition.newBuilder()
                                                        .setField(FieldCondition.newBuilder()
                                                                        .setKey("dataSourceId")
                                                                        .setMatch(Match.newBuilder()
                                                                                        .setInteger(dataSourceId)
                                                                                        .build())
                                                                        .build())
                                                        .build())
                                        .build();

                        qdrantClient.deleteAsync(COLLECTION, filter).get();
                        log.info("Deleted vectors for dataSourceId: {}", dataSourceId);

                } catch (InterruptedException | ExecutionException e) {
                        log.error("Failed to delete vectors", e);
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Lỗi khi xóa vector", e);
                }
        }
}