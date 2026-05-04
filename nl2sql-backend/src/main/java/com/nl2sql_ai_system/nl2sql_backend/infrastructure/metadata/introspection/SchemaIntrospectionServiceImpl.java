package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.introspection;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.ColumnMetadata;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableMetadata;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.entity.TableRelationship;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaIntrospectionServiceImpl implements SchemaIntrospectionService {

    @Override
    public SchemaSnapshot extractFull(String jdbcUrl, String username, String password) {

        JdbcTemplate jdbc = new JdbcTemplate(createDataSource(jdbcUrl, username, password));

        // 1. TABLES
        List<TableMetadata> tables = jdbc.query("""
                    SELECT table_schema, table_name
                    FROM information_schema.tables
                    WHERE table_type = 'BASE TABLE'
                      AND table_schema NOT IN ('pg_catalog', 'information_schema')
                """, (rs, rowNum) -> {
            TableMetadata t = new TableMetadata();
            t.setSchemaName(rs.getString("table_schema"));
            t.setTableName(rs.getString("table_name"));
            return t;
        });

        Map<String, TableMetadata> tableMap = new HashMap<>();

        for (TableMetadata t : tables) {
            tableMap.put(t.getTableName(), t);
        }

        // 2. COLUMNS
        List<Map<String, Object>> columnRows = jdbc.queryForList("""
                    SELECT table_name, column_name, data_type, is_nullable
                    FROM information_schema.columns
                """);

        for (Map<String, Object> row : columnRows) {
            String tableName = (String) row.get("table_name");

            TableMetadata table = tableMap.get(tableName);
            if (table == null)
                continue;

            ColumnMetadata col = new ColumnMetadata();
            col.setColumnName((String) row.get("column_name"));
            col.setDataType((String) row.get("data_type"));
            col.setIsNullable("YES".equals(row.get("is_nullable")));

            // attach vào table (bạn đang không có @OneToMany nên phải init thủ công)
            if (table.getColumns() == null) {
                table.setColumns(new ArrayList<>());
            }
            table.getColumns().add(col);
        }

        // 3. PRIMARY KEY
        List<Map<String, Object>> pkRows = jdbc.queryForList("""
                    SELECT kcu.table_name, kcu.column_name
                    FROM information_schema.table_constraints tc
                    JOIN information_schema.key_column_usage kcu
                      ON tc.constraint_name = kcu.constraint_name
                    WHERE tc.constraint_type = 'PRIMARY KEY'
                """);

        for (Map<String, Object> row : pkRows) {
            String table = (String) row.get("table_name");
            String col = (String) row.get("column_name");

            TableMetadata t = tableMap.get(table);
            if (t != null && t.getColumns() != null) {
                t.getColumns().stream()
                        .filter(c -> c.getColumnName().equals(col))
                        .forEach(c -> c.setIsPrimaryKey(true));
            }
        }

        // 4. FOREIGN KEY + RELATIONSHIP
        List<TableRelationship> relationships = new ArrayList<>();

        List<Map<String, Object>> fkRows = jdbc.queryForList("""
                    SELECT
                        tc.table_name AS source_table,
                        kcu.column_name AS source_column,
                        ccu.table_name AS target_table,
                        ccu.column_name AS target_column
                    FROM information_schema.table_constraints tc
                    JOIN information_schema.key_column_usage kcu
                      ON tc.constraint_name = kcu.constraint_name
                    JOIN information_schema.constraint_column_usage ccu
                      ON ccu.constraint_name = tc.constraint_name
                    WHERE tc.constraint_type = 'FOREIGN KEY'
                """);

        for (Map<String, Object> row : fkRows) {
            String sourceTable = (String) row.get("source_table");
            String sourceColumn = (String) row.get("source_column");
            String targetTable = (String) row.get("target_table");
            String targetColumn = (String) row.get("target_column");

            TableMetadata srcTable = tableMap.get(sourceTable);
            TableMetadata tgtTable = tableMap.get(targetTable);

            if (srcTable == null || tgtTable == null)
                continue;

            ColumnMetadata srcCol = findColumn(srcTable, sourceColumn);
            ColumnMetadata tgtCol = findColumn(tgtTable, targetColumn);

            if (srcCol != null)
                srcCol.setIsForeignKey(true);

            TableRelationship rel = new TableRelationship();
            rel.setSourceTable(srcTable);
            rel.setTargetTable(tgtTable);
            rel.setSourceColumn(srcCol);
            rel.setTargetColumn(tgtCol);
            rel.setRelationshipType("FOREIGN_KEY");

            relationships.add(rel);
        }

        return new SchemaSnapshot(tables, relationships);
    }

    private DataSource createDataSource(String url, String user, String pass) {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        return ds;
    }

    private ColumnMetadata findColumn(TableMetadata table, String columnName) {
        if (table.getColumns() == null)
            return null;

        return table.getColumns().stream()
                .filter(c -> c.getColumnName().equals(columnName))
                .findFirst()
                .orElse(null);
    }
}
