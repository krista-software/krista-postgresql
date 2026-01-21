# Create Table

## Overview

The PostgreSQL extension does not have a standalone "Create Table" catalog request. Table creation is automatically handled by the [Create Data](pages/CreateData.md) operation, which creates the table and inserts data in a single step.

## How Tables Are Created

Tables in the PostgreSQL extension are created implicitly when you use the **Create Data** catalog request. The table schema (columns and data types) is automatically inferred from the data you provide.

### Automatic Schema Inference

When you call Create Data:
1. The first record in your data list defines the table schema
2. Column names are derived from the keys in the first record
3. Column data types are inferred from the values
4. The table is created with the inferred schema
5. All records are inserted into the newly created table

## Creating Tables with Create Data

To create a table, use the [Create Data](pages/CreateData.md) catalog request with:
- **Table Name**: The name for your new table
- **Data**: At least one record that defines the table structure

### Example: Create Employee Table

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
    }
  ]
}
```

**Result**: 
- Table `employees` is created with 6 columns
- Column types are inferred: employee_id (INTEGER), first_name (VARCHAR), last_name (VARCHAR), email (VARCHAR), hire_date (DATE), salary (NUMERIC)
- 1 row is inserted

## Data Type Inference

The extension automatically determines column types based on the values in your first record:

| Value Type | PostgreSQL Type | Example |
|------------|-----------------|---------|
| Integer number | INTEGER | `123` |
| Decimal number | NUMERIC or DECIMAL | `99.99` |
| Text string | VARCHAR or TEXT | `"John Doe"` |
| Boolean | BOOLEAN | `true` or `false` |
| Date string | DATE or TIMESTAMP | `"2024-01-15"` |

### Type Inference Examples

```json
{
  "id": 1,              // → INTEGER
  "name": "Product",    // → VARCHAR
  "price": 29.99,       // → NUMERIC
  "in_stock": true,     // → BOOLEAN
  "created": "2024-01-15" // → DATE or TIMESTAMP
}
```

## Table Naming Rules

Follow PostgreSQL naming conventions for table names:

### Valid Table Names
- Must start with a letter (a-z) or underscore (_)
- Can contain letters, numbers, and underscores
- Maximum length: 63 characters
- Case-sensitive when quoted, lowercase when unquoted

### Examples
- ✅ `employees`
- ✅ `customer_orders`
- ✅ `product_inventory_2024`
- ✅ `_temp_data`
- ❌ `123_table` (starts with number)
- ❌ `my-table` (contains hyphen)
- ❌ `table name` (contains space)

## Creating Tables with Specific Structures

### Single Column Table
```json
{
  "Table Name": "simple_list",
  "Data": [
    {"item": "First item"},
    {"item": "Second item"}
  ]
}
```

### Multi-Column Table
```json
{
  "Table Name": "products",
  "Data": [
    {
      "product_id": "P001",
      "name": "Laptop",
      "category": "Electronics",
      "price": 999.99,
      "quantity": 50
    }
  ]
}
```

### Table with Mixed Data Types
```json
{
  "Table Name": "orders",
  "Data": [
    {
      "order_id": 1001,
      "customer_name": "John Doe",
      "order_date": "2024-11-24",
      "total_amount": 1299.99,
      "is_paid": true,
      "items_count": 3
    }
  ]
}
```

## Best Practices

### 1. Design Schema First
Before creating a table, plan your schema:
- Determine required columns
- Choose appropriate data types
- Consider future data needs

### 2. Use Consistent Naming
- Use lowercase with underscores: `customer_orders`
- Be descriptive: `employee_attendance` not `emp_att`
- Use singular or plural consistently

### 3. Include Primary Key
Always include an ID column for record identification:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com"
}
```

### 4. Provide Representative Data
The first record should include all columns you need:
```json
// Good - all columns present
{
  "id": 1,
  "name": "John",
  "email": "john@example.com",
  "phone": "555-1234"
}

// Bad - missing columns
{
  "id": 1,
  "name": "John"
}
// Later records can't add email and phone columns
```

