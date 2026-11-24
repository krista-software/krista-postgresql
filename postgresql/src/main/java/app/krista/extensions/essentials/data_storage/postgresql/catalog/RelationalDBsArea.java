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

package app.krista.extensions.essentials.data_storage.postgresql.catalog;

import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.data_storage.postgresql.impl.TableManager;
import app.krista.model.base.FreeForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Domain(id = "catEntryDomain_a2bb0076-6ba7-411c-b3a8-c6a7f9974627",
        name = "Data Storage",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "0da7df0f-222e-4c10-9507-c5d9f3e1e262")
public class RelationalDBsArea {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationalDBsArea.class);

    private final TableManager tableManager;

    @Inject
    public RelationalDBsArea(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    @CatalogRequest(
            id = "localDomainRequest_59e1795e-eebb-4f1f-997d-d46836d5c2a7",
            name = "Search Data",
            description = "Returns data matching the query provided in the input",
            area = "Relational DBs",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Desc(name = "Data", type = "[ Composite ]", required = false)
    public List<FreeForm> searchData(
            @Field(name = "Query", type = "Text", required = true) String query) {
        LOGGER.info("Received request to search data in PostgreSQL using a custom query.");
        return tableManager.search(query);
    }

    @CatalogRequest(
            id = "localDomainRequest_6529f402-07a2-4132-ac1e-fae0d9620302",
            name = "Get All Tables",
            description = "Returns all the table names that exist in the database",
            area = "Relational DBs",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Desc(name = "Tables", type = "[ Text ]", required = false)
    public List<String> getAllTables() {
        LOGGER.info("Received request to list all PostgreSQL tables.");
        return tableManager.listTables();
    }

    @CatalogRequest(
            id = "localDomainRequest_f061e1e9-8112-4ec8-bc40-338c31e0051f",
            name = "Create Data",
            description = "Creates a table in the database with provided table name and data in it",
            area = "Relational DBs",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    public void createData(
            @Field(name = "Table Name", type = "Text", required = true) String tableName,
            @Field.Desc(name = "Data", type = "[ Composite ]", required = true) List<FreeForm> data) throws ExecutionException {
        LOGGER.info("Received request to create PostgreSQL table '{}' with {} rows of data.", tableName, data.size());
        tableManager.create(tableName, data);
    }

    @CatalogRequest(
            id = "localDomainRequest_8bb13f19-fa3a-481f-a265-685f65ca6dc7",
            name = "Delete Table",
            description = "Deletes the table from the database that matches provided table name",
            area = "Relational DBs",
            type = CatalogRequest.Type.CHANGE_SYSTEM)
    @Field(name = "Message", type = "Text", required = false)
    public String deleteTable(
            @Field(name = "Table Name", type = "Text", required = true) String tableName) {
        LOGGER.info("Received request to delete PostgreSQL table '{}'.", tableName);
        return tableManager.deleteTable(tableName);
    }

    @CatalogRequest(
            id = "localDomainRequest_2f627b1f-f8ee-4d93-bb8b-2903bd463d3b",
            name = "Get Data",
            description = "Returns all rows of the table that matches the provided table name from the database",
            area = "Relational DBs",
            type = CatalogRequest.Type.QUERY_SYSTEM)
    @Field.Desc(name = "Data", type = "[ Composite ]", required = false)
    public List<FreeForm> getData(
            @Field(name = "Table Name", type = "Text", required = true) String tableName) {
        LOGGER.info("Received request to get all rows from PostgreSQL table '{}'.", tableName);
        return tableManager.read(tableName);
    }

}