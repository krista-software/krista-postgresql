# Delete Table

## Overview

Permanently removes a table and all its data from the PostgreSQL database. This operation cannot be undone, so use with caution.

## Request Details

- **Area**: Relational DBs
- **Type**: CHANGE_SYSTEM
- **ID**: `localDomainRequest_8bb13f19-fa3a-481f-a265-685f65ca6dc7`

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Table Name | Text | Yes | Name of the table to delete | `old_employees` |

### Parameter Details

#### Table Name
The name of the existing table to permanently remove from the database.

- **Format**: Valid PostgreSQL table name
- **Case Sensitivity**: PostgreSQL table names are case-sensitive when quoted
- **Examples**: `temp_data`, `old_employees`, `archived_orders`
- **Warning**: This operation is irreversible

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Message | Text | Confirmation message indicating the table was deleted successfully |

### Output Details

#### Message
A text message confirming the successful deletion of the table.

- **Format**: String message
- **Example**: `"Table 'old_employees' was deleted successfully."`

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Table Name is empty | "Table Name is required" | Provide a valid table name |
| Table does not exist | "Table 'tablename' does not exist" | Verify table name or check if already deleted |
| Insufficient permissions | "Permission denied" | Ensure database user has DROP permission |
| Table has dependencies | "Cannot drop table due to dependencies" | Remove dependent objects first or use CASCADE |

## Usage Examples

### Example 1: Delete Temporary Table

**Input**:
```json
{
  "Table Name": "temp_processing"
}
```

**Output**:
```json
{
  "Message": "Table 'temp_processing' was deleted successfully."
}
```

**Result**: Table `temp_processing` and all its data permanently removed

### Example 2: Delete Archived Data Table

**Input**:
```json
{
  "Table Name": "archived_orders_2023"
}
```

**Output**:
```json
{
  "Message": "Table 'archived_orders_2023' was deleted successfully."
}
```

**Result**: Table `archived_orders_2023` and all historical data permanently removed

### Example 3: Delete Test Table

**Input**:
```json
{
  "Table Name": "test_data"
}
```

**Output**:
```json
{
  "Message": "Table 'test_data' was deleted successfully."
}
```

**Result**: Test table removed from database

## Business Rules

### 1. Permanent Deletion
- Table and ALL its data are permanently removed
- Operation cannot be undone
- No recovery possible without database backup

### 2. Cascade Behavior
- By default, deletion fails if other objects depend on the table
- Dependencies include: foreign keys, views, triggers, functions
- Contact administrator if cascade deletion is needed

### 3. Transaction Handling
- Table deletion is performed as a single transaction
- If deletion fails, database state remains unchanged
- No partial deletions occur

### 4. Permissions Required
Database user must have:
- DROP permission on the table
- Ownership of the table OR superuser privileges

## Common Use Cases

### 1. Clean Up Temporary Tables
```
Scenario: Remove temporary processing tables after job completion
Action: Delete Table for each temporary table
Result: Database cleaned up, storage reclaimed
```

### 2. Remove Archived Data
```
Scenario: Delete old archived tables no longer needed
Action: Delete Table for archived_data_2020
Result: Old data removed, storage freed
```

### 3. Development and Testing
```
Scenario: Clean up test tables after testing
Action: Delete Table for test_employees
Result: Test data removed from database
```

### 4. Data Migration Cleanup
```
Scenario: Remove staging tables after successful migration
Action: Delete Table for staging_customers
Result: Staging tables removed
```

## Error Handling

### Table Does Not Exist
**Error**: "Table 'employees' does not exist"

**Cause**: The specified table name doesn't exist in the database

**Resolution**:
1. Verify the table name is spelled correctly
2. Check if table was already deleted
3. Use [Get All Tables](pages/GetAllTables.md) to list existing tables

### Permission Denied
**Error**: "Permission denied to drop table employees"

**Cause**: Database user doesn't have DROP permission on the table

**Resolution**:
1. Grant DROP permission to the database user:
   ```sql
   GRANT DROP ON TABLE employees TO krista_user;
   ```
2. Or grant ownership:
   ```sql
   ALTER TABLE employees OWNER TO krista_user;
   ```
3. Contact database administrator

### Table Has Dependencies
**Error**: "Cannot drop table employees because other objects depend on it"

**Cause**: Other database objects (views, foreign keys, etc.) reference this table

