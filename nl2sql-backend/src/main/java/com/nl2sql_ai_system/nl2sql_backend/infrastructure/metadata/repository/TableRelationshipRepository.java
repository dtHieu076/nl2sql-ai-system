package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.repository;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface TableRelationshipRepository extends JpaRepository<TableRelationship, Long> {

    // Lấy toàn bộ relationship theo datasource
    List<TableRelationship> findByDataSourceId(Long dataSourceId);

    // Xóa toàn bộ relationship theo datasource (cực kỳ quan trọng khi sync)
    @Transactional
    @Modifying
    void deleteByDataSourceId(Long dataSourceId);
}