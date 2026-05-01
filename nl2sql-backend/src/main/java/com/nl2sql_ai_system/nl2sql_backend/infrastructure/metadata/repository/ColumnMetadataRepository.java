package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.repository;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.ColumnMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ColumnMetadataRepository extends JpaRepository<ColumnMetadata, Long> {
    List<ColumnMetadata> findByTableId(Long tableId);
}