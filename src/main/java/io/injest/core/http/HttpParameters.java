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
 * Last Modified: 7/21/20, 7:34 PM
 */

package io.injest.core.http;

import io.injest.core.boot.StaticConfig;
import io.injest.core.structs.Parcel;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Abstraction layer for HTTP variables including path parameters,
 * store string parameters and parsed request body parameters
 */
final public class HttpParameters implements Parcel {

    private final StaticConfig config = StaticConfig.getInstance();
    private final ParameterWrapper parameterWrapper;
    private final ParameterSource source;

    HttpParameters(ParameterWrapper parameterWrapper, ParameterSource source) {
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
                .stream().anyMatch(value -> !value.isEmpty());
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
     * @param <T>         type of target value
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
     * @param <T>          type for target value
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
     * @param <T>          type of target value
     * @return non-null value of type T
     */
    public <T> T getOrDefault(String key, T defaultValue, Function<String, T> transformFn) {
        Optional<T> value = get(key, transformFn);
        return value.orElse(defaultValue);
    }

    /**
     * Gets a nullable value from parameters and returns it if it exists, otherwise
     * it is retrieved via the defaultGetter function provided
     *
     * @param key      key
     * @param clazz    target class
     * @param defaultGetter defaultGetter to get value in case of absence
     * @param <T>      type of target value
     * @return non-null value of type T
     */
    public <T> T getOrDefault(String key, Class<T> clazz, Supplier<T> defaultGetter) {
        Optional<T> value = parameterWrapper.get(source, key, clazz);
        return value.orElseGet(defaultGetter);
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
     * @param enumClass the target enum class
     * @param <T>       type of target class
     * @return optional of tpe T
     */
    public <T extends Enum<T>> Optional<T> getEnum(String key, Class<T> enumClass) {
        try {
            Optional<String> optional = this.get(key);
            if (optional.isPresent()) {
                T anEnum = Enum.valueOf(enumClass, optional.get());
                return Optional.of(anEnum);
            }
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets an enum-bound value from parameters, or the default of the value is not found or invalid
     *
     * @param key          key
     * @param enumClass    the target enum class
     * @param defaultValue the default value if the value is not found
     * @param <T>          type of target class
     * @return enum of type T
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, T defaultValue) {
        return getEnum(key, enumClass).orElse(defaultValue);
    }

    /**
     * Gets an enum-bound value from parameters, or the default of the value is not found or invalid
     *
     * @param key           key
     * @param enumClass     the target enum class
     * @param defaultGetter supplier to invoke if the value is not found
     * @param <T>           type of target class
     * @return enum of type T
     */
    public <T extends Enum<T>> T getEnum(String key, Class<T> enumClass, Supplier<T> defaultGetter) {
        return getEnum(key, enumClass).orElseGet(defaultGetter);
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
                .orElse(Integer.MIN_VALUE);
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
                .orElse(Long.MIN_VALUE);
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
                .orElse(Double.NaN);
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
                .orElse(Float.NaN);
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
        if (!has(key))
            return false;
        return !(getString(key).toLowerCase().equals("false") || getString(key).equals("0"));
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
     * Manually add pseudo-parameters into the parameter mappings
     *
     * @param params Parcel of values to add
     */
    void inject(InjectableParams params) {
        parameterWrapper.injectParams(params);
    }

    /**
     * Gets the default source of parameters based on the request method
     *
     * @param method the request method
     * @return the parameter source
     */
    public static ParameterSource getDefaultMethodSource(RequestMethod method) {
        switch (method) {
            case POST:
            case PATCH:
            case PUT:
                return ParameterSource.BODY;
            default:
                return ParameterSource.ANY;
        }
    }
}
