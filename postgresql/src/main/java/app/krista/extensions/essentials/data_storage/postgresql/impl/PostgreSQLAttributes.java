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

import app.krista.extension.executor.Invoker;
import app.krista.extensions.essentials.data_storage.postgresql.util.InvokerAttributes;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

@Service
public final class PostgreSQLAttributes {

    public static final String DATABASE_NAME = "Database Name";
    public static final String USER_NAME = "User Name";
    public static final String PASSWORD = "Password";
    public static final String HOST = "Server FQDN";
    public static final String PORT = "Port";

    public static final int DEFAULT_PORT = 5432;

    private final Invoker invoker;

    @Inject
    public PostgreSQLAttributes(Invoker invoker) {
        this.invoker = invoker;
    }

    public String getDatabaseName() {
        return InvokerAttributes.getNonEmptyString(invoker, DATABASE_NAME);
    }

    public String getDbUserName() {
        return InvokerAttributes.getStringOrEmpty(invoker, USER_NAME);
    }

    public String getDbPassword() {
        return InvokerAttributes.getStringOrEmpty(invoker, PASSWORD);
    }

    public String getHost() {
        return InvokerAttributes.getNonEmptyString(invoker, HOST);
    }

    public int getPort() {
        return InvokerAttributes.getIntegerOrDefault(invoker, PORT, DEFAULT_PORT);
    }

}
