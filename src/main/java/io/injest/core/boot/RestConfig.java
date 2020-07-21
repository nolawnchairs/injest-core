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
 * Last Modified: 6/1/19 3:44 PM
 */

package io.injest.core.boot;

import io.injest.core.InjestMessages;
import io.injest.core.annotations.directives.ConfigValue;
import io.injest.core.annotations.directives.DeferredConfig;
import io.injest.core.structs.BinaryTuple;
import io.injest.core.structs.Receptacle;
import io.injest.core.util.Exceptions;
import io.injest.core.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

final public class RestConfig implements Receptacle {

    private static final RestConfig INSTANCE = new RestConfig();
    private static final HashSet<String> DEFERRED_VALUES = new HashSet<>();
    private static final HashMap<String, BinaryTuple<Class<?>, Object>> VALUES = new HashMap<>();
    private static final Log LOG = Log.with(RestConfig.class);

    public static RestConfig getInstance() {
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

        if (field.isAnnotationPresent(DeferredConfig.class)) {
            LOG.i(String.format("Deferring configuration value [%s]", configKey));
            DEFERRED_VALUES.add(configKey);
            return;
        }

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

    void checkFulfilledDeferrals() {
        final HashSet<String> unfulfilledValues = new HashSet<>();
        for (String key : DEFERRED_VALUES)
            if (!VALUES.containsKey(key))
                unfulfilledValues.add(key);
        if (unfulfilledValues.size() > 0)
            InjestMessages.unfulfilledDeferredConfigValues(unfulfilledValues).toWarningLog(this);
        DEFERRED_VALUES.clear();
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

    @Override
    public void putString(String key, String value) {
        putValue(key, String.class, value);
    }

    @Override
    public void putInt(String key, int value) {
        putValue(key, Integer.class, value);
    }

    @Override
    public void putLong(String key, long value) {
        putValue(key, Long.class, value);
    }

    @Override
    public void putDouble(String key, double value) {
        putValue(key, Double.class, value);
    }

    @Override
    public void putFloat(String key, float value) {
        putValue(key, Float.class, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        putValue(key, Boolean.class, value);
    }

    @Override
    public void putObject(String key, Object value) {
        putValue(key, Object.class, value);
    }

    @Override
    public void putStringArray(String key, List<String> values) {
        putValue(key, String[].class, values.toArray());
    }

    public void putStringArray(String key, String[] values) {
        putValue(key, String[].class, values);
    }

    @Override
    public void putIntArray(String key, List<Integer> values) {
        putValue(key, Integer[].class, values.toArray());
    }

    public void putIntArray(String key, Integer[] values) {
        putValue(key, Integer[].class, values);
    }

    @Override
    public void putLongArray(String key, List<Long> values) {
        putValue(key, Long[].class, values.toArray());
    }

    public void putLongArray(String key, Long[] values) {
        putValue(key, Long[].class, values);
    }

    @Override
    public void putDoubleArray(String key, List<Double> values) {
        putValue(key, Double[].class, values.toArray());
    }

    public void putDoubleArray(String key, Double[] values) {
        putValue(key, Double[].class, values);
    }

    @Override
    public void putFloatArray(String key, List<Float> values) {
        putValue(key, Float[].class, values.toArray());
    }

    public void putFloatArray(String key, Float[] values) {
        putValue(key, Float[].class, values);
    }

    @Override
    public void putBooleanArray(String key, List<Boolean> values) {
        putValue(key, Boolean[].class, values.toArray());
    }

    public void putBooleanArray(String key, Boolean[] values) {
        putValue(key, Boolean[].class, values);
    }
}
