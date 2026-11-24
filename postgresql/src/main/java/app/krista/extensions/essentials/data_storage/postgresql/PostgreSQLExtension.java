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

package app.krista.extensions.essentials.data_storage.postgresql;

import app.krista.extension.impl.anno.Extension;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.InvokerRequest;
import app.krista.extension.impl.anno.Java;
import app.krista.extension.impl.anno.StaticResource;
import app.krista.extensions.essentials.data_storage.postgresql.impl.ConnectionProvider;
import app.krista.extensions.essentials.data_storage.postgresql.impl.PostgreSQLAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Field.Text(PostgreSQLAttributes.HOST)
@Field(name = PostgreSQLAttributes.PORT, type = "Number")
@Field.Text(PostgreSQLAttributes.DATABASE_NAME)
@Field.Text(PostgreSQLAttributes.USER_NAME)
@Field.Text(value = PostgreSQLAttributes.PASSWORD, isSecured = true)
@Java(version = Java.Version.JAVA_21)
@Extension(version = "2.0.2", name = "PostGreSQL")
@StaticResource(path = "docs", file = "docs")
public final class PostgreSQLExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSQLExtension.class);

    private final ConnectionProvider connectionProvider;

    @Inject
    public PostgreSQLExtension(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> getCustomTabs() {
        return Map.of("Documentation", "static/docs");
    }

    @InvokerRequest(InvokerRequest.Type.VALIDATE_ATTRIBUTES)
    public void validateAttributes(Map<String, Object> attributes) {
        String host = (String) attributes.get(PostgreSQLAttributes.HOST);
        try {
            InetAddress.getByName(host);
            LOGGER.debug("Validated PostgreSQL host '{}'.", host);
        } catch (UnknownHostException cause) {
            LOGGER.error("Invalid PostgreSQL host '{}': {}", host, cause.getMessage());
            throw new IllegalArgumentException(
                    String.format("The server FQDN '%s' could not be resolved. Please verify the hostname and try again.", host),
                    cause);
        }
    }

    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() {
        LOGGER.debug("Testing PostgreSQL database connection.");
        try (Connection connection = connectionProvider.createConnection()) {
            if (!connection.isValid(5)) {
                LOGGER.error("PostgreSQL connection test failed: connection is not valid.");
                throw new SQLException("Connection is not valid.");
            }
            LOGGER.info("PostgreSQL connection test succeeded.");
        } catch (SQLException cause) {
            LOGGER.error("Unable to connect to the PostgreSQL database: {}", cause.getMessage(), cause);
            throw new IllegalStateException(
                    "Unable to connect to the PostgreSQL database. Please verify the host, port, database name, username, and password.",
                    cause);
        }
    }

}
