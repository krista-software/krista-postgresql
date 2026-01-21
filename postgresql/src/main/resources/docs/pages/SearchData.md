# Search Data

## Overview

Executes a custom SQL query against the PostgreSQL database and returns matching records. This operation provides flexible querying capabilities with support for filtering, sorting, joins, and aggregations.

## Request Details

- **Area**: Relational DBs
- **Type**: QUERY_SYSTEM
- **ID**: `localDomainRequest_59e1795e-eebb-4f1f-997d-d46836d5c2a7`

## Input Parameters

| Parameter Name | Type | Required | Description | Example |
|----------------|------|----------|-------------|---------|
| Query | Text | Yes | SQL SELECT query to execute | `SELECT * FROM employees WHERE salary > 70000` |

### Parameter Details

#### Query
A valid PostgreSQL SELECT query that will be executed against the database.

- **Format**: SQL SELECT statement
- **Supported**: SELECT queries only (no INSERT, UPDATE, DELETE, DROP, etc.)
- **Features**: WHERE clauses, ORDER BY, LIMIT, JOIN, GROUP BY, aggregate functions
- **Examples**:
  - `SELECT * FROM employees`
  - `SELECT first_name, last_name FROM employees WHERE department = 'Sales'`
  - `SELECT * FROM products ORDER BY price DESC LIMIT 10`

## Output Parameters

| Parameter Name | Type | Description |
|----------------|------|-------------|
| Data | [ Composite ] | List of records matching the query, where each record contains the selected columns |

### Output Details

#### Data
A list of composite objects where each object represents a row from the query result. Each object contains key-value pairs corresponding to the selected column names and their values.

- **Format**: List of records with column-value pairs
- **Empty Result**: Returns empty list `[]` if no rows match the query
- **Column Selection**: Only columns specified in SELECT are returned

## Validation Rules

| Validation | Error Message | Resolution |
|------------|---------------|------------|
| Query is empty | "Query is required" | Provide a valid SQL query |
| Invalid SQL syntax | "SQL syntax error" | Verify query syntax |
| Table does not exist | "Table does not exist" | Verify table name in query |
| Column does not exist | "Column does not exist" | Verify column names in query |
| Non-SELECT query | "Only SELECT queries allowed" | Use SELECT statement only |

## Usage Examples

### Example 1: Filter by Salary

**Input**:
```json
{
  "Query": "SELECT * FROM employees WHERE salary > 70000"
}
```

**Output**:
```json
{
  "Data": [
    {
      "employee_id": 2,
      "first_name": "Jane",
      "last_name": "Smith",
      "email": "jane.smith@example.com",
      "hire_date": "2024-02-01",
      "salary": 82000
    },
    {
      "employee_id": 3,
      "first_name": "Bob",
      "last_name": "Johnson",
      "email": "bob.johnson@example.com",
      "hire_date": "2024-01-20",
      "salary": 95000
    }
  ]
}
```

### Example 2: Select Specific Columns

**Input**:
```json
{
  "Query": "SELECT product_name, price FROM products WHERE category = 'Electronics'"
}
```

**Output**:
```json
{
  "Data": [
    {
      "product_name": "Laptop",
      "price": 999.99
    },
    {
      "product_name": "Monitor",
      "price": 299.99
    }
  ]
}
```

### Example 3: Sort and Limit Results

**Input**:
```json
{
  "Query": "SELECT * FROM products ORDER BY price DESC LIMIT 5"
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
      "product_id": "P003",
      "product_name": "Tablet",
      "category": "Electronics",
      "quantity": 30,
      "price": 599.99,
      "in_stock": true
    }
  ]
}
```

### Example 4: Aggregate Functions

**Input**:
```json
{
  "Query": "SELECT category, COUNT(*) as product_count, AVG(price) as avg_price FROM products GROUP BY category"
}
```

**Output**:
```json
{
  "Data": [
    {
      "category": "Electronics",
      "product_count": 15,
      "avg_price": 549.99
    },
    {
      "category": "Accessories",
      "product_count": 42,
      "avg_price": 29.99
    }
  ]
}
```

### Example 5: Join Multiple Tables

**Input**:
```json
{
  "Query": "SELECT e.first_name, e.last_name, d.department_name FROM employees e JOIN departments d ON e.department_id = d.id"
}
```

**Output**:
```json
{
  "Data": [
    {
      "first_name": "John",
      "last_name": "Doe",
      "department_name": "Engineering"
    },
    {
      "first_name": "Jane",
      "last_name": "Smith",
      "department_name": "Sales"
    }
  ]
}
```

## Business Rules

### 1. Query Restrictions
- **Only SELECT queries** are allowed
- No data modification queries (INSERT, UPDATE, DELETE)
- No schema modification queries (CREATE, ALTER, DROP)
- No transaction control statements (BEGIN, COMMIT, ROLLBACK)

### 2. SQL Features Supported
- **Filtering**: WHERE clauses with comparison operators
- **Sorting**: ORDER BY with ASC/DESC
- **Limiting**: LIMIT and OFFSET for pagination
- **Joins**: INNER JOIN, LEFT JOIN, RIGHT JOIN, FULL OUTER JOIN
- **Aggregation**: COUNT, SUM, AVG, MIN, MAX, GROUP BY, HAVING
- **Functions**: String functions, date functions, mathematical functions
- **Subqueries**: Nested SELECT statements

