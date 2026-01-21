# Authentication

## Overview

The PostgreSQL Extension uses standard database authentication to establish secure connections with your PostgreSQL database. This page explains the authentication mechanisms, security considerations, and best practices for managing database credentials.

## Authentication Method

### Database Authentication

The extension uses **PostgreSQL native authentication** through JDBC (Java Database Connectivity). When you configure the extension with your database credentials, these are used to authenticate every connection to the PostgreSQL server.

**How it works**:
1. Extension receives a catalog request (e.g., Create Data, Get Data)
2. Extension creates a JDBC connection using configured credentials
3. PostgreSQL server validates the username and password
4. If authentication succeeds, the connection is established
5. Extension executes the requested operation
6. Connection is closed after the operation completes

## Authentication Configuration

### Required Credentials

| Credential | Description | Example |
|------------|-------------|---------|
| User Name | PostgreSQL database user | `postgres`, `app_user` |
| Password | Password for the database user | `********` |
| Database Name | Target database for authentication | `TestDB` |

### PostgreSQL Authentication Methods

PostgreSQL supports multiple authentication methods configured in `pg_hba.conf`:

#### 1. Password Authentication (md5/scram-sha-256)
Most common method for remote connections. Passwords are hashed before transmission.

**pg_hba.conf entry**:
```
host    all    all    0.0.0.0/0    scram-sha-256
```

**Recommended for**: Production environments with encrypted passwords

#### 2. Trust Authentication
No password required. Only suitable for local development.

**pg_hba.conf entry**:
```
local   all    all    trust
```

**Recommended for**: Local development only (NOT for production)

#### 3. Peer Authentication
Uses operating system username. Only works for local connections.

**pg_hba.conf entry**:
```
local   all    all    peer
```

**Recommended for**: Local administrative access

## Security Considerations

### 1. Credential Storage

**In Krista**:
- Passwords are stored encrypted in Krista's configuration
- Credentials are never logged or exposed in error messages
- Only authorized Krista administrators can view/modify credentials

**Best Practices**:
- Use strong, unique passwords for database users
- Rotate passwords regularly (every 90 days recommended)
- Never share database credentials
- Use different credentials for development and production

### 2. Connection Security

**Unencrypted Connections**:
By default, PostgreSQL connections may be unencrypted, allowing potential eavesdropping.

**SSL/TLS Encryption**:
For production environments, configure PostgreSQL to require SSL/TLS:

**postgresql.conf**:
```
ssl = on
ssl_cert_file = 'server.crt'
ssl_key_file = 'server.key'
```

**pg_hba.conf**:
```
hostssl    all    all    0.0.0.0/0    scram-sha-256
```

### 3. Network Security

**Firewall Configuration**:
- Restrict PostgreSQL port (default 5432) to trusted IP addresses
- Use VPN or private networks for remote access
- Never expose PostgreSQL directly to the public internet

**Example firewall rule (iptables)**:
```bash
iptables -A INPUT -p tcp --dport 5432 -s <krista-server-ip> -j ACCEPT
iptables -A INPUT -p tcp --dport 5432 -j DROP
```

### 4. User Permissions

**Principle of Least Privilege**:
Grant only the minimum permissions required for Krista operations.

**Example - Read-Only User**:
```sql
CREATE USER krista_readonly WITH PASSWORD 'strong_password';
GRANT CONNECT ON DATABASE TestDB TO krista_readonly;
GRANT USAGE ON SCHEMA public TO krista_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO krista_readonly;
```

**Example - Full Access User**:
```sql
CREATE USER krista_admin WITH PASSWORD 'strong_password';
GRANT CONNECT ON DATABASE TestDB TO krista_admin;
GRANT USAGE ON SCHEMA public TO krista_admin;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO krista_admin;
GRANT CREATE ON SCHEMA public TO krista_admin;
```

## Authentication Flow

### Successful Authentication

```
1. Krista Extension initiates connection
   ↓
2. JDBC driver sends credentials to PostgreSQL
   ↓
3. PostgreSQL validates username/password
   ↓
4. PostgreSQL checks user permissions for database
   ↓
5. Connection established
   ↓
6. Extension executes SQL operation
   ↓
7. Connection closed
```

### Failed Authentication

```
1. Krista Extension initiates connection
   ↓
2. JDBC driver sends credentials to PostgreSQL
   ↓
3. PostgreSQL rejects invalid credentials
   ↓
4. Extension receives authentication error
   ↓
5. Error returned to user (credentials not exposed)
```

## Common Authentication Issues

### Issue 1: "Authentication failed for user"

**Cause**: Incorrect username or password

**Resolution**:
1. Verify credentials in extension configuration
2. Test credentials using psql: `psql -U <username> -d <database> -h <host>`
3. Reset password if needed:
   ```sql
   ALTER USER username WITH PASSWORD 'new_password';
   ```

### Issue 2: "No pg_hba.conf entry for host"

**Cause**: PostgreSQL server doesn't allow connections from Krista's IP address

**Resolution**:
1. Edit `pg_hba.conf` to allow connections from Krista server
2. Add entry: `host all all <krista-ip>/32 scram-sha-256`
3. Reload PostgreSQL: `pg_ctl reload` or `systemctl reload postgresql`

### Issue 3: "FATAL: database does not exist"

**Cause**: Database name is incorrect or database hasn't been created

**Resolution**:
1. List databases: `\l` in psql
2. Create database if needed: `CREATE DATABASE TestDB;`
3. Update extension configuration with correct database name

### Issue 4: "Connection refused"

**Cause**: PostgreSQL server not listening on the specified address/port

**Resolution**:
1. Verify PostgreSQL is running: `systemctl status postgresql`
2. Check `postgresql.conf` for `listen_addresses`:
   ```
   listen_addresses = '*'  # or specific IP
   ```
3. Restart PostgreSQL: `systemctl restart postgresql`

### Issue 5: "SSL connection required"

**Cause**: PostgreSQL server requires SSL but extension isn't configured for SSL

**Resolution**:
1. Configure PostgreSQL to allow non-SSL connections (development only)
2. Or configure JDBC connection string to use SSL (contact Krista support)

## Best Practices

### 1. Use Dedicated Database Users
Create separate database users for Krista instead of using superuser accounts.

### 2. Implement Role-Based Access Control
Use PostgreSQL roles to manage permissions:

```sql
-- Create role with specific permissions
CREATE ROLE krista_role;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO krista_role;

-- Assign role to user
CREATE USER krista_user WITH PASSWORD 'password';
GRANT krista_role TO krista_user;
```

### 3. Monitor Authentication Attempts
Enable PostgreSQL logging to track authentication attempts:

**postgresql.conf**:
```
log_connections = on
log_disconnections = on
log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h '
```

### 4. Regular Security Audits
- Review user permissions quarterly
- Audit database access logs
- Remove unused database users
- Update passwords regularly

### 5. Use Connection Pooling
For high-volume environments, consider connection pooling to reduce authentication overhead.

## Related Documentation

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure database connection parameters
- [Create Table](pages/CreateTable.md) - Required permissions for table creation
- [PostgreSQL Security Documentation](https://www.postgresql.org/docs/current/auth-methods.html)

## Support & Resources

- [PostgreSQL Authentication Methods](https://www.postgresql.org/docs/current/auth-methods.html)
- [PostgreSQL SSL Support](https://www.postgresql.org/docs/current/ssl-tcp.html)
- [JDBC PostgreSQL Driver](https://jdbc.postgresql.org/)

