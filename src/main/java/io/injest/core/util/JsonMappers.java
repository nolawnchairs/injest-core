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
 * Last Modified: 3/30/19 6:47 AM
 */

package io.injest.core.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.injest.core.boot.ApplicationState;
import io.injest.core.boot.ConfigKeys;
import io.injest.core.boot.RestConfig;
import java.util.HashMap;
import java.util.function.Consumer;

public final class JsonMappers {

    private static final JsonMappers INSTANCE = new JsonMappers();
    private final HashMap<Integer, ObjectMapper> defaultMappers = new HashMap<>();

    public static final int DEFAULT_REST_OPERATION = 1;
    public static final int DEFAULT_SERIALIZATION = 2;

    private JsonMappers() {
        defaultMappers.put(DEFAULT_REST_OPERATION, new RestOperationDefault());
        defaultMappers.put(DEFAULT_SERIALIZATION, new SerializationDefault());
    }

    public static void configureDefaultMapper(int mapper, Consumer<ObjectMapper> mapperConsumer) {
        if (ApplicationState.getState() == ApplicationState.State.RUNNING)
            throw new IllegalStateException("Default ObjectMappers must be configured during boot. Move this consumer to a Bootable instance");
        mapperConsumer.accept(INSTANCE.defaultMappers.get(mapper));
    }

    public static ObjectMapper restOperationDefault() {
        return INSTANCE.defaultMappers.get(DEFAULT_REST_OPERATION);
    }

    public static ObjectMapper serializationDefault() {
        return INSTANCE.defaultMappers.get(DEFAULT_SERIALIZATION);
    }

    public static ObjectMapper newSerializationDefault() {
        return new SerializationDefault();
    }

    private static class SerializationDefault extends ObjectMapper {
        SerializationDefault() {
            this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            this.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            this.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
            this.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        }
    }

    private static class RestOperationDefault extends SerializationDefault {
        RestOperationDefault() {
            RestConfig restConfig = RestConfig.getInstance();
            if (!restConfig.getBoolean(ConfigKeys.Json.JSON_INCLUDE_NULL_VALUES))
                this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            if (restConfig.getBoolean(ConfigKeys.Json.JSON_INDENT_OUTPUT))
                this.configure(SerializationFeature.INDENT_OUTPUT, true);
        }
    }
}
