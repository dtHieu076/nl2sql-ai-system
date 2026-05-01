package com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.service;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.enumDataSourceStatus;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.repository.DataSourceRepository;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataSourceService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository dataSourceRepository;

    @Override
    public Long resolve(String role) {
        return dataSourceRepository.findByAssignedRoleAndStatus(role.toUpperCase(), enumDataSourceStatus.ACTIVE)
                .map(DataSource::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy DataSource phù hợp cho Role: " + role));
    }

    @Override
    public DataSource create(DataSource ds) {
        ds.setStatus(enumDataSourceStatus.ACTIVE);
        ds.setCreatedAt(LocalDateTime.now());
        return dataSourceRepository.save(ds);
    }

    @Override
    public List<DataSource> findAll() {
        return dataSourceRepository.findAll();
    }

    @Override
    public DataSource findById(Long id) {
        return dataSourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DataSource not found"));
    }

}