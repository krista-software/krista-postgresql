# Get Data

## Overview

Retrieves all rows from a specified table in the PostgreSQL database. This operation returns the complete dataset from the table, including all columns and records.

## Request Details

- **Area**: Relational DBs
- **Type**: QUERY_SYSTEM
- **ID**: `localDomainRequest_2f627b1f-f8ee-4d93-bb8b-2903bd463d3b`

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Table Name | Text | Yes | Name of the table to retrieve data from | `employees` |

### Parameter Details

#### Table Name
The name of the existing table from which to retrieve all records.

- **Format**: Valid PostgreSQL table name
- **Case Sensitivity**: PostgreSQL table names are case-sensitive when quoted
- **Examples**: `employees`, `customer_orders`, `product_inventory`

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Data | [ Composite ] | List of all records from the table, where each record contains all column values |

### Output Details

#### Data
A list of composite objects where each object represents a row from the table. Each object contains key-value pairs corresponding to column names and their values.

- **Format**: List of records with column-value pairs
- **Empty Result**: Returns empty list `[]` if table has no rows
- **Column Order**: Columns are returned in the order they were defined in the table

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Table Name is empty | "Table Name is required" | Provide a valid table name |
| Table does not exist | "Table 'tablename' does not exist" | Verify table name or create the table first |
| Insufficient permissions | "Permission denied" | Ensure database user has SELECT permission |

## Usage Examples

### Example 1: Retrieve Employee Data

**Input**:
```json
{
  "Table Name": "employees"
}
```

**Output**:
```json
{
  "Data": [
    {
      "employee_id": 1,
      "first_name": "John",
      "last_name": "Doe",
      "email": "john.doe@example.com",
      "hire_date": "2024-01-15",
      "salary": 75000
    },
    {
      "employee_id": 2,
      "first_name": "Jane",
      "last_name": "Smith",
      "email": "jane.smith@example.com",
      "hire_date": "2024-02-01",
      "salary": 82000
    }
  ]
}
```

### Example 2: Retrieve Product Inventory

**Input**:
```json
{
  "Table Name": "products"
}
```

**Output**:
```json
{
  "Data": [
    {
      "product_id": "P001",
      "product_name": "Laptop",
      "category": "Electronics",
      "quantity": 50,
      "price": 999.99,
      "in_stock": true
    },
    {
      "product_id": "P002",
      "product_name": "Mouse",
      "category": "Accessories",
      "quantity": 200,
      "price": 29.99,
      "in_stock": true
    }
  ]
}
```

### Example 3: Empty Table

**Input**:
```json
{
  "Table Name": "new_table"
}
```

**Output**:
```json
{
  "Data": []
}
```

**Result**: Empty list returned for table with no rows

## Business Rules

### 1. Complete Data Retrieval
- Returns ALL rows from the table without filtering
- No pagination or row limits
- Includes all columns from the table

### 2. Data Types
- Column values are returned in their native data types
- Numeric values: returned as numbers
- Text values: returned as strings
- Boolean values: returned as true/false
- Date/Time values: returned as formatted strings
- NULL values: returned as null

### 3. Performance Considerations
- Large tables may take longer to retrieve
- All data is loaded into memory
- Consider using [Search Data](pages/SearchData.md) for filtered queries on large tables

## Common Use Cases

### 1. Data Export
```
Scenario: Export all employee records for reporting
Action: Get Data from employees table
Result: Complete employee dataset retrieved
```

### 2. Data Verification
```
Scenario: Verify data was inserted correctly
Action: Get Data after Create Data operation
Result: Confirm all records are present
```

### 3. Data Analysis
```
Scenario: Analyze product inventory levels
Action: Get Data from products table
Result: Full inventory dataset for analysis
```

### 4. Data Migration
```
Scenario: Extract data for migration to another system
Action: Get Data from source table
Result: Complete dataset ready for migration
```

## Error Handling

### Table Does Not Exist
**Error**: "Table 'employees' does not exist"

**Cause**: The specified table name doesn't exist in the database

**Resolution**:
1. Verify the table name is spelled correctly
2. Check if table exists using [Get All Tables](pages/GetAllTables.md)
3. Create the table using [Create Data](pages/CreateData.md) if needed

### Permission Denied
**Error**: "Permission denied for table employees"

**Cause**: Database user doesn't have SELECT permission on the table

**Resolution**:
1. Grant SELECT permission to the database user:
   ```sql
   GRANT SELECT ON TABLE employees TO krista_user;
   ```
2. Contact database administrator
3. Use a user account with appropriate permissions

### Connection Error
**Error**: "Could not connect to database"

**Cause**: Database connection failed

**Resolution**:
1. Verify database connection parameters in [Extension Configuration](pages/ExtensionConfiguration.md)
2. Ensure PostgreSQL server is running
3. Check network connectivity

## Performance Considerations

### Large Tables
For tables with many rows (>10,000):
- **Consider**: Using [Search Data](pages/SearchData.md) with LIMIT clause
- **Example**: `SELECT * FROM employees LIMIT 1000`
- **Benefit**: Faster response time and reduced memory usage

### Wide Tables
For tables with many columns (>50):
- **Consider**: Using [Search Data](pages/SearchData.md) to select specific columns
- **Example**: `SELECT employee_id, first_name, last_name FROM employees`
- **Benefit**: Reduced data transfer and faster processing

### Memory Usage
- All retrieved data is loaded into memory
- Monitor memory usage for very large datasets
- Consider pagination or filtering for production use

## Best Practices

### 1. Verify Table Exists
Before retrieving data, confirm the table exists:
1. Use [Get All Tables](pages/GetAllTables.md) to list available tables
2. Verify table name spelling and case

### 2. Handle Empty Results
Always check if the returned Data list is empty:
```javascript
if (data.length === 0) {
  // Handle empty table case
}
```

### 3. Use for Small to Medium Tables
Get Data is ideal for:
- Tables with < 10,000 rows
- Complete data exports
- Data verification after inserts

### 4. Consider Alternatives for Large Tables
For large tables, use [Search Data](pages/SearchData.md) with:
- WHERE clauses for filtering
- LIMIT clauses for pagination
- Specific column selection

### 5. Monitor Performance
- Track query execution time
- Monitor memory usage
- Optimize for production workloads

## Related Catalog Requests

- [Create Data](pages/CreateData.md) - Create table and insert records
- [Search Data](pages/SearchData.md) - Query data with custom SQL filters
- [Delete Table](pages/DeleteTable.md) - Remove a table from the database
- [Get All Tables](pages/GetAllTables.md) - List all tables in the database

## Technical Implementation

### Helper Class
- **Class**: TableManager
- **Package**: app.krista.extensions.essentials.data_storage.postgresql.impl
- **Method**: `read(String tableName)`

### Database Operations
1. Validate table name
2. Establish database connection
3. Execute SQL: `SELECT * FROM <tableName>`
4. Fetch all result rows
5. Convert rows to FreeForm objects
6. Return list of records
7. Close connection

### SQL Generated
```sql
SELECT * FROM <table_name>;
```

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure database connection
- [Authentication](pages/Authentication.md) - Database permissions and security
- [Search Data](pages/SearchData.md) - Advanced querying with filters

