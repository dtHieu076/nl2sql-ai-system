package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.service;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points.Condition;
import io.qdrant.client.grpc.Points.FieldCondition;
import io.qdrant.client.grpc.Points.Filter;
import io.qdrant.client.grpc.Points.Match;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaVectorService {

    private final VectorStore vectorStore;

    // Tiêm trực tiếp Client gốc của Qdrant để dùng các lệnh nâng cao
    private final QdrantClient qdrantClient;

    // Lấy tên collection từ file application.properties, mặc định là
    // schema_metadata
    @Value("${spring.ai.vectorstore.qdrant.collection-name:schema_metadata}")
    private String collectionName;

    /**
     * Ghi danh sách bảng vào Vector DB (Qdrant)
     */
    public void indexTables(List<TableMetadata> tables, Long dataSourceId) {
        List<Document> documents = tables.stream().map(table -> {
            String schemaContent = generateSchemaDescription(table);

            Map<String, Object> metadata = Map.of(
                    "dataSourceId", dataSourceId,
                    "tableName", table.getTableName());

            return new Document(schemaContent, metadata);
        }).collect(Collectors.toList());

        vectorStore.add(documents);
        log.info("Đã lưu {} bảng vào Qdrant cho DataSource ID: {}", documents.size(), dataSourceId);
    }

    /**
     * Tìm kiếm các bảng liên quan đến câu hỏi (intent) của user
     */
    public List<String> findRelevantSchemas(String intent, Long dataSourceId, int topK) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.query(intent)
                        .withFilterExpression("dataSourceId == " + dataSourceId)
                        .withTopK(topK));

        return results.stream()
                .map(Document::getContent)
                .collect(Collectors.toList());
    }

    /**
     * Xóa toàn bộ vector thuộc về một DataSource cụ thể
     */
    public void deleteByDataSourceId(Long dataSourceId) {
        try {
            // Tạo điều kiện lọc: metadata.dataSourceId == dataSourceId
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

            // Thực thi lệnh xóa bất đồng bộ trên Qdrant và chờ kết quả (.get())
            qdrantClient.deleteAsync(collectionName, filter).get();
            log.info("Đã xóa các vector cũ của DataSource ID: {} trong Qdrant", dataSourceId);

        } catch (Exception e) {
            log.error("Lỗi khi xóa vector cũ của DataSource ID: {}", dataSourceId, e);
            throw new RuntimeException("Không thể xóa vector cũ trong Qdrant", e);
        }
    }

    /**
     * Helper: Tạo chuỗi mô tả bảng.
     */
    private String generateSchemaDescription(TableMetadata table) {
        return String.format("Table %s", table.getTableName());
    }
}