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
 * Last Modified: 7/21/20, 7:19 PM
 */

package io.injest.security.csrf;

import io.injest.core.http.HttpRequest;
import io.injest.core.http.HttpResponse;
import io.undertow.server.session.Session;
import io.undertow.util.Sessions;

public abstract class CsrfSessionInterceptor extends CsrfInterceptor {

    private final CsrfOptions options = CsrfOptions.INSTANCE;

    @Override
    protected void setSessionValue(String token, HttpRequest request, HttpResponse response) {
        final Session session = getSession(request);
        session.setAttribute(options.cookieName, token);
    }

    @Override
    protected String getHashedValue(HttpRequest request) {
        return (String) getSession(request).getAttribute(options.cookieName);
    }

    protected Session getSession(HttpRequest request) {
        return Sessions.getOrCreateSession(request.getExchange());
    }
}
