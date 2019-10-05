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

import io.injest.core.annotations.directives.ParamSource;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

class ConflatedParameters {

    private TreeMap<String, Mappings> mappings = new TreeMap<>();

    ConflatedParameters(ParameterSet... parameterSets) {
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

    boolean containsKey(String key, ParamSource.Source source) {
        return mappings.containsKey(key) &&
                (source == ParamSource.Source.ANY || mappings.get(key).keyExistsForSource(source));
    }

    Deque<String> getCollectedValues(String key, ParamSource.Source source) {
        if (containsKey(key, source)) {
            if (source == ParamSource.Source.ANY)
                return mappings.get(key).get();
            return mappings.get(key).get(source);
        }
        return new ArrayDeque<>();
    }

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

    private static class Mappings {

        TreeMap<ParamSource.Source, Deque<String>> data = new TreeMap<>();

        void add(ParamSource.Source source, Deque<String> values) {
            data.put(source, values);
        }

        boolean keyExistsForSource(ParamSource.Source source) {
            return data.containsKey(source);
        }

        Deque<String> get(ParamSource.Source source) {
            if (keyExistsForSource(source))
                return data.get(source);
            return new ArrayDeque<>();
        }

        Deque<String> get() {
            Deque<String> values = new ArrayDeque<>();
            for (ParamSource.Source source : data.keySet())
                values.addAll(data.get(source));
            return values;
        }
    }
}
