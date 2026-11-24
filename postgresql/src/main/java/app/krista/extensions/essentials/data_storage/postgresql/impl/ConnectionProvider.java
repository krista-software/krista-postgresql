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

package app.krista.extensions.essentials.data_storage.postgresql.impl;

import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public final class ConnectionProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionProvider.class);

    private final PostgreSQLAttributes attributes;

    @Inject
    public ConnectionProvider(PostgreSQLAttributes attributes) {
        this.attributes = attributes;
    }

    public Connection createConnection() {
        String url = createUrl();
        try {
            LOGGER.debug("Creating PostgreSQL connection to URL '{}'.", url);
            return DriverManager.getConnection(url, attributes.getDbUserName(), attributes.getDbPassword());
        } catch (SQLException | RuntimeException cause) {
            LOGGER.error("Unable to establish PostgreSQL connection to URL '{}': {}", url, cause.getMessage(), cause);
            throw new IllegalStateException(
                    "Unable to connect to the PostgreSQL database. Please verify the server address, port, database name, username, and password.",
                    cause);
        }
    }

    private String createUrl() {
        return "jdbc:postgresql://" + attributes.getHost() + ":" + attributes.getPort() + "/" + attributes.getDatabaseName();
    }
}
