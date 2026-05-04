package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.introspection;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableRelationship;

import java.util.List;

public interface SchemaIntrospectionService {

    SchemaSnapshot extractFull(String jdbcUrl, String username, String password);
}