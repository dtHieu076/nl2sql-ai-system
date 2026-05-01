package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "columns_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private TableMetadata table;

    private String columnName;
    private String dataType;
    private String columnDescription;
    private Boolean isNullable;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;
    private String synonyms;
    private String sampleValues;
}