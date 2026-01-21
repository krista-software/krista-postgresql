# Create Data

## Overview

Creates a new table in the PostgreSQL database with the provided table name and inserts the specified data rows. This operation combines table creation and data insertion in a single request.

## Request Details

- **Area**: Relational DBs
- **Type**: CHANGE_SYSTEM
- **ID**: `localDomainRequest_f061e1e9-8112-4ec8-bc40-338c31e0051f`

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Table Name | Text | Yes | Name of the table to create | `employees` |
| Data | [ Composite ] | Yes | List of records to insert into the table | See examples below |

### Parameter Details

#### Table Name
The name of the table to create in the PostgreSQL database.

- **Format**: Valid PostgreSQL table name
- **Naming Rules**:
  - Must start with a letter or underscore
  - Can contain letters, numbers, and underscores
  - Case-sensitive (PostgreSQL converts unquoted names to lowercase)
  - Maximum length: 63 characters
- **Examples**: `employees`, `customer_orders`, `product_inventory`

#### Data
A list of composite records where each record represents a row to insert. Each record contains field names and values that will become columns in the table.

- **Format**: List of key-value pairs
- **Column Types**: Automatically inferred from data values
  - Text values → VARCHAR
  - Numeric values → NUMERIC or INTEGER
  - Boolean values → BOOLEAN
  - Date values → DATE or TIMESTAMP
- **First Record**: Defines the table schema (column names and types)
- **Subsequent Records**: Must have matching column structure

## Output Parameters

This operation does not return output parameters. Success is indicated by the absence of errors.

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Table Name is empty | "Table Name is required" | Provide a valid table name |
| Data list is empty | "Data is required" | Provide at least one record |
| Table already exists | "Table already exists" | Use a different table name or delete the existing table first |
| Invalid table name | "Invalid table name" | Follow PostgreSQL naming conventions |
| Inconsistent column structure | "Column mismatch" | Ensure all records have the same columns |

## Usage Examples

### Example 1: Create Employee Table

**Input**:
```json
{
  "Table Name": "employees",
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

**Result**: Table `employees` created with 6 columns and 2 rows inserted

### Example 2: Create Product Inventory Table

**Input**:
```json
{
  "Table Name": "products",
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

**Result**: Table `products` created with 6 columns and 2 rows inserted

### Example 3: Create Single Record Table

**Input**:
```json
{
  "Table Name": "configuration",
  "Data": [
    {
      "config_key": "app_version",
      "config_value": "1.0.0",
      "last_updated": "2024-11-24"
    }
  ]
}
```

**Result**: Table `configuration` created with 3 columns and 1 row inserted

## Business Rules

### 1. Table Creation
- If the table doesn't exist, it will be created automatically
- Table schema is derived from the first record in the Data list
- Column names are taken from the keys in the first record
- Column data types are inferred from the values

### 2. Data Insertion
- All records in the Data list are inserted into the newly created table
- Records must have consistent column structure
- NULL values are supported for optional fields

### 3. Transaction Handling
- Table creation and data insertion are performed as a single transaction
- If any error occurs, the entire operation is rolled back
- The table will not be created if data insertion fails

### 4. Column Type Inference
The extension automatically determines column types based on data values:
- **Text**: VARCHAR(255) or TEXT
- **Integer**: INTEGER
- **Decimal**: NUMERIC or DECIMAL
- **Boolean**: BOOLEAN
- **Date/Time**: TIMESTAMP or DATE

## Common Use Cases

### 1. Import Data from External Source
```
Scenario: Import employee data from CSV file
Action: Create Data with table name and parsed CSV records
Result: New table created with all employee records
```

### 2. Initialize Application Data
```
Scenario: Set up initial configuration or reference data
Action: Create Data with configuration table and default values
Result: Configuration table ready for application use
```

### 3. Create Temporary Working Table
```
Scenario: Create temporary table for data processing
Action: Create Data with temp table name and intermediate results
Result: Temporary table available for further queries
```

### 4. Migrate Data Between Systems
```
Scenario: Transfer data from one system to PostgreSQL
Action: Create Data with extracted records from source system
Result: Data successfully migrated to PostgreSQL
```

## Error Handling

### Table Already Exists
**Error**: "Table 'employees' already exists"

**Cause**: A table with the specified name already exists in the database

**Resolution**:
1. Use a different table name
2. Delete the existing table using [Delete Table](pages/DeleteTable.md)
3. Append data to existing table using a different method

### Invalid Table Name
**Error**: "Invalid table name"

**Cause**: Table name doesn't follow PostgreSQL naming conventions

**Resolution**:
1. Ensure name starts with letter or underscore
2. Use only letters, numbers, and underscores
3. Keep name under 63 characters

### Column Mismatch
**Error**: "Column structure mismatch in data records"

**Cause**: Records in the Data list have different columns

**Resolution**:
1. Ensure all records have the same column names
2. Use NULL for missing values instead of omitting columns
3. Verify data structure before submission

### Insufficient Permissions
**Error**: "Permission denied to create table"

**Cause**: Database user doesn't have CREATE permission

**Resolution**:
1. Grant CREATE permission to the database user
2. Contact database administrator
3. Use a user account with appropriate permissions

## Best Practices

### 1. Consistent Data Structure
Ensure all records in the Data list have identical column structure:
```json
// Good
[
  {"id": 1, "name": "John", "age": 30},
  {"id": 2, "name": "Jane", "age": 25}
]

// Bad - inconsistent columns
[
  {"id": 1, "name": "John", "age": 30},
  {"id": 2, "name": "Jane", "email": "jane@example.com"}
]
```

### 2. Use Descriptive Table Names
Choose table names that clearly describe the data:
- `customer_orders` instead of `orders1`
- `employee_attendance` instead of `data`

### 3. Plan Column Types
Consider the data types when structuring your records:
- Use consistent number formats (integer vs decimal)
- Use standard date formats (ISO 8601: YYYY-MM-DD)
- Be explicit with boolean values (true/false)

### 4. Validate Data Before Creation
- Check for duplicate records
- Verify data completeness
- Ensure data quality

### 5. Handle Large Datasets
For very large datasets:
- Consider batch processing
- Monitor database storage capacity
- Use appropriate data types to minimize storage

## Related Catalog Requests

- [Get Data](pages/GetData.md) - Retrieve all rows from a table
- [Search Data](pages/SearchData.md) - Query data with custom SQL
- [Delete Table](pages/DeleteTable.md) - Remove a table from the database
- [Get All Tables](pages/GetAllTables.md) - List all tables in the database

## Technical Implementation

### Helper Class
- **Class**: TableManager
- **Package**: app.krista.extensions.essentials.data_storage.postgresql.impl
- **Method**: `create(String tableName, List<FreeForm> data)`

### Database Operations
1. Analyze first record to determine table schema
2. Generate CREATE TABLE SQL statement
3. Execute table creation
4. Generate INSERT statements for all records
5. Execute batch insert
6. Commit transaction

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure database connection
- [Authentication](pages/Authentication.md) - Database permissions and security

