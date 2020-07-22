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

import io.injest.core.boot.ConfigKeys;
import io.injest.core.boot.StaticConfig;
import io.injest.core.util.Env;
import java.util.Arrays;

import static io.injest.core.http.ResponseState.AdapterStatus;
import static io.injest.core.http.ResponseState.RequestStatus;

class HandlerProcessor {

    private final StaticConfig staticConfig = StaticConfig.getInstance();
    protected final HttpExchange exchange;
    protected final HandlerInstance<?> handlerInstance;
    protected boolean isBuffered = false;

    HandlerProcessor(HandlerInstance<?> instance) {
        this.handlerInstance = instance;
        this.exchange = instance.getHttpExchange();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    ResponseState processRequest() {

        final Handler handler = handlerInstance.getHandler();
        final HttpRequest request = exchange.getRequest();
        final ResponseWriter writer = new ResponseWriter();
        final Adapter adapter = exchange.getResponse().getResponseAdapter();

        // if request was invalidated by a request interceptor
        if (!request.isValid()) {
            final Adapter errorAdapter = writer.createErrorAdapter(request.getRequestError());
            int statusCode = exchange.getResponse().getStatusCode();
            adapter.replace(errorAdapter);
            return new ResponseState(
                    statusCode == 0 ? 400 : statusCode,
                    RequestStatus.INVALID,
                    AdapterStatus.REPLACED);
        }

        // if request has all required parameters
        if (request.hasAllRequiredParameters()) {
            try {
                int statusCode = handler.handle(request, adapter);

                // if request was invalidated during handling
                if (!request.isValid()) {
                    final Adapter errorAdapter = writer.createErrorAdapter(request.getRequestError());
                    adapter.replace(errorAdapter);
                    return new ResponseState(
                            statusCode,
                            RequestStatus.INVALID,
                            AdapterStatus.REPLACED);
                }

                if (adapter.getReplacement() != null) {
                    final Adapter replacement = adapter.getReplacement();
                    adapter.replace(replacement);
                    return new ResponseState(
                            statusCode,
                            RequestStatus.VALID,
                            AdapterStatus.REPLACED);
                }

                if (adapter.isEjected()) {
                    return new ResponseState(
                            statusCode,
                            RequestStatus.VALID,
                            AdapterStatus.EJECTED);
                }

                // request OK and we can continue...
                return new ResponseState(statusCode, RequestStatus.VALID);
            } catch (Exception e) {
                request.invalidate();
                final ErrorAdapter errorAdapter = writer.createErrorAdapter(e);
                if (Env.isDevelopment()) {
                    if (staticConfig.getBoolean(ConfigKeys.Dev.EMBED_STACK_TRACE).orElse(true))
                        errorAdapter.setStackTrace(Arrays.asList(e.getStackTrace()));
                    if (staticConfig.getBoolean(ConfigKeys.Dev.PRINT_STACK_TRACE).orElse(false))
                        e.printStackTrace();
                }
                adapter.replace(errorAdapter);
                return new ResponseState(
                        500,
                        RequestStatus.INVALID,
                        AdapterStatus.REPLACED);
            }
        } else {
            request.invalidate();
            final Adapter errorAdapter = writer.createErrorAdapter(
                    String.format("Required parameters missing in request: %s", request.getMissingParams().toString()));
            adapter.replace(errorAdapter);
            return new ResponseState(
                    staticConfig.getInt(ConfigKeys.DEFAULT_RESPONSE_CONTENT_TYPE).orElse(422),
                    RequestStatus.INVALID,
                    AdapterStatus.REPLACED);
        }
    }

    ResponseBody createResponseBody(ResponseState state, String contentType) {
        if (state.adapterStatus == AdapterStatus.EJECTED)
            return new ResponseBody(true);

        final ResponseWriter writer = new ResponseWriter();
        final HttpResponse response = exchange.getResponse();

        // take topmost adapter from the adapter stack for serialization
        Adapter adapter = response.takeAdapter();
        return new ResponseBody(writer.getResponseJson(adapter));
    }
}
