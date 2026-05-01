package com.nl2sql_ai_system.nl2sql_backend.chat.controller;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataSourceService;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.MetadataService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/datasource")
@RequiredArgsConstructor
public class DataSourceManagementController {

    private final DataSourceService dataSourceService;
    private final MetadataService metadataService;

    // 1. Add new datasource + auto sync schema
    @PostMapping
    public DataSource create(@RequestBody DataSource request) {

        DataSource saved = dataSourceService.create(request);

        // auto sync schema ngay sau khi add
        metadataService.sync(saved.getId());

        return saved;
    }

    // 2. Sync 1 datasource
    @PostMapping("/sync/{dataSourceId}")
    public String sync(@PathVariable Long dataSourceId) {
        metadataService.sync(dataSourceId);
        return "Sync datasource " + dataSourceId + " successfully";
    }

    // 3. Sync all datasources
    @PostMapping("/sync/all")
    public String syncAll() {
        metadataService.syncAll();
        return "Sync all datasources successfully";
    }
}