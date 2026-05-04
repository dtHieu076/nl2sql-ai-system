package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.repository;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableMetadataRepository extends JpaRepository<TableMetadata, Long> {

    // basic
    List<TableMetadata> findByDataSourceId(Long dataSourceId);

    // 🔥 QUAN TRỌNG: fetch luôn columns để tránh N+1
    @Query("""
                SELECT DISTINCT t
                FROM TableMetadata t
                LEFT JOIN FETCH t.columns
                WHERE t.dataSource.id = :dataSourceId
            """)
    List<TableMetadata> findWithColumnsByDataSourceId(@Param("dataSourceId") Long dataSourceId);
}