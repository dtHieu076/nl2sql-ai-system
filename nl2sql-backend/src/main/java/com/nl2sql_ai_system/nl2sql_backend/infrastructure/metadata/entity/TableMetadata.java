package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tables_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id")
    private DataSource dataSource;

    private String schemaName;
    private String tableName;
    private String tableDescription;
    private LocalDateTime createdAt = LocalDateTime.now();
}