### 5. Use Appropriate Data Types
Structure your data to get the right column types:
- Use numbers without quotes: `123` not `"123"`
- Use standard date formats: `"2024-01-15"`
- Use boolean values: `true`/`false` not `"yes"`/`"no"`

## Common Use Cases

### 1. Create Configuration Table
```json
{
  "Table Name": "app_config",
  "Data": [
    {
      "config_key": "app_version",
      "config_value": "1.0.0",
      "last_updated": "2024-11-24"
    }
  ]
}
```

### 2. Create Lookup Table
```json
{
  "Table Name": "status_codes",
  "Data": [
    {"code": "ACTIVE", "description": "Active status"},
    {"code": "INACTIVE", "description": "Inactive status"},
    {"code": "PENDING", "description": "Pending approval"}
  ]
}
```

### 3. Create Transaction Table
```json
{
  "Table Name": "transactions",
  "Data": [
    {
      "transaction_id": 1,
      "account_id": 1001,
      "amount": 250.00,
      "transaction_date": "2024-11-24",
      "type": "CREDIT",
      "description": "Payment received"
    }
  ]
}
```

## Limitations

### No Custom Constraints
The automatic table creation does not support:
- Primary key constraints
- Foreign key constraints
- Unique constraints
- Check constraints
- Default values
- NOT NULL constraints (except inferred)

### No Index Creation
- No automatic index creation
- No custom indexes
- Primary key index not created automatically

### No Custom Data Types
- Cannot specify custom PostgreSQL types
- Cannot use ENUM types
- Cannot use ARRAY types
- Limited to basic data types

## Advanced Table Creation

For advanced table creation with constraints, indexes, and custom types, you would need to:

1. **Use Direct SQL** (outside this extension)
   ```sql
   CREATE TABLE employees (
     employee_id SERIAL PRIMARY KEY,
     first_name VARCHAR(50) NOT NULL,
     last_name VARCHAR(50) NOT NULL,
     email VARCHAR(100) UNIQUE,
     hire_date DATE DEFAULT CURRENT_DATE,
     salary NUMERIC(10,2) CHECK (salary > 0)
   );
   ```

2. **Then Use Extension** for data operations
   - Use [Get Data](pages/GetData.md) to retrieve records
   - Use [Search Data](pages/SearchData.md) to query
   - Note: Cannot use [Create Data](pages/CreateData.md) if table already exists

## Checking If Table Exists

Before creating a table, check if it already exists:

1. Use [Get All Tables](pages/GetAllTables.md) to list all tables
2. Check if your desired table name is in the list
3. If exists, either:
   - Use a different name
   - Delete the existing table with [Delete Table](pages/DeleteTable.md)
   - Use the existing table

## Error Handling

### Table Already Exists
**Error**: "Table 'employees' already exists"

**Cause**: A table with that name already exists

**Resolution**:
1. Choose a different table name
2. Delete the existing table using [Delete Table](pages/DeleteTable.md)
3. Use the existing table for your data

### Invalid Table Name
**Error**: "Invalid table name"

**Cause**: Table name doesn't follow PostgreSQL naming rules

**Resolution**:
1. Start with letter or underscore
2. Use only letters, numbers, underscores
3. Keep under 63 characters

### Inconsistent Data Structure
**Error**: "Column mismatch in data records"

**Cause**: Records have different columns

**Resolution**:
1. Ensure all records have the same columns
2. Use null for missing values
3. Verify data structure before submission

## Related Catalog Requests

- [Create Data](pages/CreateData.md) - Create table and insert data (primary method)
- [Get All Tables](pages/GetAllTables.md) - Check if table exists
- [Delete Table](pages/DeleteTable.md) - Remove table if needed
- [Get Data](pages/GetData.md) - Verify table structure after creation

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure database connection
- [Authentication](pages/Authentication.md) - Database permissions
- [PostgreSQL CREATE TABLE Documentation](https://www.postgresql.org/docs/current/sql-createtable.html)

