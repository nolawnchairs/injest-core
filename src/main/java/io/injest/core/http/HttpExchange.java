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
 * Last Modified: 4/7/19 1:37 PM
 */

package io.injest.core.http;

import io.injest.core.structs.Bundle;
import io.injest.core.structs.Structures;
import io.undertow.server.HttpServerExchange;

final public class HttpExchange {

    private final HttpServerExchange nativeExchange;
    private final HttpRequest request;
    private final HttpResponse response;
    private final Handler handler;
    private final Bundle exchangeData = Structures.newBundle();

    HttpExchange(HttpServerExchange exchange, Handler handler) {
        this.nativeExchange = exchange;
        this.handler = handler;
        this.request = new HttpRequest(this);
        this.response = new HttpResponse(this);
    }

    protected HttpRequest getRequest() {
        return request;
    }

    protected HttpResponse getResponse() {
        return response;
    }

    protected Handler getCurrentHandler() {
        return handler;
    }

    protected Bundle getExchangeData() {
        return exchangeData;
    }

    protected HttpServerExchange getNativeExchange() {
        return nativeExchange;
    }
}
