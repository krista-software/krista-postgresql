# Get All Tables

## Overview

Retrieves a list of all table names that exist in the PostgreSQL database. This operation provides a complete inventory of tables available in the configured database.

## Request Details

- **Area**: Relational DBs
- **Type**: QUERY_SYSTEM
- **ID**: `localDomainRequest_6529f402-07a2-4132-ac1e-fae0d9620302`

## Input Parameters

This operation does not require any input parameters.

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Tables | [ Text ] | List of all table names in the database |

### Output Details

#### Tables
A list of strings where each string is the name of a table in the database.

- **Format**: List of table names
- **Order**: Alphabetical order (typically)
- **Empty Result**: Returns empty list `[]` if database has no tables
- **System Tables**: Only user-created tables are returned (system tables excluded)

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Connection failure | "Could not connect to database" | Verify connection configuration |
| Insufficient permissions | "Permission denied" | Ensure database user has appropriate permissions |

## Usage Examples

### Example 1: List All Tables

**Input**: (No input required)

**Output**:
```json
{
  "Tables": [
    "customers",
    "employees",
    "orders",
    "products",
    "departments"
  ]
}
```

**Result**: List of 5 tables in the database

### Example 2: Empty Database

**Input**: (No input required)

**Output**:
```json
{
  "Tables": []
}
```

**Result**: Empty list indicating no user tables exist

### Example 3: Single Table

**Input**: (No input required)

**Output**:
```json
{
  "Tables": [
    "configuration"
  ]
}
```

**Result**: Database contains only one table

## Business Rules

### 1. User Tables Only
- Returns only user-created tables
- System tables (pg_catalog, information_schema) are excluded
- Temporary tables may or may not be included depending on session

### 2. Schema Scope
- Returns tables from the current schema (typically 'public')
- Tables in other schemas may not be visible
- Schema can be configured in connection settings

### 3. Permissions
- Only tables the database user has permission to see are returned
- Hidden tables (no SELECT permission) won't appear in the list

## Common Use Cases

### 1. Database Discovery
```
Scenario: Explore available tables in a new database
Action: Get All Tables
Result: Complete list of tables to work with
```

### 2. Validation Before Operations
```
Scenario: Verify table exists before querying
Action: Get All Tables, then check if target table is in list
Result: Confirm table availability
```

### 3. Database Documentation
```
Scenario: Generate database inventory for documentation
Action: Get All Tables
Result: List of all tables for documentation
```

### 4. Cleanup Operations
```
Scenario: Identify tables to delete during cleanup
Action: Get All Tables, then selectively delete unwanted tables
Result: Clean database with only necessary tables
```

### 5. Migration Planning
```
Scenario: Plan data migration from PostgreSQL
Action: Get All Tables to identify all tables to migrate
Result: Complete table inventory for migration
```

## Error Handling

### Connection Error
**Error**: "Could not connect to database"

**Cause**: Database connection failed

**Resolution**:
1. Verify database connection parameters in [Extension Configuration](pages/ExtensionConfiguration.md)
2. Ensure PostgreSQL server is running
3. Check network connectivity
4. Verify credentials are correct

### Permission Denied
**Error**: "Permission denied to access schema information"

**Cause**: Database user doesn't have permission to query system tables

**Resolution**:
1. Grant necessary permissions:
   ```sql
   GRANT USAGE ON SCHEMA public TO krista_user;
   GRANT SELECT ON ALL TABLES IN SCHEMA information_schema TO krista_user;
   ```
2. Contact database administrator
3. Use a user account with appropriate permissions

### Empty Result When Tables Exist
**Issue**: Get All Tables returns empty list but tables exist

**Cause**: Tables may be in a different schema or user lacks permissions

**Resolution**:
1. Check current schema:
   ```sql
   SELECT current_schema();
   ```
2. List tables in specific schema:
   ```sql
   SELECT tablename FROM pg_tables WHERE schemaname = 'your_schema';
   ```
3. Verify user has SELECT permission on tables

## Integration Patterns

### Pattern 1: Verify Table Before Query
```
1. Call Get All Tables
2. Check if target table exists in returned list
3. If exists, proceed with Get Data or Search Data
4. If not exists, handle error or create table
```

