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
 * Last Modified: 10/23/19 11:48 AM
 */

package io.injest.security.cors;

import io.injest.core.http.HttpRequest;
import io.injest.core.http.HttpResponse;
import io.injest.core.util.Wildcard;
import java.util.Set;

class CorsConfigurator {

    private final CorsOptions options;
    private final HttpRequest request;
    private final HttpResponse response;

    public CorsConfigurator(CorsOptions options, HttpRequest request, HttpResponse response) {
        this.options = options;
        this.request = request;
        this.response = response;
    }

    boolean checkOrigin(String origin) {
        if (origin == null)
            return false;
        final Set<String> allowedOrigins = options.origins;
        response.putHeader("Vary", "Origin");

        if (allowedOrigins.contains("*")) {
            response.putHeader("Access-Control-Allow-Origin", "*");
            return true;
        }
        if (allowedOrigins.contains(origin)) {
            response.putHeader("Access-Control-Allow-Origin", origin);
            return true;
        }

        for (String allowed: allowedOrigins) {
            if (Wildcard.equalsOrMatch(origin, allowed)) {
                response.putHeader("Access-Control-Allow-Origin", origin);
                return true;
            }
        }
        return false;
    }

    void setGlobalHeaders() {
        if (options.exposedHeaders.size() > 0) {
            String exposedHeaders = String.join(",", options.exposedHeaders);
            response.putHeader("Access-Control-Expose-Headers", exposedHeaders);
        }
        if (options.credentials)
            response.putHeader("Access-Control-Allow-Credentials", "true");
    }

    void setOptionsHeaders() {
        response.putHeader("Access-Control-Allow-Methods", String.join(",", options.methods));

        String allowedHeaders;
        if (options.allowedHeaders.size() == 0) {
            allowedHeaders = request.getHeader("access-control-request-allowedHeaders");
            response.putHeader("Vary", "Access-Control-Request-Headers");
        } else {
            allowedHeaders = String.join(",", options.allowedHeaders) + ",content-type,origin,accept";
        }
        if (!"".equals(allowedHeaders)) {
            response.putHeader("Access-Control-Allow-Headers", allowedHeaders);
        }

        if (options.maxAge > -1) {
            response.putHeader("Access-Control-Max-Age", String.valueOf(options.maxAge));
        }
    }
}
