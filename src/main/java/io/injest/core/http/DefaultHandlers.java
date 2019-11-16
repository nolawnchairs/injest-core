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
 * Last Modified: 4/14/19 2:27 PM
 */

package io.injest.core.http;

import io.injest.core.annotations.directives.Produces;
import io.injest.security.cors.Cors;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public final class DefaultHandlers {

    @Produces(ContentType.TEXT_PLAIN)
    public static class DefaultFallbackHandler implements HttpHandler {
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            exchange.setStatusCode(404);
            exchange.getResponseHeaders()
                    .put(Headers.CONTENT_TYPE, ContentType.TEXT_PLAIN);
            exchange.getResponseSender()
                    .send(String.format("Could not %s %s",
                            exchange.getRequestMethod(),
                            exchange.getRequestURI()
                    ));
        }
    }


    @Produces(ContentType.TEXT_PLAIN)
    public static class DefaultOptionsHandler extends Handler<BareResponse> {
        @Override
        protected int handle(HttpRequest request, BareResponse adapter) throws Exception {
            request.getResponse().setContentType(ContentType.TEXT_PLAIN);
            request.getResponse().putHeader(Headers.CONTENT_LENGTH, "0");
            request.getResponse().setStatusCode(Cors.getOptionsInstance().getOptionsSuccessStatus());
            return 204;
        }
    }

}
