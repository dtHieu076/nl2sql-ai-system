package com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.repository;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.enumDataSourceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {
    Optional<DataSource> findById(Long id);

    Optional<DataSource> findByAssignedRoleAndStatus(String assignedRole, enumDataSourceStatus status);
}