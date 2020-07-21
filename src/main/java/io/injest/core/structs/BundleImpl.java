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
 * Last Modified: 4/13/19 10:23 AM
 */

package io.injest.core.structs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BundleImpl implements Bundle {

    private final HashMap<String, Item> store = new HashMap<>();

    @Override
    public void putString(String key, String value) {
        store.put(key, new Item(String.class, value));
    }

    @Override
    public void putInt(String key, int value) {
        store.put(key, new Item(Integer.class, value));
    }

    @Override
    public void putLong(String key, long value) {
        store.put(key, new Item(Long.class, value));
    }

    @Override
    public void putDouble(String key, double value) {
        store.put(key, new Item(Double.class, value));
    }

    @Override
    public void putFloat(String key, float value) {
        store.put(key, new Item(Float.class, value));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        store.put(key, new Item(Boolean.class, value));
    }

    @Override
    public void putObject(String key, Object value) {
        store.put(key, new Item(Object.class, value));
    }

    @Override
    public void putStringArray(String key, List<String> values) {
        store.put(key, Item.list(String.class, values));
    }

    @Override
    public void putIntArray(String key, List<Integer> values) {
        store.put(key, Item.list(Integer.class, values));
    }

    @Override
    public void putLongArray(String key, List<Long> values) {
        store.put(key, Item.list(Long.class, values));
    }

    @Override
    public void putDoubleArray(String key, List<Double> values) {
        store.put(key, Item.list(Double.class, values));
    }

    @Override
    public void putFloatArray(String key, List<Float> values) {
        store.put(key, Item.list(Float.class, values));
    }

    @Override
    public void putBooleanArray(String key, List<Boolean> values) {
        store.put(key, Item.list(Boolean.class, values));
    }

    @Override
    public String getString(String key) {
        return getItem(String.class, key);
    }

    @Override
    public int getInt(String key) {
        if (!store.containsKey(key))
            return 0;
        return getItem(Integer.class, key);
    }

    @Override
    public long getLong(String key) {
        if (!store.containsKey(key))
            return 0;
        return getItem(Long.class, key);
    }

    @Override
    public double getDouble(String key) {
        if (!store.containsKey(key))
            return 0;
        return getItem(Double.class, key);
    }

    @Override
    public float getFloat(String key) {
        if (!store.containsKey(key))
            return 0;
        return getItem(Float.class, key);
    }

    @Override
    public boolean getBoolean(String key) {
        if (!store.containsKey(key))
            return false;
        return getItem(Boolean.class, key);
    }

    @Override
    public Object getObject(String key) {
        return store.get(key).values.getFirst();
    }

    @Override
    public List<String> getStringArray(String key) {
        return getList(String.class, key);
    }

    @Override
    public List<Integer> getIntArray(String key) {
        return getList(Integer.class, key);
    }

    @Override
    public List<Long> getLongArray(String key) {
        return getList(Long.class, key);
    }

    @Override
    public List<Double> getDoubleArray(String key) {
        return getList(Double.class, key);
    }

    @Override
    public List<Float> getFloatArray(String key) {
        return getList(Float.class, key);
    }

    @Override
    public List<Boolean> getBooleanArray(String key) {
        return getList(Boolean.class, key);
    }

    @Override
    public boolean has(String key) {
        return store.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return store.keySet();
    }

    @Override
    public Class<?> getValueType(String key) {
        return store.get(key).valueType;
    }

    private <T> T getItem(Class<T> clazz, String key) {
        return store.get(key).get(clazz);
    }

    private <T> List<T> getList(Class<T> clazz, String key) {
        return this.store.get(key).values.stream().filter(Objects::nonNull).map(clazz::cast)
                .collect(Collectors.toList());
    }


    @Override
    public String toString() {
        int i = 0;
        Set<String> keys = keySet();
        Iterator<String> iterator = keys.iterator();
        String[] sa = new String[keys.size()];
        while (iterator.hasNext()) {
            String key = iterator.next();
            sa[i++] = key + "=" + this.store.get(key).values.toString();
        }
        return String.join(", ", sa);
    }

    private static class Item {

        Class<?> valueType;
        LinkedList<Object> values = new LinkedList<>();

        static <T> Item list(Class<?> valueType, Iterable<T> iterable) {
            return new Item(valueType, iterable);
        }

        Item(Class<?> valueType, Object value) {
            this.valueType = valueType;
            this.values.addFirst(value);
        }

        Item(Class<?> valueType, Iterable<?> value) {
            this.valueType = valueType;
            this.values.addFirst(value);
        }

        @SuppressWarnings("unchecked")
        <T> T get(Class<?> clazz) {
            return (T) clazz.cast(this.values.getFirst());
        }
    }
}
