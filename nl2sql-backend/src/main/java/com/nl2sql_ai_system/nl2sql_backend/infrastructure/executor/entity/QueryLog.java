package com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.entity;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "query_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id")
    private DataSource dataSource;

    @Column(columnDefinition = "TEXT")
    private String userQuery;

    @Column(columnDefinition = "TEXT")
    private String generatedSql;

    private String status;
    private String errorMessage;
    private Integer executionTimeMs;
    private Integer promptTokens;
    private Integer completionTokens;
    private LocalDateTime createdAt = LocalDateTime.now();
}