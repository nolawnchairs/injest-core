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
 * Last Modified: 4/2/19 11:52 AM
 */

package io.injest.core.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.injest.core.util.JsonMappers;

class ResponseWriter {

    /**
     * Create an error adapter instance
     *
     * @param message String message to output
     * @param args    Additional (optional) arguments for the string formatting
     */
    ErrorAdapter createErrorAdapter(String message, Object... args) {
        ErrorAdapter adapter = new ErrorAdapter();
        adapter.setErrorMessage(args.length == 0 ? message : String.format(message, args));
        return adapter;
    }

    /**
     * Create an error adapter instance using an exception thrown by the handle method
     *
     * @param e Exception thrown
     */
    ErrorAdapter createErrorAdapter(Exception e) {
        return createErrorAdapter("Caught runtime exception: %s - %s", e.getClass().getSimpleName(), e.getMessage());
    }

    /**
     * Transform an adapter into a JSON string
     *
     * @param adapter adapter
     * @return JSON string
     */
    String getResponseJson(Adapter adapter) {
        final ObjectMapper jsonMapper = JsonMappers.restOperationDefault();
        try {
            return jsonMapper.writeValueAsString(adapter);
        } catch (JsonProcessingException e) {
            return String.format(
                    "{\"error\":\"JSON serialization error [%s]\"}",
                    adapter.getClass().getName());
        }
    }
}
