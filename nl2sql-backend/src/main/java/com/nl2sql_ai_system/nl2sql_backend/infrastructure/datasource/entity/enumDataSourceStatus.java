package com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity;

import lombok.Getter;

@Getter
public enum enumDataSourceStatus {
    ACTIVE("Hoạt động"),
    INACTIVE("Ngừng hoạt động"),
    MAINTENANCE("Bảo trì");

    private final String description;

    enumDataSourceStatus(String description) {
        this.description = description;
    }
}