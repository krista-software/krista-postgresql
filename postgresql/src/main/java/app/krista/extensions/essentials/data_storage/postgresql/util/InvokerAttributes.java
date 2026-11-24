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

package app.krista.extensions.essentials.data_storage.postgresql.util;

import app.krista.extension.executor.Invoker;

public final class InvokerAttributes {

    private static final double EPSILON = 1.0e-10;
    private static final double DOUBLE_ROUNDING_OFFSET = 0.5;

    private InvokerAttributes() {
    }

    public static String getStringOrDefault(Invoker invoker, String attributeName, String defaultValue) {
        String value = getStringOrEmpty(invoker, attributeName);
        return value.isBlank() ? defaultValue : value;
    }

    public static String getStringOrNull(Invoker invoker, String attributeName) {
        return ensureType(invoker.getAttributes().get(attributeName), String.class);
    }


    public static String getStringOrEmpty(Invoker invoker, String attributeName) {
        String value = getStringOrNull(invoker, attributeName);
        return value == null ? "" : value;
    }

    public static String getNonEmptyString(Invoker invoker, String attributeName) {
        String value = getStringOrEmpty(invoker, attributeName);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Required attribute '" + attributeName + "' is missing or empty.");
        }
        return value;
    }

    public static Double getDoubleOrDefault(Invoker invoker, String attributeName, Double defaultValue) {
        Double value = getDoubleOrNull(invoker, attributeName);
        return value == null ? defaultValue : value;
    }

    public static Double getDoubleOrNull(Invoker invoker, String attributeName) {
        return ensureType(invoker.getAttributes().get(attributeName), Double.class);
    }

    public static double getNonNullDouble(Invoker invoker, String attributeName) {
        Double value = getDoubleOrNull(invoker, attributeName);
        if (value == null) {
            throw new IllegalArgumentException("Required numeric attribute '" + attributeName + "' is missing.");
        }
        return value;
    }

    public static Integer getIntegerOrDefault(Invoker invoker, String attributeName, Integer defaultValue) {
        Integer value = getIntegerOrNull(invoker, attributeName);
        return value == null ? defaultValue : value;
    }

    public static Integer getIntegerOrNull(Invoker invoker, String attributeName) {
        Double value = getDoubleOrNull(invoker, attributeName);
        if (value == null) {
            return null;
        }
        if (value + DOUBLE_ROUNDING_OFFSET > Integer.MAX_VALUE - EPSILON) {
            throw new IllegalArgumentException("Numeric attribute '" + attributeName + "' is too large to be converted to an integer.");
        }
        return (int) (value + DOUBLE_ROUNDING_OFFSET);
    }

    public static int getNonNullInteger(Invoker invoker, String attributeName) {
        Integer value = getIntegerOrNull(invoker, attributeName);
        if (value == null) {
            throw new IllegalArgumentException("Required integer attribute '" + attributeName + "' is missing.");
        }
        return value;
    }

    private static <T> T ensureType(Object value, Class<T> typeClass) {
        if (value != null && !typeClass.isInstance(value)) {
            throw new IllegalArgumentException(
                    "Attribute value '" + value + "' is not of expected type '" + typeClass.getSimpleName() + "'.");
        }
        return typeClass.cast(value);
    }

}
