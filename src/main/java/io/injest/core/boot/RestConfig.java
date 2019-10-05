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
import io.injest.core.structs.BundleImpl;
import io.injest.core.util.Exceptions;
import io.injest.core.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;

final public class RestConfig extends BundleImpl {

    private static RestConfig instance = new RestConfig();
    private static Log log = Log.with(RestConfig.class);

    private HashSet<String> deferredValues = new HashSet<>();

    public static RestConfig getInstance() {
        return instance;
    }

    /**
     * Assign value to rest configuration key
     * @param field annotated field
     */
    void assignValueFromField(Field field) {
        final String configKey = field.getAnnotation(ConfigValue.class).value();

        if (field.isAnnotationPresent(DeferredConfig.class)) {
            log.i(String.format("Deferring configuration value [%s]", configKey));
            deferredValues.add(configKey);
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
            log.e("Reflection failed scanning configuration at" + field.getName());
        }
    }

    void checkFulfilledDeferrals() {
        final HashSet<String> unfulfilledValues = new HashSet<>();
        for (String value : deferredValues)
            if (!has(value))
                unfulfilledValues.add(value);
        if (unfulfilledValues.size() > 0)
            InjestMessages.unfulfilledDeferredConfigValues(unfulfilledValues).toWarningLog(this);
        deferredValues.clear();
    }

    void putValue(String configKey, BinaryTuple<Class, Object> tuple) throws ClassCastException {
        putValue(configKey, tuple.getLeft(), tuple.getRight());
    }

    private void putValue(String configKey, Class<?> type, Object value) throws ClassCastException {
        if (type == int.class)
            putInt(configKey, (int) value);
        else if (type == long.class)
            putLong(configKey, (long) value);
        else if (type == double.class)
            putDouble(configKey, (double) value);
        else if (type == float.class)
            putFloat(configKey, (float) value);
        else if (type == boolean.class)
            putBoolean(configKey, (boolean) value);
        else if (type == String.class)
            putString(configKey, (String) value);
        else if (type == String[].class)
            putStringArray(configKey, Arrays.asList((String[]) value));
        else if (type == int[].class)
            putIntArray(configKey, Arrays.asList((Integer[]) value));
        else if (type == long[].class)
            putLongArray(configKey, Arrays.asList((Long[]) value));
        else if (type == double[].class)
            putDoubleArray(configKey, Arrays.asList((Double[]) value));
        else if (type == float[].class)
            putFloatArray(configKey, Arrays.asList((Float[]) value));
        else if (type.isEnum())
            putObject(configKey, value);
        else InjestMessages.invalidConfigValueType(configKey, type).toErrorLog(this);
    }
}
