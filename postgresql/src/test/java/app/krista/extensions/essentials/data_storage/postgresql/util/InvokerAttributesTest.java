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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class InvokerAttributesTest {

    private Invoker newInvokerWithAttributes(Map<String, Object> attributes) {
        Invoker invoker = Mockito.mock(Invoker.class);
        Mockito.when(invoker.getAttributes()).thenReturn(attributes);
        return invoker;
    }

    @Test
    public void testGetStringOrDefaultUsesDefaultWhenBlank() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("name", "");
        Invoker invoker = newInvokerWithAttributes(attributes);

        String result = InvokerAttributes.getStringOrDefault(invoker, "name", "default-name");

        Assert.assertEquals("default-name", result);
    }

    @Test
    public void testGetStringOrEmptyReturnsEmptyWhenMissing() {
        Map<String, Object> attributes = new HashMap<>();
        Invoker invoker = newInvokerWithAttributes(attributes);

        String result = InvokerAttributes.getStringOrEmpty(invoker, "missing");

        Assert.assertEquals("", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNonEmptyStringThrowsForBlank() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("required", "   ");
        Invoker invoker = newInvokerWithAttributes(attributes);

        InvokerAttributes.getNonEmptyString(invoker, "required");
    }

    @Test
    public void testDoubleHelpers() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("pi", 3.14d);
        Invoker invoker = newInvokerWithAttributes(attributes);

        Assert.assertEquals(Double.valueOf(3.14d), InvokerAttributes.getDoubleOrNull(invoker, "pi"));
        Assert.assertEquals(3.14d, InvokerAttributes.getNonNullDouble(invoker, "pi"), 0.0d);
        Assert.assertEquals(Double.valueOf(1.23d),
                InvokerAttributes.getDoubleOrDefault(invoker, "missing", 1.23d));
    }

    @Test
    public void testIntegerHelpers() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("age", 30.0d);
        Invoker invoker = newInvokerWithAttributes(attributes);

        Assert.assertEquals(Integer.valueOf(30), InvokerAttributes.getIntegerOrNull(invoker, "age"));
        Assert.assertEquals(30, InvokerAttributes.getNonNullInteger(invoker, "age"));
        Assert.assertEquals(Integer.valueOf(42),
                InvokerAttributes.getIntegerOrDefault(invoker, "missing", 42));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIntegerHelpersThrowWhenTooLarge() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("big", (double) Integer.MAX_VALUE + 1.0d);
        Invoker invoker = newInvokerWithAttributes(attributes);

        InvokerAttributes.getIntegerOrNull(invoker, "big");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnsureTypeThrowsOnWrongType() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key", 123);
        Invoker invoker = newInvokerWithAttributes(attributes);

        InvokerAttributes.getStringOrNull(invoker, "key");
    }
}