**Resolution**:
1. Identify dependencies:
   ```sql
   SELECT * FROM information_schema.table_constraints 
   WHERE table_name = 'employees';
   ```
2. Remove dependent objects first
3. Or use CASCADE (requires manual SQL - contact administrator):
   ```sql
   DROP TABLE employees CASCADE;
   ```

### Connection Error
**Error**: "Could not connect to database"

**Cause**: Database connection failed

**Resolution**:
1. Verify database connection parameters in [Extension Configuration](pages/ExtensionConfiguration.md)
2. Ensure PostgreSQL server is running
3. Check network connectivity

## Safety Considerations

### Before Deleting a Table

1. **Verify Table Name**
   - Double-check the table name
   - Use [Get All Tables](pages/GetAllTables.md) to confirm

2. **Backup Data**
   - Export data using [Get Data](pages/GetData.md) if needed
   - Ensure database backups are current
   - Consider archiving data before deletion

3. **Check Dependencies**
   - Verify no other tables reference this table via foreign keys
   - Check for views that use this table
   - Review application code for table references

4. **Confirm with Stakeholders**
   - Ensure all stakeholders approve the deletion
   - Document the deletion decision
   - Communicate the change to affected teams

5. **Test in Non-Production First**
   - Test deletion in development environment
   - Verify no unexpected side effects
   - Confirm application continues to function

## Best Practices

### 1. Use Descriptive Naming for Temporary Tables
Make temporary tables easy to identify:
- `temp_processing_20241124`
- `staging_import_customers`
- `test_data_analysis`

### 2. Regular Cleanup Schedule
Establish a routine for cleaning up temporary tables:
- Daily: Remove processing tables
- Weekly: Clean up test tables
- Monthly: Archive and delete old data tables

### 3. Document Table Deletions
Maintain a log of deleted tables:
- Table name
- Deletion date
- Reason for deletion
- Person who authorized deletion

### 4. Implement Soft Deletes for Important Data
For critical tables, consider:
- Adding an `is_deleted` flag instead of dropping table
- Moving data to archive table before deletion
- Maintaining audit trail

### 5. Verify Before Deletion
Always verify table contents before deletion:
```sql
-- Check row count
SELECT COUNT(*) FROM table_name;

-- Review sample data
SELECT * FROM table_name LIMIT 10;
```

## Recovery Options

### If Table Deleted by Mistake

1. **Restore from Backup**
   - Contact database administrator
   - Restore from most recent backup
   - May lose data created after backup

2. **Point-in-Time Recovery**
   - If PostgreSQL WAL archiving is enabled
   - Restore database to point before deletion
   - Requires administrator assistance

3. **Recreate Table**
   - If data is available elsewhere
   - Use [Create Data](pages/CreateData.md) to recreate
   - Manually restore data from source

## Alternative Approaches

### Instead of Deleting Table

Consider these alternatives:

1. **Rename Table**
   ```sql
   ALTER TABLE employees RENAME TO employees_old;
   ```

2. **Move to Archive Schema**
   ```sql
   ALTER TABLE employees SET SCHEMA archive;
   ```

3. **Truncate Table** (keeps structure, removes data)
   ```sql
   TRUNCATE TABLE employees;
   ```

4. **Soft Delete** (add is_deleted flag)
   ```sql
   ALTER TABLE employees ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
   UPDATE employees SET is_deleted = TRUE;
   ```

## Related Catalog Requests

- [Create Data](pages/CreateData.md) - Create new table with data
- [Get Data](pages/GetData.md) - Retrieve data before deletion
- [Get All Tables](pages/GetAllTables.md) - List all tables to verify deletion
- [Search Data](pages/SearchData.md) - Query table before deletion

## Technical Implementation

### Helper Class
- **Class**: TableManager
- **Package**: app.krista.extensions.essentials.data_storage.postgresql.impl
- **Method**: `deleteTable(String tableName)`

### Database Operations
1. Validate table name is not empty
2. Establish database connection
3. Verify table exists
4. Execute SQL: `DROP TABLE <tableName>`
5. Commit transaction
6. Return success message
7. Close connection

### SQL Generated
```sql
DROP TABLE <table_name>;
```

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure database connection
- [Authentication](pages/Authentication.md) - Database permissions and security
- [PostgreSQL DROP TABLE Documentation](https://www.postgresql.org/docs/current/sql-droptable.html)

