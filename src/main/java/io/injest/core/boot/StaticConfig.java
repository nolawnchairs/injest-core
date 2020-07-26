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
 * Last Modified: 7/21/20, 7:35 PM
 */

package io.injest.core.boot;

import io.injest.core.Exceptions;
import io.injest.core.annotations.directives.ConfigValue;
import io.injest.core.structs.BinaryTuple;
import io.injest.core.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Optional;

final public class StaticConfig {

    private static final StaticConfig INSTANCE = new StaticConfig();
    private static final HashMap<String, BinaryTuple<Class<?>, Object>> VALUES = new HashMap<>();
    private static final Log LOG = Log.with(StaticConfig.class);

    public static StaticConfig getInstance() {
        return INSTANCE;
    }

    public boolean has(String key) {
        return VALUES.containsKey(key);
    }

    /**
     * Assign value to rest configuration key
     * @param field annotated field
     */
    void assignValueFromField(Field field) {
        final String configKey = field.getAnnotation(ConfigValue.class).value();

        field.setAccessible(true);

        if ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
            throw Exceptions.configFieldNotStatic(field.getName());

        try {
            Object value = field.get(null);
            Class<?> type = field.getType();
            putValue(configKey, type, value);
        } catch (IllegalAccessException
                | ClassCastException e) {
            e.printStackTrace();
            LOG.e("Reflection failed scanning configuration at" + field.getName());
        }
    }

    private void putValue(String configKey, Class<?> type, Object value) {
        VALUES.put(configKey, new BinaryTuple<>(type, value));
    }

    public Optional<String> getString(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(String.class))
            return Optional.ofNullable((String) holder.getRight());
        return Optional.empty();
    }

    public Optional<Integer> getInt(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Integer.class))
            return Optional.ofNullable((Integer) holder.getRight());
        return Optional.empty();
    }

    public Optional<Long> getLong(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Long.class))
            return Optional.ofNullable((Long) holder.getRight());
        return Optional.empty();
    }

    public Optional<Float> getFloat(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Float.class))
            return Optional.ofNullable((Float) holder.getRight());
        return Optional.empty();
    }

    public Optional<Double> getDouble(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Double.class))
            return Optional.ofNullable((Double) holder.getRight());
        return Optional.empty();
    }

    public Optional<Boolean> getBoolean(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Boolean.class))
            return Optional.ofNullable((Boolean) holder.getRight());
        return Optional.empty();
    }

    public Optional<String[]> getStringArray(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(String[].class))
            return Optional.ofNullable((String[]) holder.getRight());
        return Optional.empty();
    }

    public Optional<Integer[]> getIntArray(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Integer[].class))
            return Optional.ofNullable((Integer[]) holder.getRight());
        return Optional.empty();
    }

    public Optional<Long[]> getLongArray(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Long[].class))
            return Optional.ofNullable((Long[]) holder.getRight());
        return Optional.empty();
    }

    public Optional<Float[]> getFloatArray(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Float[].class))
            return Optional.ofNullable((Float[]) holder.getRight());
        return Optional.empty();
    }

    public Optional<Double[]> getDoubleArray(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Double[].class))
            return Optional.ofNullable((Double[]) holder.getRight());
        return Optional.empty();
    }

    public Optional<Boolean[]> getBooleanArray(String key) {
        BinaryTuple<Class<?>, Object> holder = VALUES.get(key);
        if (holder != null && holder.getLeft().equals(Boolean[].class))
            return Optional.ofNullable((Boolean[]) holder.getRight());
        return Optional.empty();
    }
}
