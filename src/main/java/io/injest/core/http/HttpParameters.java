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
 * Last Modified: 5/30/19 2:56 PM
 */

package io.injest.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Enums;
import io.injest.core.annotations.directives.ParamSource;
import io.injest.core.boot.ConfigKeys;
import io.injest.core.boot.RestConfig;
import io.injest.core.structs.Parcel;
import io.injest.core.util.JsonMappers;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstraction layer for HTTP variables including path parameters,
 * store string parameters and parsed request body parameters
 */
final public class HttpParameters implements Parcel {

    private final RestConfig config = RestConfig.getInstance();
    private final ParameterWrapper parameterWrapper;
    private final ParamSource.Source source;

    HttpParameters(ParameterWrapper parameterWrapper, ParamSource.Source source) {
        this.parameterWrapper = parameterWrapper;
        this.source = source;
    }


    /**
     * Determine if a parameter key exists
     *
     * @param key key identifier
     * @return if parameter exists
     */
    public boolean has(String key) {
        return parameterWrapper.containsKey(source, key);
    }

    /**
     * Determine if a parameter exists and it's value
     * is non-empty (string length > 0 and excluding whitespace)
     * or otherwise truthy. Deque items are considered non-empty
     * if at least one element is non-empty
     *
     * @param key key of parameter
     * @return if key is valid and non-empty
     */
    public boolean hasValue(String key) {
        return has(key) && parameterWrapper.getRawValue(source, key)
                .stream().filter(value -> !value.isEmpty()).count() > 0;
    }

    /**
     * Gets raw value deque of strings from key
     *
     * @param key parameter key
     * @return String Deque
     */
    public Deque<String> getRawValue(String key) {
        return parameterWrapper.getRawValue(source, key);
    }

    /**
     * Get all raw values as a map of values
     *
     * @return String deque map of all raw values for source
     */
    public Map<String, Deque<String>> getRawValues() {
        return parameterWrapper.getRawValues(source);
    }


    /**
     * Get nullable value from parameters
     *
     * @param key   key
     * @param clazz target class
     * @param <T>   type of target
     * @return nullable value of type T
     */
    public <T> T getNullable(String key, Class<T> clazz) {
        return parameterWrapper.getNullable(source, key, clazz);
    }

    /**
     * Gets a nullable value from parameters and transforms to specified object
     * using lambda function
     *
     * @param key         key
     * @param transformFn function that transforms from string to object
     * @param <T>         type of object
     * @return nullable value of type T
     */
    public <T> T getNullable(String key, Function<String, T> transformFn) {
        Optional<String> value = parameterWrapper.get(source, key, String.class);
        return value.map(transformFn).orElse(null);
    }

    /**
     * Gets a nullable value from parameters and returns value or defaultValue if null
     *
     * @param key          key
     * @param clazz        target class
     * @param defaultValue fallback value if target is null
     * @param <T>          type for target class
     * @return non-null value of type T
     */
    public <T> T getOrDefault(String key, Class<T> clazz, T defaultValue) {
        return parameterWrapper.getOrDefault(source, key, clazz, defaultValue);
    }

    /**
     * Gets a nullable value from parameters and returns transformed value or
     * default value if null
     *
     * @param key          key
     * @param defaultValue default value of type T
     * @param transformFn  lambda that converts raw string value into target type T
     * @param <T>          type of target class
     * @return non-null value of type T
     */
    public <T> T getOrDefault(String key, T defaultValue, Function<String, T> transformFn) {
        Optional<T> value = get(key, transformFn);
        return value.orElse(defaultValue);
    }

    /**
     * Gets an Optional of type String
     *
     * @param key key
     * @return Optional String
     */
    public Optional<String> get(String key) {
        return parameterWrapper.get(source, key);
    }

    /**
     * Gets optional value from parameters
     *
     * @param key   key
     * @param clazz target class
     * @param <T>   type of target class
     * @return optional value of type T
     */
    public <T> Optional<T> get(String key, Class<T> clazz) {
        return parameterWrapper.get(source, key, clazz);
    }

    /**
     * Gets optional value from parameters and converts value to type T via lambda
     *
     * @param key         key
     * @param transformFn transformation lambda
     * @param <T>         type of target class
     * @return optional of type T
     */
    public <T> Optional<T> get(String key, Function<String, T> transformFn) {
        Optional<String> value = parameterWrapper.get(source, key, String.class);
        return value.flatMap(s -> Optional.ofNullable(transformFn.apply(s)));
    }

