package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.introspection;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableRelationship;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SchemaSnapshot {
    private List<TableMetadata> tables;
    private List<TableRelationship> relationships;
}