### 3. Performance
- Complex queries may take longer to execute
- Use LIMIT to restrict result set size
- Create indexes on frequently queried columns for better performance

## Common Use Cases

### 1. Filtered Data Retrieval
```
Scenario: Find all high-value customers
Query: SELECT * FROM customers WHERE total_purchases > 10000
Result: List of customers with purchases exceeding $10,000
```

### 2. Reporting and Analytics
```
Scenario: Generate monthly sales report
Query: SELECT DATE_TRUNC('month', order_date) as month, SUM(total) as revenue FROM orders GROUP BY month
Result: Monthly revenue aggregated by month
```

### 3. Data Validation
```
Scenario: Find duplicate email addresses
Query: SELECT email, COUNT(*) FROM users GROUP BY email HAVING COUNT(*) > 1
Result: Email addresses that appear more than once
```

### 4. Pagination
```
Scenario: Retrieve page 2 of employee list (10 per page)
Query: SELECT * FROM employees ORDER BY employee_id LIMIT 10 OFFSET 10
Result: Employees 11-20 in sorted order
```

## Advanced Query Examples

### Date Range Filtering
```sql
SELECT * FROM orders 
WHERE order_date BETWEEN '2024-01-01' AND '2024-12-31'
ORDER BY order_date DESC
```

### Pattern Matching
```sql
SELECT * FROM employees 
WHERE email LIKE '%@example.com'
```

### Multiple Conditions
```sql
SELECT * FROM products 
WHERE (category = 'Electronics' OR category = 'Computers')
  AND price < 1000
  AND in_stock = true
```

### Calculated Columns
```sql
SELECT 
  product_name,
  price,
  quantity,
  (price * quantity) as total_value
FROM products
WHERE quantity > 0
```

## Error Handling

### SQL Syntax Error
**Error**: "Syntax error near 'FORM'"

**Cause**: Invalid SQL syntax in the query

**Resolution**:
1. Review SQL syntax
2. Check for typos (e.g., "FORM" instead of "FROM")
3. Validate query in a SQL client before using in extension

### Table Does Not Exist
**Error**: "Relation 'tablename' does not exist"

**Cause**: Referenced table doesn't exist in the database

**Resolution**:
1. Use [Get All Tables](pages/GetAllTables.md) to list available tables
2. Verify table name spelling and case
3. Create table if needed using [Create Data](pages/CreateData.md)

### Column Does Not Exist
**Error**: "Column 'columnname' does not exist"

**Cause**: Referenced column doesn't exist in the table

**Resolution**:
1. Use [Get Data](pages/GetData.md) to see available columns
2. Verify column name spelling and case
3. Update query with correct column names

### Permission Denied
**Error**: "Permission denied for table tablename"

**Cause**: Database user doesn't have SELECT permission

**Resolution**:
1. Grant SELECT permission:
   ```sql
   GRANT SELECT ON TABLE tablename TO krista_user;
   ```
2. Contact database administrator

## Best Practices

### 1. Use Parameterized Queries
For dynamic queries, ensure proper escaping to prevent SQL injection:
- Avoid concatenating user input directly into queries
- Use proper quoting for string literals

### 2. Optimize Query Performance
- Use WHERE clauses to filter data at the database level
- Create indexes on frequently queried columns
- Use LIMIT to restrict result set size
- Avoid SELECT * when only specific columns are needed

### 3. Test Queries First
- Test complex queries in a SQL client (psql, pgAdmin) before using in extension
- Verify query returns expected results
- Check query execution time for performance

### 4. Handle Large Result Sets
For queries returning many rows:
- Use LIMIT and OFFSET for pagination
- Consider breaking into smaller queries
- Monitor memory usage

### 5. Use Meaningful Column Aliases
```sql
-- Good
SELECT 
  COUNT(*) as total_orders,
  SUM(amount) as total_revenue
FROM orders

-- Less clear
SELECT 
  COUNT(*) as c,
  SUM(amount) as s
FROM orders
```

## Related Catalog Requests

- [Get Data](pages/GetData.md) - Retrieve all rows from a table
- [Create Data](pages/CreateData.md) - Create table and insert records
- [Delete Table](pages/DeleteTable.md) - Remove a table from the database
- [Get All Tables](pages/GetAllTables.md) - List all tables in the database

## Technical Implementation

### Helper Class
- **Class**: TableManager
- **Package**: app.krista.extensions.essentials.data_storage.postgresql.impl
- **Method**: `search(String query)`

### Database Operations
1. Validate query is not empty
2. Establish database connection
3. Prepare SQL statement
4. Execute query
5. Fetch all result rows
6. Convert rows to FreeForm objects
7. Return list of records
8. Close connection

## See Also

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure database connection
- [Authentication](pages/Authentication.md) - Database permissions and security
- [PostgreSQL SELECT Documentation](https://www.postgresql.org/docs/current/sql-select.html)

