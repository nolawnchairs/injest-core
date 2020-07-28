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
 * Last Modified: 5/30/19 3:03 PM
 */

package io.injest.core.http;

import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.injest.core.util.ObjectUtils.castPrimitive;

public class ParameterWrapper {

    private final ParameterSet pathParams;
    private final ParameterSet queryParams;
    private ParameterSet bodyParams;
    private CollectedParameters collected;

    ParameterWrapper(Map<String, Deque<String>> pathParams,
                     Map<String, Deque<String>> queryParams,
                     ParameterSource paramSource) {
        this(pathParams, queryParams, null, paramSource);
    }

    ParameterWrapper(Map<String, Deque<String>> pathParams,
                     Map<String, Deque<String>> queryParams,
                     Map<String, Deque<String>> bodyParams,
                     ParameterSource paramSource) {
        this.pathParams = new ParameterSet(pathParams, ParameterSource.PATH);
        this.queryParams = new ParameterSet(queryParams, ParameterSource.QUERY);

        if (bodyParams != null && (paramSource == null || paramSource == ParameterSource.BODY))
            this.bodyParams = new ParameterSet(bodyParams, ParameterSource.BODY);
    }

    /**
     * Merge all three parameter sources into one
     */
    void collect() {
        this.collected = new CollectedParameters(pathParams, queryParams, bodyParams);
    }

    /**
     * Determine if conflated parameters contains a key
     *
     * @param source parameter source
     * @param key    key
     * @return true if the key exists
     */
    boolean containsKey(ParameterSource source, String key) {
        return collected.containsKey(key, source);
    }

    /**
     * Obtain untyped, raw Deque of String values via key
     *
     * @param source parameter source
     * @param key    key
     * @return untyped String Deque
     */
    Deque<String> getRawValue(ParameterSource source, String key) {
        return collected.getCollectedValues(key, source);
    }

    /**
     * Obtain typed, nullable value
     *
     * @param source parameter source
     * @param key    key
     * @param clazz  target type class
     * @param <T>    target type
     * @return nullable value of type T
     */
    <T> T getNullable(ParameterSource source, String key, Class<T> clazz) {
        return get(source, key, clazz).orElse(null);
    }

    /**
     * Obtain typed, non-null value using default fallback value
     *
     * @param source       parameter source
     * @param key          key
     * @param clazz        target type class
     * @param defaultValue fallback value
     * @param <T>          target type
     * @return typed value or fallback
     */
    <T> T getOrDefault(ParameterSource source, String key, Class<T> clazz, T defaultValue) {
        return get(source, key, clazz).orElse(defaultValue);
    }

    /**
     * Obtain typed Optional value
     *
     * @param source parameter source
     * @param key    key
     * @param clazz  target type class
     * @param <T>    target type
     * @return Optional of type T
     */
    <T> Optional<T> get(ParameterSource source, String key, Class<T> clazz) {
        if (!collected.containsKey(key, source))
            return Optional.empty();
        String rawValue = collected.getCollectedValues(key, source).getFirst();
        T typedValue = castPrimitive(rawValue, clazz);
        return Optional.ofNullable(typedValue);
    }

    /**
     * Obtain optional parameter as string value
     *
     * @param source parameter source
     * @param key    key
     * @return Optional String
     */
    Optional<String> get(ParameterSource source, String key) {
        if (!collected.containsKey(key, source))
            return Optional.empty();
        String value = collected.getCollectedValues(key, source).getFirst();
        return Optional.ofNullable(value);
    }

    /**
     * Obtain typed list of values
     *
     * @param source parameter source
     * @param key    key
     * @param clazz  target type class for list elements
     * @param <T>    target type
     * @return value list of type T
     */
    <T> List<T> getList(ParameterSource source, String key, Class<T> clazz) {
        return getList(source, key, clazz, false);
    }

    /**
     * Obtain typed list of values
     *
     * @param source    parameter source
     * @param key       key
     * @param clazz     target type class for list elements
     * @param keepNulls add un-castable values to output list
     * @param <T>       target type
     * @return value list of type T
     */
    <T> List<T> getList(ParameterSource source, String key, Class<T> clazz, boolean keepNulls) {
        if (!collected.containsKey(key, source))
            return Collections.emptyList();

        LinkedList<T> typedValues = new LinkedList<>();
        for (String value : collected.getCollectedValues(key, source)) {
            T typedValue = castPrimitive(value, clazz);
            if (typedValue != null || keepNulls) {
                typedValues.add(typedValue);
            }
        }
        return typedValues;
    }

    /**
     * Obtain ParameterMap of key/value pairs
     *
     * @param source parameter source
     * @param key    parameter key
     * @return new ParameterMap
     */
    ParameterMap getMap(ParameterSource source, String key) {
        ParameterMap map = new ParameterMap();
        if (!collected.containsKey(key, source))
            return map;
        Iterator<String> iterator = collected.getCollectedValues(key, source).iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (iterator.hasNext())
                map.put(k, iterator.next());
        }
        return map;
    }

    /**
     * Get all parameters together
     *
     * @param source parameter source
     * @return Map of String deque containing parameter data
     */
    Map<String, Deque<String>> getRawValues(ParameterSource source) {
        switch (source) {
            case QUERY:
                return queryParams == null ? null : queryParams.toDequeMap();
            case BODY:
                return bodyParams == null ? null : bodyParams.toDequeMap();
            case PATH:
                return pathParams == null ? null : pathParams.toDequeMap();
        }
        return collected.getCollectedValues();
    }

    /**
     * Injects pseudo-parameters into the request for chaining handlers
     *
     * @param params the parameters to inject
     */
    void injectParams(InjectableParams params) {
        ParameterSet injectedParams = new ParameterSet(params.getMappings(), ParameterSource.INJECTED);
        this.collected = new CollectedParameters(pathParams, queryParams, bodyParams, injectedParams);
    }
}
