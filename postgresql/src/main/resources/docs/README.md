# PostgreSQL Extension

## Overview

The PostgreSQL Extension enables Krista to connect with PostgreSQL databases to perform comprehensive data management operations. This extension provides seamless integration for managing tables, records, and executing queries within your PostgreSQL database environment.

## Key Features

- **Table Management**: Create and delete tables with custom schemas
- **Data Operations**: Create, read, search, and delete records
- **Flexible Queries**: Execute custom SQL queries with parameter support
- **Connection Management**: Secure database connections with configurable parameters
- **Error Handling**: Comprehensive validation and error reporting
- **JDBC Integration**: Standard JDBC connectivity for reliable database access

## Quick Start Guide

1. **Configure Connection** - Set up Server FQDN, Database Name, Username, Password, and Port
2. **Test Connection** - Verify database connectivity
3. **Create Tables** - Define table schemas with columns and data types
4. **Manage Data** - Execute catalog requests to create, read, search, and delete data

## Documentation Structure

### Getting Started

- [Extension Configuration](pages/ExtensionConfiguration.md) - Configure connection parameters and settings
- [Authentication](pages/Authentication.md) - Database authentication and security

### Table Management

- [Create Table](pages/CreateTable.md) - Create new tables with custom schemas
- [Delete Table](pages/DeleteTable.md) - Remove tables from the database
- [Get All Tables](pages/GetAllTables.md) - List all tables in the database

### Data Operations

- [Create Data](pages/CreateData.md) - Insert new records into tables
- [Get Data](pages/GetData.md) - Retrieve records from tables
- [Search Data](pages/SearchData.md) - Search for records using filters
- [Delete Data](pages/DeleteData.md) - Remove records from tables

## Connection Parameters

### Server FQDN
Fully qualified domain name of the server or host. JDBC (the Java Database Connectivity API) requires the server address to connect with individual databases.

**Example**: `localhost`

### Database Name
Name of the database that you are trying to connect to.

**Example**: `TestDB`

### User Name
The username is the name of the user in the database with appropriate permissions.

### Password
Password for the user to connect to the database.

### Port
Port number of the PostgreSQL server.

**Example**: `5432` (default PostgreSQL port)

## Setup Instructions

1. Navigate to the Extension setup page in Krista
2. Enter the following connection parameters:
   - Server FQDN
   - Database Name
   - Username
   - Password
   - Port
3. Click "Save Changes"
4. Upgrade the Invoker
5. You are now ready to connect with the database and perform operations

## Support & Resources

- **Extension Version**: 1.0.0
- **PostgreSQL JDBC Driver**: 42.4.0
- **Java Version**: 21
- **Database**: PostgreSQL 9.0+

For detailed information about each catalog request, please refer to the individual documentation pages linked above.

