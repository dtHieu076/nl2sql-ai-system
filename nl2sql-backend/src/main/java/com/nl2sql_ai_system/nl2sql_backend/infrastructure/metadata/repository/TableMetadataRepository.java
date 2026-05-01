package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.repository;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TableMetadataRepository extends JpaRepository<TableMetadata, Long> {
    List<TableMetadata> findByDataSourceId(Long dataSourceId);
}