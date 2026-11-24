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
import com.mockrunner.mock.jdbc.MockResultSet;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

public class TableManagerTest {

    @Test
    public void testCreateTable() throws Exception {
        //Given
        ConnectionProvider connectionProvider = Mockito.mock(ConnectionProvider.class);
        Connection connection = Mockito.mock(Connection.class);
        String tableName = "TestTable";

        List<FreeForm> data = new ArrayList<>();
        FreeForm row = new FreeForm();
        row.put("Test1", "Text", "row1");
        row.put("Test2", "Text", "row2");
        data.add(row);
        Mockito.when(connectionProvider.createConnection()).thenReturn(connection);
        PreparedStatement statement = Mockito.mock(PreparedStatement.class);
        Statement statement1 = Mockito.mock(Statement.class);
        Mockito.when(connection.prepareStatement(anyString())).thenReturn(statement);
        Mockito.when(connectionProvider.createConnection()).thenReturn(connection);
        Mockito.when(connection.createStatement()).thenReturn(statement1);
        TableManager tableManager = new TableManager(connectionProvider);

        //When
        tableManager.create(tableName, data);
    }

    @Test
    public void testCheckListOfTablesinDB() throws Exception {
        // Given
        ConnectionProvider connectionProvider = Mockito.mock(ConnectionProvider.class);
        Connection connection = Mockito.mock(Connection.class);
        String tableName = "TestTable";

        DatabaseMetaData databaseMetaData = Mockito.mock(DatabaseMetaData.class);
        MockResultSet tables = new MockResultSet("tables");
        tables.addColumn("TABLE_NAME", new String[]{tableName});
        Mockito.when(connection.getMetaData()).thenReturn(databaseMetaData);
        Mockito.when(databaseMetaData.getTables(null, null, "%",
                new String[]{"TABLE"})).thenReturn(tables);
        Mockito.when(connectionProvider.createConnection()).thenReturn(connection);
        Statement statement = Mockito.mock(Statement.class);
        Mockito.when(connection.createStatement()).thenReturn(statement);
        Mockito.when(connectionProvider.createConnection()).thenReturn(connection);
        TableManager tableManager = new TableManager(connectionProvider);

        // When
        List<String> result = tableManager.listTables();

        // Then
        Assert.assertNotNull(result);
        Assert.assertTrue(result.contains(tableName));
    }

    @Test
    public void testReadRecords() throws Exception {
        // Given
        ConnectionProvider connectionProvider = Mockito.mock(ConnectionProvider.class);
        Connection connection = Mockito.mock(Connection.class);
        String tableName = "TestTable";

        MockResultSet records = new MockResultSet("records");
        records.addColumn("TEST1", Arrays.asList("row1", "row2"));
        records.addColumn("TEST2", Arrays.asList("row1", "row2"));
        Mockito.when(connectionProvider.createConnection()).thenReturn(connection);
        Statement statement = Mockito.mock(Statement.class);
        Mockito.when(connection.createStatement()).thenReturn(statement);
        Mockito.when(statement.executeQuery(anyString())).thenReturn(records);
        Mockito.when(connectionProvider.createConnection()).thenReturn(connection);
        TableManager tableManager = new TableManager(connectionProvider);

        // When
        List<FreeForm> result = tableManager.read(tableName);

        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals("row1", result.get(0).get("TEST1"));
        Assert.assertEquals("row2", result.get(1).get("TEST2"));
    }

}
