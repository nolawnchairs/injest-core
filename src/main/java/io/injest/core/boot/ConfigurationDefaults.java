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
 * Last Modified: 5/22/19 4:47 AM
 */

package io.injest.core.boot;

import io.injest.core.annotations.directives.ParamSource;
import io.injest.core.http.ContentType;
import io.injest.core.structs.BinaryTuple;
import java.util.HashMap;

class ConfigurationDefaults {

    private static final HashMap<String, BinaryTuple<Class, Object>> DEFAULTS = new HashMap<>();

    static {
        DEFAULTS.put(ConfigKeys.RESPONSE_CHARSET, new BinaryTuple<>(String.class, "utf-8"));
        DEFAULTS.put(ConfigKeys.ENABLE_GZIP, new BinaryTuple<>(boolean.class, false));
        DEFAULTS.put(ConfigKeys.DEFAULT_RESPONSE_CONTENT_TYPE, new BinaryTuple<>(String.class, ContentType.JSON));
        DEFAULTS.put(ConfigKeys.Json.JSON_INCLUDE_NULL_VALUES, new BinaryTuple<>(boolean.class, false));
        DEFAULTS.put(ConfigKeys.Json.JSON_INDENT_OUTPUT, new BinaryTuple<>(boolean.class, false));
        DEFAULTS.put(ConfigKeys.DefaultParams.INT, new BinaryTuple<>(int.class, Integer.MIN_VALUE));
        DEFAULTS.put(ConfigKeys.DefaultParams.LONG, new BinaryTuple<>(long.class, Long.MIN_VALUE));
        DEFAULTS.put(ConfigKeys.DefaultParams.FLOAT, new BinaryTuple<>(float.class, Float.NaN));
        DEFAULTS.put(ConfigKeys.DefaultParams.DOUBLE, new BinaryTuple<>(double.class, Double.NaN));
        DEFAULTS.put(ConfigKeys.Dev.EMBED_STACK_TRACE, new BinaryTuple<>(boolean.class, true));
        DEFAULTS.put(ConfigKeys.Dev.PRINT_STACK_TRACE, new BinaryTuple<>(boolean.class, false));
        DEFAULTS.put(ConfigKeys.ParamSources.PARAM_SOURCE_PATCH, new BinaryTuple<>(ParamSource.Source.class, ParamSource.Source.BODY));
        DEFAULTS.put(ConfigKeys.ParamSources.PARAM_SOURCE_POST, new BinaryTuple<>(ParamSource.Source.class, ParamSource.Source.BODY));
        DEFAULTS.put(ConfigKeys.ParamSources.PARAM_SOURCE_PUT, new BinaryTuple<>(ParamSource.Source.class, ParamSource.Source.BODY));
    }

    static void assignDefaults() {
        RestConfig config = RestConfig.getInstance();
        DEFAULTS.forEach(config::putValue);
    }
}
