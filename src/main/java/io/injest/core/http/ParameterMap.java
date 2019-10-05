/*
 * Injest - https://injest.io
 *
 * Copyright (c) 2019.
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Last Modified: 8/14/19 10:58 PM
 */

package io.injest.core.http;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Function;

public class ParameterMap extends LinkedHashMap<String, String> {

    private HashSet<String> enforcedKeys = new HashSet<>();

    public ParameterMap requireKeys(String... keys) {
        Collections.addAll(enforcedKeys, keys);
        return this;
    }

    public boolean hasRequiredKeys() {
        for (String key : enforcedKeys)
            if (!containsKey(key))
                return false;
        return true;
    }

    public String getString(String key) {
        return get(key);
    }

    public int getInt(String key) {
        return Integer.valueOf(get(key));
    }

    public long getLong(String key) {
        return Long.valueOf(get(key));
    }

    public float getFloat(String key) {
        return Float.valueOf(get(key));
    }

    public double getDouble(String key) {
        return Double.valueOf(get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.valueOf(get(key));
    }

    public boolean getTruthyBoolean(String key) {
        return !(get(key).equals("false") || get(key).equals("0"));
    }

    public <T> Optional<T> getOptional(String key, Class<T> aClass, Function<String, T> transformer) {
        if (!containsKey(key))
            return Optional.empty();
        try {
            T value = transformer.apply(get(key));
            return Optional.of(value);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
