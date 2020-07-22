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
 * Last Modified: 5/30/19 10:09 AM
 */

package io.injest.core.http;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class BufferedResponseProcessor extends TextResponseProcessor {

    private final BufferedResponseAdapter adapter;

    BufferedResponseProcessor(HandlerInstance instance) {
        super(instance);
        this.adapter = (BufferedResponseAdapter) instance.getAdapter();
        this.isBuffered = true;
    }

    @Override
    ResponseState processRequest() {
        final Handler<?> handler = handlerInstance.getHandler();
        if (handler.isBlocking())
            return super.processRequest();
        ResponseWriter writer = new ResponseWriter();
        ErrorAdapter errorAdapter = writer.createErrorAdapter(String.format(
                "Attempting to send BufferedResponse without using blocking I/O. Use the Blocking annotation on the handler [%s]",
                handler.getClass().getName()));
        adapter.replace(errorAdapter);
        return new ResponseState(
                500,
                ResponseState.RequestStatus.INVALID,
                ResponseState.AdapterStatus.REPLACED);
    }

    @Override
    ResponseBody createResponseBody(ResponseState state, String contentType) {
        if (state.requestStatus == ResponseState.RequestStatus.INVALID) {
            return super.createResponseBody(state, contentType);
        } else {
            this.sendBufferedResponse(state.statusCode, contentType);
            return new ResponseBody(false);
        }
    }

    private void sendBufferedResponse(int status, String contentType) {
        final Handler handler = handlerInstance.getHandler();

        final HttpServerExchange serverExchange = exchange.getNativeExchange();
        final HeaderMap responseHeaders = serverExchange.getResponseHeaders();

        serverExchange.setResponseContentLength(adapter.getStreamLength());
        responseHeaders.put(Headers.STATUS, status);
        responseHeaders.put(Headers.CONTENT_TYPE, contentType);

        serverExchange.startBlocking();

        final OutputStream outputStream = serverExchange.getOutputStream();
        final InputStream inputStream = adapter.getInputStream();

        try {
            byte[] buf = new byte[0x2000];
            int c;
            while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                outputStream.write(buf, 0, c);
                outputStream.flush();
            }

            try {
                inputStream.close();
                outputStream.close();
                handler.onResponseSent(exchange.getRequest(), exchange.getResponse());
            } catch (IOException e) {
                // ignore...
            }

        } catch (IOException e) {
            handler.onResponseError(exchange.getRequest(), exchange.getResponse(), e);
        }
    }
}
