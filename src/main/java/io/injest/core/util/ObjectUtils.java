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
 * Last Modified: 6/25/19 9:38 PM
 */

package io.injest.core.util;

import org.reflections.Reflections;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ObjectUtils {

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Object createInstanceOf(String className) {
        return createInstanceOf(getClass(className));
    }

    public static Object createInstanceOf(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (IllegalAccessException
                | InstantiationException
                | InvocationTargetException
                | NoSuchMethodException e) {
            return null;
        }
    }

    public static Object createInstanceOf(Class<?> clazz, Object... initArgs) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return constructor.newInstance(initArgs);
        } catch (IllegalAccessException
                | InstantiationException
                | InvocationTargetException
                | NoSuchMethodException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T castPrimitive(String value, Class<T> clazz) {
        try {
            if (clazz.equals(String.class))
                return (T) value;
            if (clazz.equals(Integer.class))
                return (T) (Integer) Integer.parseInt(value);
            if (clazz.equals(Long.class))
                return (T) (Long) Long.parseLong(value);
            if (clazz.equals(Float.class))
                return (T) (Float) Float.parseFloat(value);
            if (clazz.equals(Double.class))
                return (T) (Double) Double.parseDouble(value);
            if (clazz.equals(Boolean.class))
                return (T) Boolean.valueOf(value);
            if (clazz.equals(Object.class))
                return (T) value;
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
