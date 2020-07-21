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
 * Last Modified: 5/29/19 6:02 AM
 */

package io.injest.core.http;

import io.injest.core.annotations.directives.ParamSource;
import io.injest.core.structs.BinaryTuple;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ParameterSet {

    private final Map<String, Deque<String>> params;
    private final Set<String> keySet;
    private final ParamSource.Source source;

    ParameterSet(Map<String, Deque<String>> params, ParamSource.Source source) {
        this.params = normalizeParameters(params);
        this.keySet = this.params.keySet();
        this.source = source;
    }

    /**
     * Normalizes key values by removing tailing [] and adding appropriate
     * values into the String Deque
     *
     * @param params parameter map
     * @return normalized HashMap
     */
    private Map<String, Deque<String>> normalizeParameters(Map<String, Deque<String>> params) {
        Map<String, Deque<String>> normalizedKeyMap = new HashMap<>();
        for (String key : params.keySet()) {
            BinaryTuple<String, Deque<String>> tuple = normalizeEntry(key, params.get(key));
            if (normalizedKeyMap.containsKey(tuple.getLeft())) {
                Deque<String> existingValues = normalizedKeyMap.get(tuple.getLeft());
                existingValues.addAll(tuple.getRight());
                normalizedKeyMap.put(tuple.getLeft(), existingValues);
            } else {
                normalizedKeyMap.put(tuple.getLeft(), tuple.getRight());
            }
        }
        return normalizedKeyMap;
    }

    ParamSource.Source getSource() {
        return source;
    }

    Set<String> keySet() {
        return keySet;
    }

    Deque<String> getValues(String key) {
        return params.get(key);
    }

    Map<String, Deque<String>> toDequeMap() {
        return params;
    }

    private static final String ARRAY_KEYED = "^(.*)\\[(.*?)\\]$";

    /**
     * Normalize each map entry
     *
     * @param key   native key
     * @param value native value deque
     * @return BinaryTuple of normalized key and derived value
     */
    private BinaryTuple<String, Deque<String>> normalizeEntry(String key, Deque<String> value) {
        if (key.endsWith("[]")) {
            return new BinaryTuple<>(key.replace("[]", ""), value);
        } else if (key.matches(ARRAY_KEYED)) {
            Pattern pattern = Pattern.compile(ARRAY_KEYED);
            Matcher matcher = pattern.matcher(key);
            if (matcher.find() && matcher.groupCount() == 2) {
                Deque<String> inner = new LinkedList<>();
                inner.add(matcher.group(2));
                inner.add(value.getFirst());
                return new BinaryTuple<>(matcher.group(1), inner);
            }
        }
        return new BinaryTuple<>(key, value);
    }
}
