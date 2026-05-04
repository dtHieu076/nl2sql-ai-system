#!/bin/bash
set -e

# 1. Tạo các Database: nl2sql_admin_db và hr_db
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE nl2sql_admin_db;
    CREATE DATABASE hr_db;
EOSQL

# 2. Tạo các bảng quản lý hệ thống bên trong nl2sql_admin_db
echo "Initializing tables for nl2sql_admin_db..."
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "nl2sql_admin_db" <<-EOSQL
    -- Module: Data Source Registry
    CREATE TABLE data_sources (
       id BIGSERIAL PRIMARY KEY,
       name VARCHAR(100),
       db_type VARCHAR(50),
       jdbc_url TEXT,
       driver_class_name VARCHAR(100),
       username VARCHAR(100),
       password VARCHAR(255),
       status VARCHAR(20),
       assigned_role VARCHAR(20),
       last_sync_at TIMESTAMP,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    -- Module: Metadata
    CREATE TABLE tables_metadata (
       id BIGSERIAL PRIMARY KEY,
       data_source_id BIGINT REFERENCES data_sources(id),
       schema_name VARCHAR(100) DEFAULT 'public',
       table_name VARCHAR(255),
       table_description TEXT,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE columns_metadata (
       id BIGSERIAL PRIMARY KEY,
       table_id BIGINT REFERENCES tables_metadata(id),
       column_name VARCHAR(255),
       data_type VARCHAR(50),
       column_description TEXT,
       is_nullable BOOLEAN,
       is_primary_key BOOLEAN DEFAULT FALSE,
       is_foreign_key BOOLEAN DEFAULT FALSE,
       synonyms TEXT,
       sample_values TEXT
    );

    CREATE TABLE table_relationships (
       id BIGSERIAL PRIMARY KEY,
       data_source_id BIGINT REFERENCES data_sources(id),
       source_table_id BIGINT REFERENCES tables_metadata(id),
       source_column_id BIGINT REFERENCES columns_metadata(id),
       target_table_id BIGINT REFERENCES tables_metadata(id),
       target_column_id BIGINT REFERENCES columns_metadata(id),
       relationship_type VARCHAR(50),
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    -- Module: Query Executor & Logging
    CREATE TABLE query_logs (
       id BIGSERIAL PRIMARY KEY,
       data_source_id BIGINT REFERENCES data_sources(id),
       user_query TEXT,
       generated_sql TEXT,
       status VARCHAR(20),
       error_message TEXT,
       execution_time_ms INT,
       prompt_tokens INT,
       completion_tokens INT,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    -- Module: AI & Prompting
    CREATE TABLE prompt_templates (
       id BIGSERIAL PRIMARY KEY,
       name VARCHAR(100),
       template TEXT,
       is_active BOOLEAN DEFAULT TRUE,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE business_rules (
       id BIGSERIAL PRIMARY KEY,
       data_source_id BIGINT REFERENCES data_sources(id),
       rule_name VARCHAR(255),
       rule_definition TEXT,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
EOSQL

echo "Importing HR backup into hr_db..."
# Bỏ ON_ERROR_STOP=1 để nó không bị sập bởi lỗi transaction_timeout của Neon
psql --username "$POSTGRES_USER" --dbname "hr_db" -f /db-scripts/hr_backup.sql

echo "Importing Seed Data into nl2sql_admin_db..."
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "nl2sql_admin_db" -f /db-scripts/seed-data.sql