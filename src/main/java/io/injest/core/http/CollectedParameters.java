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
 * Last Modified: 7/22/20, 12:53 AM
 */

package io.injest.core.http;

import io.injest.core.annotations.directives.ParamSource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class CollectedParameters {

    private final TreeMap<String, Mappings> mappings = new TreeMap<>();

    CollectedParameters(ParameterSet... parameterSets) {
        for (ParameterSet set : parameterSets) {
            if (set != null) {
                Set<String> keySet = set.keySet();
                for (String key : keySet) {
                    Mappings m = mappings.getOrDefault(key, new Mappings());
                    m.add(set.getSource(), set.getValues(key));
                    mappings.put(key, m);
                }
            }
        }
    }

    /**
     * Determine if a parameter with key exists in any of the collected
     * parameter collections. Any parameters injected via chained handlers
     * will overwrite those found in the path, query or body sources
     *
     * @param key    the key
     * @param source the source
     * @return true if it exists
     */
    boolean containsKey(String key, ParamSource.Source source) {
        return mappings.containsKey(key) &&
                (source == ParamSource.Source.ANY
                        || mappings.get(key).keyExistsForSource(ParamSource.Source.INJECTED)
                        || mappings.get(key).keyExistsForSource(source));
    }

    /**
     * Gets all collected values from the given parameter source. Parameters
     * that were injected via chained handlers will overwrite those of other
     * sources with the same key
     *
     * @param key    the key
     * @param source the source
     * @return an array deque of the collected values
     */
    Deque<String> getCollectedValues(String key, ParamSource.Source source) {
        if (source == ParamSource.Source.ANY)
            return mappings.get(key).ofSource();
        Deque<String> injectedValues = mappings.get(key)
                .ofSource(ParamSource.Source.INJECTED);
        if (injectedValues != null)
            return injectedValues;
        return mappings.get(key).ofSource(source);
    }

    /**
     * Gets all collected values as a single array deque from all sources except
     * injected values
     *
     * @return an array deque of all collected values
     */
    Map<String, Deque<String>> getCollectedValues() {
        Map<String, Deque<String>> output = new TreeMap<>();
        for (String key : mappings.keySet()) {
            Mappings m = mappings.get(key);
            Deque<String> values = new ArrayDeque<>();
            for (Deque<String> v : m.data.values())
                values.addAll(v);
            output.put(key, values);
        }
        return output;
    }

    /**
     * Holder class for all parameter mappings
     */
    private static class Mappings {

        /**
         *
         */
        TreeMap<ParamSource.Source, Deque<String>> data = new TreeMap<>();

        /**
         * Adds a set of values to the given source
         *
         * @param source the parameter source
         * @param values the collection of values
         */
        void add(ParamSource.Source source, Deque<String> values) {
            data.put(source, values);
        }

        /**
         * Determines if parameters exist in a source for a given key
         *
         * @param source the parameter source
         * @return true if the parameter exists
         */
        boolean keyExistsForSource(ParamSource.Source source) {
            return data.containsKey(source);
        }

        /**
         * Gets values from a parameter source
         *
         * @param source the parameter source
         * @return the collection of values
         */
        Deque<String> ofSource(ParamSource.Source source) {
            if (keyExistsForSource(source))
                return data.get(source);
            return null;
        }

        /**
         * Gets values from all parameter sources
         *
         * @return the collection of values
         */
        Deque<String> ofSource() {
            Deque<String> values = new ArrayDeque<>();
            for (ParamSource.Source source : data.keySet())
                values.addAll(data.get(source));
            return values;
        }
    }
}
