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
 * Last Modified: 4/11/19 8:46 PM
 */

package io.injest.core.structs;

import java.util.List;

/**
 * Write-only parcel of different data types
 */
public interface Receptacle {

    /**
     * Add a String to the parcel
     * @param key value identifier
     * @param value String value
     */
    void putString(String key, String value);

    /**
     * Add an 32-bit integer to the parcel
     * @param key value identifier
     * @param value integer value
     */
    void putInt(String key, int value);

    /**
     * Add a 64-bit long to the parcel
     * @param key value identifier
     * @param value long value
     */
    void putLong(String key, long value);

    /**
     * Add a 64-bit floating point value to the parcel
     * @param key value identifier
     * @param value double value
     */
    void putDouble(String key, double value);

    /**
     * Add a 32-but floating point value to the parcel
     * @param key value identifier
     * @param value float value
     */
    void putFloat(String key, float value);

    /**
     * Add a boolean value to the parcel
     * @param key value identifier
     * @param value boolean value
     */
    void putBoolean(String key, boolean value);

    /**
     * Add an object of undetermined type to the parcel
     * @param key value identifier
     * @param value object value
     */
    void putObject(String key, Object value);

    /**
     * Add a collection of strings to the parcel
     * @param key value identifier
     * @param values string values
     */
    void putStringArray(String key, List<String> values);

    /**
     * Add a collection of 32-bit signed integers to the parcel
     * @param key value identifier
     * @param values integer values
     */
    void putIntArray(String key, List<Integer> values);

    /**
     * Add a collection of 64-bit signed integers to the parcel
     * @param key value identifier
     * @param values long values
     */
    void putLongArray(String key, List<Long> values);

    /**
     * Add a collection of 64-bit floating point numbers to the parcel
     * @param key value identifier
     * @param values double values
     */
    void putDoubleArray(String key, List<Double> values);

    /**
     * Add a collection of 32-bit floating point numbers to the parcel
     * @param key value identifier
     * @param values float values
     */
    void putFloatArray(String key, List<Float> values);

    /**
     * Add a collection of boolean values to the parcel
     * @param key value identifier
     * @param values boolean values
     */
    void putBooleanArray(String key, List<Boolean> values);
}
