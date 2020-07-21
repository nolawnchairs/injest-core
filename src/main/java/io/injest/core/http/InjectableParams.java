/*
 * Injest - https://injest.io
 *
 * Copyright (c) 2020.
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
 * Last Modified: 7/21/20, 4:52 AM
 */

package io.injest.core.http;

import io.injest.core.structs.Receptacle;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InjectableParams implements Receptacle {

    private final Map<String, Deque<String>> mappings = new HashMap<>();

    Map<String, Deque<String>> getMappings() {
        return mappings;
    }

    @Override
    public void putString(String key, String value) {
        Deque<String> values = new LinkedList<>();
        values.add(value);
        mappings.put(key, values);
    }

    @Override
    public void putInt(String key, int value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putLong(String key, long value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putDouble(String key, double value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putFloat(String key, float value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putObject(String key, Object value) {
        putString(key, String.valueOf(value));
    }

    @Override
    public void putStringArray(String key, List<String> values) {
        mappings.put(key, new LinkedList<>(values));
    }

    @Override
    public void putIntArray(String key, List<Integer> values) {
        mappings.put(key, values.stream().map(String::valueOf).collect(Collectors.toCollection(LinkedList::new)));
    }

    @Override
    public void putLongArray(String key, List<Long> values) {
        mappings.put(key, values.stream().map(String::valueOf).collect(Collectors.toCollection(LinkedList::new)));
    }

    @Override
    public void putDoubleArray(String key, List<Double> values) {
        mappings.put(key, values.stream().map(String::valueOf).collect(Collectors.toCollection(LinkedList::new)));
    }

    @Override
    public void putFloatArray(String key, List<Float> values) {
        mappings.put(key, values.stream().map(String::valueOf).collect(Collectors.toCollection(LinkedList::new)));
    }

    @Override
    public void putBooleanArray(String key, List<Boolean> values) {
        mappings.put(key, values.stream().map(String::valueOf).collect(Collectors.toCollection(LinkedList::new)));
    }
}