    /**
     * Gets an optional enum-bound value from parameters
     *
     * @param key       key
     * @param enumClass the Enum class
     * @param <T>       type of target class
     * @return optional of tpe T
     */
    public <T extends Enum<T>> Optional<T> getEnum(String key, Class<T> enumClass) {
        try {
            Optional<String> optional = this.get(key);
            if (optional.isPresent()) {
                T anEnum = Enum.valueOf(enumClass, optional.get());
                return Optional.of(anEnum);
            } else {
                return Optional.empty();
            }
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets a list of values from parameters
     *
     * @param key   key
     * @param clazz target class for list elements
     * @param <T>   type of target class
     * @return List of type T
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        return parameterWrapper.getList(source, key, clazz);
    }

    /**
     * Gets a list of values from parameters
     *
     * @param key       key
     * @param clazz     target class for list elements
     * @param keepNulls add un-castable values to list
     * @param <T>       type of target class
     * @return List of type T
     */
    public <T> List<T> getList(String key, Class<T> clazz, boolean keepNulls) {
        return parameterWrapper.getList(source, key, clazz, keepNulls);
    }

    /**
     * Gets a list of values from parameters and maps values to type T via lambda
     *
     * @param key         key
     * @param clazz       target class of list elements
     * @param transformFn lambda to transform list elements
     * @param <T>         target class type
     * @return List of type T
     */
    public <T> List<T> getList(String key, Class<T> clazz, Function<String, T> transformFn) {
        return getList(key, String.class).stream().map(transformFn).collect(Collectors.toList());
    }

    /**
     * Gets a map of key/value pairs using format foo[bar]=baz
     *
     * @param key key
     * @return Parameter map
     */
    public ParameterMap getMap(String key) {
        return parameterWrapper.getMap(source, key);
    }

    /**
     * Gets a string from params using key
     *
     * @param key value identifier
     * @return nullable string value
     */
    @Override
    public String getString(String key) {
        return parameterWrapper.get(source, key, String.class).orElse(null);
    }

    /**
     * Gets an int from params using key
     *
     * @param key key
     * @return int value or default int value if conversion failed
     */
    @Override
    public int getInt(String key) {
        return parameterWrapper.get(source, key, Integer.class)
                .orElse(config.getInt(ConfigKeys.DefaultParams.INT));
    }

    /**
     * Gets a long from params using key
     *
     * @param key key
     * @return long value or default long value if conversion failed
     */
    @Override
    public long getLong(String key) {
        return parameterWrapper.get(source, key, Long.class)
                .orElse(config.getLong(ConfigKeys.DefaultParams.LONG));
    }

    /**
     * Gets a double value from params using key
     *
     * @param key key
     * @return double value or default double value if conversion failed
     */
    @Override
    public double getDouble(String key) {
        return parameterWrapper.get(source, key, Double.class)
                .orElse(config.getDouble(ConfigKeys.DefaultParams.DOUBLE));
    }

    /**
     * Gets a float value from params using key
     *
     * @param key key
     * @return float value or default float value if conversion failed
     */
    @Override
    public float getFloat(String key) {
        return parameterWrapper.get(source, key, Float.class)
                .orElse(config.getFloat(ConfigKeys.DefaultParams.FLOAT));
    }

    /**
     * Gets a boolean value from params using key
     *
     * @param key key
     * @return boolean value or false of conversion failed
     */
    @Override
    public boolean getBoolean(String key) {
        return parameterWrapper.get(source, key, Boolean.class).orElse(false);
    }

    /**
     * Gets a truthy boolean value from params using key
     *
     * @param key key
     * @return true unless string value is explicitly "false" or "0"
     */
    public boolean getTruthyBoolean(String key) {
        return !(getString(key).equals("false") || getString(key).equals("0"));
    }

    /**
     * Get raw object value from params
     *
     * @param key key
     * @return nullable object value
     */
    @Override
    public Object getObject(String key) {
        return parameterWrapper.get(source, key, Object.class).orElse(null);
    }

    /**
     * Gets list of strings from params
     *
     * @param key value identifier
     * @return ArrayList of String
     */
    @Override
    public List<String> getStringArray(String key) {
        return parameterWrapper.getList(source, key, String.class);
    }

    /**
     * Gets list of ints from params
     *
     * @param key value identifier
     * @return ArrayList of int
     */
    @Override
    public List<Integer> getIntArray(String key) {
        return parameterWrapper.getList(source, key, Integer.class);
    }

    /**
     * Gets list of longs from params
     *
     * @param key value identifier
     * @return ArrayList of long
     */
    @Override
    public List<Long> getLongArray(String key) {
        return parameterWrapper.getList(source, key, Long.class);
    }

    /**
     * Gets list of doubles from params
     *
     * @param key value identifier
     * @return ArrayList of double
     */
    @Override
    public List<Double> getDoubleArray(String key) {
        return parameterWrapper.getList(source, key, Double.class);
    }

    /**
     * Gets list of floats from params
     *
     * @param key value identifier
     * @return ArrayList of float
     */
    @Override
    public List<Float> getFloatArray(String key) {
        return parameterWrapper.getList(source, key, Float.class);
    }

    /**
     * Gets list of booleans from params
     *
     * @param key value identifier
     * @return ArrayList of boolean
     */
    @Override
    public List<Boolean> getBooleanArray(String key) {
        return parameterWrapper.getList(source, key, Boolean.class);
    }

    /**
     * Gets a typed Object of type T from parameter containing JSON string
     *
     * @param key   key
     * @param clazz target class
     * @param <T>   type of target class
     * @return nullable object of type T
     */
    public <T> T getJsonObject(String key, Class<T> clazz) {
        final String value = getString(key);
        if (value == null)
            return null;
        final ObjectMapper mapper = JsonMappers.serializationDefault();
        try {
            return mapper.readValue(getString(key), clazz);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets a list of Objects of type T from parameter containing JSON string
     *
     * @param key   key
     * @param clazz target class fro list elements
     * @param <T>   target class type
     * @return list of objects of type T
     */
    public <T> List<T> getJsonObjectArray(String key, Class<T> clazz) {
        final String raw = getString(key);
        final ObjectMapper mapper = JsonMappers.serializationDefault();
        try {
            return mapper.readValue(raw, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static ParamSource.Source getDefaultMethodSource(RequestMethod method) {
        switch (method) {
            case POST:
                return (ParamSource.Source) RestConfig.getInstance().getObject(ConfigKeys.ParamSources.PARAM_SOURCE_POST);
            case PUT:
                return (ParamSource.Source) RestConfig.getInstance().getObject(ConfigKeys.ParamSources.PARAM_SOURCE_PUT);
            case PATCH:
                return (ParamSource.Source) RestConfig.getInstance().getObject(ConfigKeys.ParamSources.PARAM_SOURCE_PATCH);
            default:
                return ParamSource.Source.ANY;
        }
    }
}
