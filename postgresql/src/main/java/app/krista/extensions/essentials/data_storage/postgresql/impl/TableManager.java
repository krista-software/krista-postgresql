/*
 * Postgresql Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

package app.krista.extensions.essentials.data_storage.postgresql.impl;

import app.krista.model.base.FreeForm;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public final class TableManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableManager.class);

    private final ConnectionProvider connectionProvider;

    @Inject
    public TableManager(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void create(String tableName, List<FreeForm> data) throws ExecutionException {
        isNullOrEmptyInput(tableName);
        List<String> headerRow = createHeaderRow(data);
        String headerRowString = removeSquareBracketsFromQuery(headerRow.toString());
        String statement = String.format("CREATE TABLE  %s(%s);", tableName, headerRowString);
        try (Connection connection = connectionProvider.createConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            LOGGER.info("Creating PostgreSQL table '{}' with {} columns.", tableName, headerRow.size());
            preparedStatement.execute();
            insertRecords(tableName, data, headerRow);
            LOGGER.info("Successfully created table '{}' and inserted {} rows.", tableName, data.size());
        } catch (SQLException | RuntimeException cause) {
            LOGGER.error("Error while creating table '{}' or inserting data: {}", tableName, cause.getMessage(), cause);
            throw new ExecutionException(
                    String.format("Unable to create table '%s'. Please review your table name and data, then try again.", tableName),
                    cause);
        }
    }

    private void insertRow(Map<String, Object> rowData, String tableName, List<String> headerRow) throws ExecutionException {
        List<String> transformedRowData = new ArrayList<>();
        List<String> modifiedHeaderRow = new ArrayList<>();
        modifyHeaderAndRowData(rowData, headerRow, transformedRowData, modifiedHeaderRow);
        String query = String.format("INSERT INTO %s (%s) VALUES(%s) ;", tableName, modifiedHeaderRow, transformedRowData);
        query = removeSquareBracketsFromQuery(query);
        try (Connection connection = connectionProvider.createConnection();
             Statement preparedStatement = connection.createStatement()) {
            LOGGER.debug("Inserting row into table '{}' with columns {}.", tableName, modifiedHeaderRow);
            preparedStatement.executeUpdate(query);
        } catch (RuntimeException | SQLException cause) {
            LOGGER.error("Error while inserting row into table '{}': {}", tableName, cause.getMessage(), cause);
            throw new ExecutionException(
                    String.format("Unable to insert data into table '%s'. Please verify the column names and values.", tableName),
                    cause);
        }
    }

    private void modifyHeaderAndRowData(Map<String, Object> rowData, List<String> headerRow, List<String> transformedRowData, List<String> modifiedHeaderRow) {
        headerRow.forEach(columnName -> {
            transformedRowData.add("\'" + rowData.get(columnName.split(" ")[0]).toString() + "\'");
            modifiedHeaderRow.add(columnName.replace(" VARCHAR(255)", ""));
        });
    }

    public List<FreeForm> read(String tableName) {
        isNullOrEmptyInput(tableName);
        String query = "SELECT * FROM " + tableName + " ;";
        return search(query);
    }

    public List<FreeForm> search(String query) {
        isNullOrEmptyInput(query);
        if (!query.toUpperCase().startsWith("SELECT")) {
            throw new IllegalArgumentException("Only SELECT queries are allowed when searching data.");
        }
        List<FreeForm> tableData = new ArrayList<>();
        try (Connection connection = connectionProvider.createConnection();
             Statement preparedStatement = connection.createStatement()) {
            LOGGER.debug("Executing search query: {}", query);
            ResultSet result = preparedStatement.executeQuery(query);
            while (result.next()) {
                FreeForm rowData = new FreeForm();
                int columnCount = result.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(result.getMetaData().getColumnName(i), "Text", result.getString(i));
                }
                tableData.add(rowData);
            }
            LOGGER.info("Search query completed. Returned {} rows.", tableData.size());
            return tableData;
        } catch (Exception cause) {
            LOGGER.error("Error while executing search query '{}': {}", query, cause.getMessage(), cause);
            throw new IllegalStateException(
                    "Unable to search data in PostgreSQL. Please check your query and try again.",
                    cause);
        }

    }

    public List<String> listTables() {
        List<String> tableNames = new ArrayList<>();
        try (Connection connection = connectionProvider.createConnection()) {
            LOGGER.debug("Listing PostgreSQL tables.");
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet res = meta.getTables(null, null, "%",
                    new String[]{"TABLE"});
            while (res.next()) {
                tableNames.add(res.getString("TABLE_NAME"));
            }
            LOGGER.info("Retrieved {} tables from PostgreSQL.", tableNames.size());
            return tableNames;
        } catch (SQLException | RuntimeException cause) {
            LOGGER.error("Error while listing PostgreSQL tables: {}", cause.getMessage(), cause);
            throw new IllegalStateException("Unable to list tables from the database. Please try again later.", cause);
        }

    }

    private void insertRecords(String tableName, List<FreeForm> data, List<String> headerRow) {
        data.forEach(singleRecord -> {
            try {
                insertRow(singleRecord, tableName, headerRow);
            } catch (ExecutionException cause) {
                LOGGER.error("Error while inserting records into table '{}': {}", tableName, cause.getMessage(), cause);
                throw new IllegalStateException(
                        String.format("Unable to insert one or more rows into table '%s'. Please review your data and try again.", tableName),
                        cause);
            }
        });

    }

    private void isNullOrEmptyInput(String inputParam) {
        if (inputParam == null || inputParam.isBlank()) {
            throw new IllegalArgumentException("Input value cannot be empty. Please provide a valid value.");
        }
    }

    private String removeSquareBracketsFromQuery(String statement) {
        statement = statement.replace("[", "");
        statement = statement.replace("]", "");
        return statement;

    }

    private List<String> createHeaderRow(List<FreeForm> data) {
        List<String> headerRow = new ArrayList<>();
        Set<String> keys = data.get(0).keySet();
        for (String key : keys) {
            headerRow.add(key + " VARCHAR(255)");
        }
        return headerRow;
    }

    public String deleteTable(String tableName) {
        String query = String.format("DROP TABLE %s;", tableName);
        try (Connection connection = connectionProvider.createConnection();
             Statement preparedStatement = connection.createStatement()) {
            LOGGER.info("Deleting PostgreSQL table '{}'.", tableName);
            preparedStatement.executeUpdate(query);
            LOGGER.info("Successfully deleted PostgreSQL table '{}'.", tableName);
            return String.format("Table '%s' was deleted successfully.", tableName);
        } catch (SQLException | RuntimeException cause) {
            LOGGER.error("Error while deleting PostgreSQL table '{}': {}", tableName, cause.getMessage(), cause);
            throw new IllegalStateException(
                    String.format("Unable to delete table '%s'. Please verify that the table exists and try again.", tableName),
                    cause);
        }
    }
}
