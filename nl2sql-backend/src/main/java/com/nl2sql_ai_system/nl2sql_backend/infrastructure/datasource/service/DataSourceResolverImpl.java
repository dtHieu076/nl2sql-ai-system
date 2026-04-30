package com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.service;

import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataSourceResolver;
import org.springframework.stereotype.Component;

@Component
public class DataSourceResolverImpl implements DataSourceResolver {

    @Override
    public Long resolve(String role) {
        return switch (role.toUpperCase()) {
            case "HR" -> 1L;
            case "SALES" -> 2L;
            case "ACCOUNTANT" -> 3L;
            default -> 99L;
        };
    }
}