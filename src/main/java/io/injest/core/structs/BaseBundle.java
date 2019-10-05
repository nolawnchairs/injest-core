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
 * Last Modified: 4/5/19 4:26 PM
 */

package io.injest.core.structs;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BaseBundle implements Bundle {

    private HashMap<String, BinaryTuple<Class, Integer>> registry = new HashMap<>();
    private HashMap<String, LinkedList<String>> stringStore = new HashMap<>();
    private HashMap<String, LinkedList<Integer>> intStore = new HashMap<>();
    private HashMap<String, LinkedList<Long>> longStore = new HashMap<>();
    private HashMap<String, LinkedList<Float>> floatStore = new HashMap<>();
    private HashMap<String, LinkedList<Double>> doubleStore = new HashMap<>();
    private HashMap<String, LinkedList<Boolean>> boolStore = new HashMap<>();
    private HashMap<String, Object> objectStore = new HashMap<>();

    @Override
    public boolean has(String key) {
        return registry.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return registry.keySet();
    }

    @Override
    public Class getValueType(String key) {
        return registry.get(key).getLeft();
    }

    @Override
    public String getString(String key) {
        if (has(key) && stringStore.size() > 0)
            return stringStore.get(key).getFirst();
        return null;
    }

    @Override
    public int getInt(String key) {
        if (has(key) && intStore.size() > 0)
            return intStore.get(key).getFirst();
        return 0;
    }

    @Override
    public long getLong(String key) {
        if (has(key) && longStore.size() > 0)
            return longStore.get(key).getFirst();
        return 0L;
    }

    @Override
    public double getDouble(String key) {
        if (has(key) && doubleStore.size() > 0)
            return doubleStore.get(key).getFirst();
        return 0d;
    }

    @Override
    public float getFloat(String key) {
        if (has(key) && floatStore.size() > 0)
            return floatStore.get(key).getFirst();
        return 0;
    }

    @Override
    public boolean getBoolean(String key) {
        if (has(key) && boolStore.size() > 0)
            return boolStore.get(key).getFirst();
        return false;
    }

    @Override
    public Object getObject(String key) {
        if (has(key))
            return objectStore.get(key);
        return null;
    }

    @Override
    public List<String> getStringArray(String key) {
        return stringStore.getOrDefault(key, new LinkedList<>());
    }

    @Override
    public List<Integer> getIntArray(String key) {
        return intStore.getOrDefault(key, new LinkedList<>());
    }

    @Override
    public List<Long> getLongArray(String key) {
        return longStore.getOrDefault(key, new LinkedList<>());
    }

    @Override
    public List<Double> getDoubleArray(String key) {
        return doubleStore.getOrDefault(key, new LinkedList<>());
    }

    @Override
    public List<Float> getFloatArray(String key) {
        return floatStore.getOrDefault(key, new LinkedList<>());
    }

    @Override
    public List<Boolean> getBooleanArray(String key) {
        return boolStore.getOrDefault(key, new LinkedList<>());
    }

    @Override
    public void putString(String key, String value) {
        registry.put(key, new BinaryTuple<>(String.class, 1));
        stringStore.put(key, create(value));
    }

    @Override
    public void putInt(String key, int value) {
        registry.put(key, new BinaryTuple<>(Integer.class, 1));
        intStore.put(key, create(value));
    }

    @Override
    public void putLong(String key, long value) {
        registry.put(key, new BinaryTuple<>(Long.class, 1));
        longStore.put(key, create(value));
    }

    @Override
    public void putDouble(String key, double value) {
        registry.put(key, new BinaryTuple<>(Double.class, 1));
        doubleStore.put(key, create(value));
    }

    @Override
    public void putFloat(String key, float value) {
        registry.put(key, new BinaryTuple<>(Float.class, 1));
        floatStore.put(key, create(value));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        registry.put(key, new BinaryTuple<>(Boolean.class, 1));
        boolStore.put(key, create(value));
    }

    @Override
    public void putObject(String key, Object value) {
        registry.put(key, new BinaryTuple<>(Object.class, 1));
        objectStore.put(key, value);
    }

    @Override
    public void putStringArray(String key, List<String> values) {
        registry.put(key, new BinaryTuple<>(String.class, values.size()));
        stringStore.put(key, create(values));

    }

    @Override
    public void putIntArray(String key, List<Integer> values) {
        registry.put(key, new BinaryTuple<>(Integer.class, values.size()));
        intStore.put(key, create(values));
    }

    @Override
    public void putLongArray(String key, List<Long> values) {
        registry.put(key, new BinaryTuple<>(Long.class, values.size()));
        longStore.put(key, create(values));
    }

    @Override
    public void putDoubleArray(String key, List<Double> values) {
        registry.put(key, new BinaryTuple<>(Double.class, values.size()));
        doubleStore.put(key, create(values));
    }

    @Override
    public void putFloatArray(String key, List<Float> values) {
        registry.put(key, new BinaryTuple<>(Float.class, values.size()));
        floatStore.put(key, create(values));
    }

    @Override
    public void putBooleanArray(String key, List<Boolean> values) {
        registry.put(key, new BinaryTuple<>(Boolean.class, values.size()));
        boolStore.put(key, create(values));
    }

    private <E> LinkedList<E> create(E value) {
        LinkedList<E> thing = new LinkedList<>();
        thing.add(value);
        return thing;
    }

    private <E> LinkedList<E> create(Collection<E> values) {
        return new LinkedList<>(values);
    }
}
