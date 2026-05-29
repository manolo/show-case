package com.example.application.views.aidata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.ai.provider.DatabaseProvider;

/**
 * Reads the SamplePerson H2 schema and runs read-only queries on behalf of the
 * AI orchestrator. The schema description is generated from
 * {@link java.sql.DatabaseMetaData} so the LLM always sees the real column
 * names and types.
 */
@Component
public class SamplePersonDatabaseProvider implements DatabaseProvider {

    private static final String TABLE = "SAMPLE_PERSON";

    private final DataSource dataSource;

    public SamplePersonDatabaseProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String getSchema() {
        StringBuilder schema = new StringBuilder("Dialect: H2.\nTables:\n");
        try (var connection = dataSource.getConnection()) {
            var meta = connection.getMetaData();
            try (var columns = meta.getColumns(null, null, TABLE, "%")) {
                schema.append(TABLE).append("(");
                boolean first = true;
                while (columns.next()) {
                    if (!first) schema.append(", ");
                    schema.append(columns.getString("COLUMN_NAME"))
                            .append(' ')
                            .append(columns.getString("TYPE_NAME"));
                    first = false;
                }
                schema.append(")\n");
            }
            schema.append("Notes: dateOfBirth is a DATE column; ")
                    .append("role values are 'admin'/'user'; ")
                    .append("important is a boolean flag.");
        } catch (SQLException e) {
            throw new RuntimeException("Could not read schema", e);
        }
        return schema.toString();
    }

    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        if (sql == null || !sql.trim().toUpperCase().startsWith("SELECT")) {
            throw new IllegalArgumentException("Only SELECT statements are allowed");
        }
        try (var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()) {
            ResultSetMetaData meta = resultSet.getMetaData();
            int columnCount = meta.getColumnCount();
            List<Map<String, Object>> rows = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnLabel(i), resultSet.getObject(i));
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Query failed: " + e.getMessage(), e);
        }
    }
}
