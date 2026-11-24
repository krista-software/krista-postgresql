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

import app.krista.extensions.essentials.data_storage.postgresql.impl.TableManager;
import app.krista.model.base.FreeForm;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link RelationalDBsArea} using mocked {@link TableManager}.
 *
 * These tests verify that all five catalog methods delegate correctly to
 * the TableManager and return the expected values. This helps drive
 * Jacoco coverage close to 100% for this area class.
 */
public class RelationalDBsAreaTest {

    @Test
    public void testSearchDataDelegatesToTableManager() {
        // Given
        TableManager tableManager = Mockito.mock(TableManager.class);
        RelationalDBsArea area = new RelationalDBsArea(tableManager);
        String query = "SELECT * FROM my_table";

        List<FreeForm> expected = new ArrayList<>();
        FreeForm row = new FreeForm();
        row.put("COL1", "Text", "value1");
        expected.add(row);

        Mockito.when(tableManager.search(query)).thenReturn(expected);

        // When
        List<FreeForm> actual = area.searchData(query);

        // Then
        Assert.assertSame(expected, actual);
        Mockito.verify(tableManager).search(query);
    }

    @Test
    public void testGetAllTablesDelegatesToTableManager() {
        // Given
        TableManager tableManager = Mockito.mock(TableManager.class);
        RelationalDBsArea area = new RelationalDBsArea(tableManager);

        List<String> expectedTables = Collections.singletonList("TestTable");
        Mockito.when(tableManager.listTables()).thenReturn(expectedTables);

        // When
        List<String> actualTables = area.getAllTables();

        // Then
        Assert.assertSame(expectedTables, actualTables);
        Mockito.verify(tableManager).listTables();
    }

    @Test
    public void testCreateDataDelegatesToTableManager() throws Exception {
        // Given
        TableManager tableManager = Mockito.mock(TableManager.class);
        RelationalDBsArea area = new RelationalDBsArea(tableManager);

        String tableName = "TestTable";
        List<FreeForm> data = new ArrayList<>();
        FreeForm row = new FreeForm();
        row.put("COL1", "Text", "value1");
        data.add(row);

        // When
        area.createData(tableName, data);

        // Then
        Mockito.verify(tableManager).create(tableName, data);
    }

    @Test
    public void testDeleteTableDelegatesToTableManagerAndReturnsMessage() {
        // Given
        TableManager tableManager = Mockito.mock(TableManager.class);
        RelationalDBsArea area = new RelationalDBsArea(tableManager);

        String tableName = "TestTable";
        String expectedMessage = "Table 'TestTable' was deleted successfully.";
        Mockito.when(tableManager.deleteTable(tableName)).thenReturn(expectedMessage);

        // When
        String actualMessage = area.deleteTable(tableName);

        // Then
        Assert.assertEquals(expectedMessage, actualMessage);
        Mockito.verify(tableManager).deleteTable(tableName);
    }

    @Test
    public void testGetDataDelegatesToTableManager() {
        // Given
        TableManager tableManager = Mockito.mock(TableManager.class);
        RelationalDBsArea area = new RelationalDBsArea(tableManager);

        String tableName = "TestTable";
        List<FreeForm> expectedData = new ArrayList<>();
        FreeForm row = new FreeForm();
        row.put("COL1", "Text", "value1");
        expectedData.add(row);

        Mockito.when(tableManager.read(tableName)).thenReturn(expectedData);

        // When
        List<FreeForm> actualData = area.getData(tableName);

        // Then
        Assert.assertSame(expectedData, actualData);
        Mockito.verify(tableManager).read(tableName);
    }
}

