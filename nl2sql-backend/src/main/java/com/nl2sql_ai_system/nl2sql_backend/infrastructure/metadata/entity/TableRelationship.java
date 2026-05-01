package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "table_relationships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id")
    private DataSource dataSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_table_id")
    private TableMetadata sourceTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_column_id")
    private ColumnMetadata sourceColumn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_table_id")
    private TableMetadata targetTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_column_id")
    private ColumnMetadata targetColumn;

    private String relationshipType;
    private LocalDateTime createdAt = LocalDateTime.now();
}