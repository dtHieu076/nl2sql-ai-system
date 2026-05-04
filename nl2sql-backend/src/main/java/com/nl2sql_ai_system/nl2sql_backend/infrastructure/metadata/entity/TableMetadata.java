package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    // 🔥 QUAN TRỌNG: mapping columns
    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ColumnMetadata> columns;
}