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
 * Read-only parcel of multiple data types
 */
public interface Parcel {

    /**
     * Get a string from the parcel
     * @param key value identifier
     * @return string value or null if non-existent
     */
    String getString(String key);

    /**
     * Get a 32-bit signed integer from the parcel
     * @param key value identifier
     * @return int value or 0 if not found
     */
    int getInt(String key);

    /**
     * Get a 64-bit signed integer from the parcel
     * @param key value identifier
     * @return long value or 0 if not found
     */
    long getLong(String key);

    /**
     * Get a 64-bit floating point number from the parcel
     * @param key value identifier
     * @return double value or 0 if not found
     */
    double getDouble(String key);

    /**
     * Get a 32-bit floating point number from the parcel
     * @param key value identifier
     * @return float value or 0 if not found
     */
    float getFloat(String key);

    /**
     * Get a boolean value from the parcel
     * @param key value identifier
     * @return boolean value or false if not found
     */
    boolean getBoolean(String key);

    /**
     * Get an Object of undetermined type from the parcel
     * @param key value identifier
     * @return object value or null if not found
     */
    Object getObject(String key);

    /**
     * Get a List of string values from the parcel
     * @param key value identifier
     * @return List of Strings, or empty list if not found
     */
    List<String> getStringArray(String key);

    /**
     * Get a List of integer values from the parcel
     * @param key value identifier
     * @return List of Integers, or empty list if not found
     */
    List<Integer> getIntArray(String key);

    /**
     * Get a List of Long values from the parcel
     * @param key value identifier
     * @return List of Longs, or empty list if not found
     */
    List<Long> getLongArray(String key);

    /**
     * Get a List of Double values from the parcel
     * @param key value identifier
     * @return List of Doubles, or empty list if not found
     */
    List<Double> getDoubleArray(String key);

    /**
     * Get a List of Float values from the parcel
     * @param key value identifier
     * @return List of Floats, or empty list if not found
     */
    List<Float> getFloatArray(String key);

    /**
     * Get a List of Boolean values from the parcel
     * @param key value identifier
     * @return List of Booleans, or empty list if not found
     */
    List<Boolean> getBooleanArray(String key);

    /**
     * Determine of value exists via key
     * @param key value identifier
     * @return true if present, otherwise false
     */
    boolean has(String key);
}
