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

package io.injest.security.cors;

import io.injest.core.http.HttpRequest;
import io.injest.core.http.HttpResponse;
import io.injest.core.http.Interceptor;
import io.injest.core.http.RequestMethod;
import io.injest.core.util.Log;

public abstract class CorsInterceptor implements Interceptor {

    private final CorsOptions options = CorsOptions.INSTANCE;
    private static final Log LOG = Log.with(CorsInterceptor.class);

    protected CorsInterceptor() {
        LOG.i("Configuring CORS Interceptor...");
        options.enabled = true;
        this.configure(options);
        LOG.i("CORS Configured: "+ options.toString());
    }

    @Override
    public void intercept(HttpRequest request, HttpResponse response) {
        final CorsConfigurator configurator = new CorsConfigurator(options, request, response);
        if (options.enabled && shouldEnforce(request)) {
            String origin = request.getHeader("origin");
            configurator.setGlobalHeaders();
            if (request.getRequestMethod() == RequestMethod.OPTIONS)
                configurator.setOptionsHeaders();
            if (!configurator.checkOrigin(origin))
                this.onCorsViolation(origin, request, response);
        }
    }

    protected abstract void configure(CorsOptions options);
    protected abstract void onCorsViolation(String origin, HttpRequest request, HttpResponse response);

    protected boolean shouldEnforce(HttpRequest request) {
        return true;
    }
}