### Pattern 2: Iterate Through All Tables
```
1. Call Get All Tables to get list
2. For each table in list:
   a. Call Get Data to retrieve table contents
   b. Process or export data
   c. Move to next table
```

### Pattern 3: Conditional Table Creation
```
1. Call Get All Tables
2. Check if desired table exists
3. If not exists, call Create Data to create table
4. If exists, skip creation or handle conflict
```

### Pattern 4: Database Cleanup
```
1. Call Get All Tables
2. Identify tables to delete (e.g., temp tables, old tables)
3. For each table to delete:
   a. Optionally backup with Get Data
   b. Call Delete Table
```

## Best Practices

### 1. Cache Results When Appropriate
If table structure doesn't change frequently:
- Cache the table list to reduce database queries
- Refresh cache periodically or on-demand
- Improves performance for repeated operations

### 2. Use for Validation
Before performing operations on a table:
```
1. Get All Tables
2. Verify target table exists
3. Proceed with operation
4. Handle "table not found" gracefully
```

### 3. Combine with Other Operations
Use Get All Tables as a discovery step:
```
1. Get All Tables → Discover available tables
2. Get Data → Examine table structure and content
3. Search Data → Query specific data
```

### 4. Handle Empty Results
Always check for empty table list:
```javascript
if (tables.length === 0) {
  // Handle empty database case
  // Maybe create initial tables
}
```

### 5. Document Table Purposes
After getting table list, document:
- Purpose of each table
- Data contained in each table
- Relationships between tables
- Owner/maintainer of each table

## Performance Considerations

### Fast Operation
- Get All Tables is a lightweight operation
- Queries system catalog tables only
- Typically completes in milliseconds
- Safe to call frequently

### No Data Transfer
- Only table names are returned (not data)
- Minimal network bandwidth usage
- No memory concerns even with many tables

## Output Format Details

### Table Name Format
Table names are returned as they are stored in PostgreSQL:
- **Lowercase**: Unquoted table names are stored in lowercase
  - Created as: `CREATE TABLE MyTable` → Stored as: `mytable`
- **Mixed Case**: Quoted table names preserve case
  - Created as: `CREATE TABLE "MyTable"` → Stored as: `MyTable`
- **Special Characters**: Tables with special characters are quoted
  - Example: `"table-with-dashes"`

### Sorting
- Tables are typically returned in alphabetical order
- Order may vary depending on PostgreSQL version
- Don't rely on specific ordering for logic

## Related Catalog Requests

- [Create Data](pages/CreateData.md) - Create a new table
- [Delete Table](pages/DeleteTable.md) - Remove a table from the list
- [Get Data](pages/GetData.md) - Retrieve data from a specific table
- [Search Data](pages/SearchData.md) - Query data from tables

## Technical Implementation

### Helper Class
- **Class**: TableManager
- **Package**: app.krista.extensions.essentials.data_storage.postgresql.impl
- **Method**: `listTables()`

### Database Operations
1. Establish database connection
2. Query system catalog for user tables
3. Execute SQL query against information_schema or pg_tables
4. Fetch all table names
5. Return list of table names
6. Close connection

### SQL Generated
Typical query used (may vary by implementation):
```sql
SELECT tablename 
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY tablename;
```

Or using information_schema:
```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_type = 'BASE TABLE'
ORDER BY table_name;
```

## Use Cases by Role

### Database Administrator
- Audit database contents
- Verify table creation/deletion
- Monitor database growth
- Plan maintenance activities

### Developer
- Discover available data sources
- Validate table existence before queries
- Debug connection and permission issues
- Generate database documentation

### Data Analyst
- Identify tables for analysis
- Explore database structure
- Plan data extraction queries
- Document data sources

### Integration Engineer
- Map source tables for integration
- Validate migration completeness
- Verify data synchronization
- Plan ETL processes

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure database connection
- [Authentication](pages/Authentication.md) - Database permissions and security
- [PostgreSQL System Catalogs](https://www.postgresql.org/docs/current/catalogs.html)

