# Extension Configuration

## Overview

The PostgreSQL Extension requires specific configuration parameters to establish a secure connection with your PostgreSQL database. This page provides detailed information about all configuration parameters and step-by-step setup instructions.

## Configuration Parameters

The following table lists all configuration parameters required for the extension:

| Parameter | Type | Required | Description | Example Value |
|-----------|------|----------|-------------|---------------|
| Server FQDN | Text | Yes | Fully qualified domain name or IP address of the PostgreSQL server | `localhost` or `db.example.com` |
| Database Name | Text | Yes | Name of the PostgreSQL database to connect to | `TestDB` |
| User Name | Text | Yes | Database user with appropriate permissions | `postgres` |
| Password | Text (Secured) | Yes | Password for the database user | `********` |
| Port | Text | Yes | Port number where PostgreSQL server is listening | `5432` |

### Parameter Details

#### Server FQDN
The **Server FQDN** (Fully Qualified Domain Name) is the host address of your PostgreSQL server. JDBC (the Java Database Connectivity API) requires this server address to establish connections with the database.

- **Format**: Hostname, FQDN, or IP address
- **Examples**: 
  - `localhost` (for local development)
  - `db.example.com` (production server)
  - `192.168.1.100` (IP address)

#### Database Name
The **Database Name** specifies which PostgreSQL database you want to connect to. A single PostgreSQL server can host multiple databases.

- **Format**: Valid PostgreSQL database name
- **Example**: `TestDB`, `production_db`, `analytics`
- **Note**: The database must already exist on the server

#### User Name
The **User Name** is the PostgreSQL database user account that will be used for all operations. This user must have appropriate permissions for the operations you plan to perform.

- **Format**: Valid PostgreSQL username
- **Example**: `postgres`, `app_user`, `admin`
- **Required Permissions**: 
  - `CREATE` - For creating tables
  - `SELECT` - For reading data
  - `INSERT` - For creating records
  - `UPDATE` - For modifying records
  - `DELETE` - For removing records and tables

#### Password
The **Password** is the credential for the database user account. This value is stored securely and never exposed in logs or error messages.

- **Format**: String
- **Security**: Stored encrypted in Krista
- **Best Practice**: Use strong passwords and rotate them regularly

#### Port
The **Port** number specifies where the PostgreSQL server is listening for connections.

- **Format**: Numeric port number
- **Default**: `5432` (standard PostgreSQL port)
- **Range**: 1-65535
- **Note**: If your PostgreSQL server uses a custom port, enter that value here

## Step-by-Step Setup

Follow these steps to configure the PostgreSQL Extension:

### 1. Verify PostgreSQL Server Access

Before configuring the extension, ensure:
- PostgreSQL server is running and accessible
- You have valid database credentials
- Network connectivity exists between Krista and the PostgreSQL server
- Firewall rules allow connections on the specified port

### 2. Configure Extension in Krista

1. Navigate to the **Extensions** section in your Krista instance
2. Locate the **PostgreSQL** extension
3. Click **Configure** or **Setup**

### 3. Enter Connection Parameters

1. **Server FQDN**: Enter the hostname or IP address of your PostgreSQL server
2. **Database Name**: Enter the name of the database you want to connect to
3. **User Name**: Enter the database username
4. **Password**: Enter the password for the database user
5. **Port**: Enter the port number (default: `5432`)

### 4. Test Connection

1. Click **Test Connection** to verify the configuration
2. If successful, you'll see a confirmation message
3. If the test fails, verify:
   - Server FQDN is correct and server is reachable
   - Database name exists
   - Username and password are correct
   - Port number is correct
   - Network connectivity and firewall rules

### 5. Save Configuration

1. Click **Save Changes** to store the configuration
2. The extension will now use these parameters for all database operations

### 6. Upgrade Invoker

1. After saving the configuration, upgrade the Invoker
2. This ensures the extension is ready to use with the new configuration

## Connection String Format

The extension constructs a JDBC connection string using your parameters:

```
jdbc:postgresql://<Server FQDN>:<Port>/<Database Name>
```

**Example**:
```
jdbc:postgresql://localhost:5432/TestDB
```

## Security Best Practices

### 1. Use Dedicated Database Users
Create a dedicated database user for Krista instead of using the `postgres` superuser account.

```sql
CREATE USER krista_user WITH PASSWORD 'strong_password';
GRANT CONNECT ON DATABASE TestDB TO krista_user;
GRANT USAGE ON SCHEMA public TO krista_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO krista_user;
```

### 2. Limit Permissions
Grant only the minimum permissions required for your use case.

### 3. Use SSL/TLS
For production environments, configure PostgreSQL to require SSL/TLS connections.

### 4. Network Security
- Use firewalls to restrict database access
- Consider using VPN or private networks
- Avoid exposing PostgreSQL directly to the internet

### 5. Password Management
- Use strong, unique passwords
- Rotate passwords regularly
- Store passwords securely in Krista's encrypted configuration

## Troubleshooting

### Connection Failed

**Error**: "Could not connect to database"

**Possible Causes**:
1. PostgreSQL server is not running
2. Incorrect server FQDN or port
3. Network connectivity issues
4. Firewall blocking connections

**Resolution**:
1. Verify PostgreSQL server is running: `systemctl status postgresql`
2. Test network connectivity: `ping <server-fqdn>`
3. Test port accessibility: `telnet <server-fqdn> <port>`
4. Check PostgreSQL logs for connection attempts
5. Verify firewall rules allow connections on the specified port

### Authentication Failed

**Error**: "Authentication failed for user"

**Possible Causes**:
1. Incorrect username or password
2. User doesn't have permission to access the database
3. PostgreSQL authentication configuration (pg_hba.conf) is restrictive

**Resolution**:
1. Verify username and password are correct
2. Check user permissions: `\du` in psql
3. Review pg_hba.conf for authentication rules
4. Ensure the user has CONNECT privilege on the database

### Database Not Found

**Error**: "Database does not exist"

**Possible Causes**:
1. Database name is misspelled
2. Database hasn't been created yet
3. Case sensitivity issues

**Resolution**:
1. List available databases: `\l` in psql
2. Create the database if needed: `CREATE DATABASE TestDB;`
3. Verify exact database name (PostgreSQL is case-sensitive)

### Port Already in Use

**Error**: "Port is already in use"

**Possible Causes**:
1. Another PostgreSQL instance is using the port
2. Another application is using the port

**Resolution**:
1. Check what's using the port: `lsof -i :<port>` (Linux/Mac) or `netstat -ano | findstr :<port>` (Windows)
2. Stop the conflicting service or use a different port
3. Update the Port parameter in the extension configuration

## Related Documentation

- [Authentication](pages/Authentication.md) - Database authentication and security
- [Create Table](pages/CreateTable.md) - Creating tables after configuration
- [Test Connection](pages/TestConnection.md) - Testing database connectivity

## Support & Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JDBC Driver Documentation](https://jdbc.postgresql.org/documentation/)

