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
import io.injest.core.util.Exceptions;
import io.injest.core.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

final public class RestConfig  {

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

    void putValue(String configKey, BinaryTuple<Class<?>, Object> tuple) throws ClassCastException {
        putValue(configKey, tuple.getLeft(), tuple.getRight());
    }



    private void putValue(String configKey, Class<?> type, Object value) throws ClassCastException {
        VALUES.put(configKey, new BinaryTuple<>(type, value));

//        if (type == int.class)
//            putInt(configKey, (int) value);
//        else if (type == long.class)
//            putLong(configKey, (long) value);
//        else if (type == double.class)
//            putDouble(configKey, (double) value);
//        else if (type == float.class)
//            putFloat(configKey, (float) value);
//        else if (type == boolean.class)
//            putBoolean(configKey, (boolean) value);
//        else if (type == String.class)
//            putString(configKey, (String) value);
//        else if (type == String[].class)
//            putStringArray(configKey, Arrays.asList((String[]) value));
//        else if (type == int[].class)
//            putIntArray(configKey, Arrays.asList((Integer[]) value));
//        else if (type == long[].class)
//            putLongArray(configKey, Arrays.asList((Long[]) value));
//        else if (type == double[].class)
//            putDoubleArray(configKey, Arrays.asList((Double[]) value));
//        else if (type == float[].class)
//            putFloatArray(configKey, Arrays.asList((Float[]) value));
//        else if (type.isEnum())
//            putObject(configKey, value);
//        else InjestMessages.invalidConfigValueType(configKey, type).toErrorLog(this);
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
