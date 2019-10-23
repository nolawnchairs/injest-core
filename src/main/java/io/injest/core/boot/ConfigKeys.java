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
 * Last Modified: 6/28/19 12:55 AM
 */

package io.injest.core.boot;

import io.injest.core.http.RequestMethod;

final public class ConfigKeys {

    public static final String ENABLE_GZIP = "enableGzip";
    public static final String FALLBACK_PORT = "fallbackPort";
    public static final String RESPONSE_CHARSET = "responseCharset";
    public static final String DEFAULT_RESPONSE_CONTENT_TYPE = "defaultResponseContentType";

    public static final class Dev {
        public static final String EMBED_STACK_TRACE = "embedStackTrace";
        public static final String PRINT_STACK_TRACE = "printStackTrace";
    }

    public static final class Json {
        public static final String JSON_INCLUDE_NULL_VALUES = "jsonIncludeNullValues";
        public static final String JSON_INDENT_OUTPUT = "jsonIndentOutput";
    }

    public static final class DefaultParams {
        public static final String INT = "defaultIntParam";
        public static final String LONG = "defaultLongParam";
        public static final String FLOAT = "defaultFloatParam";
        public static final String DOUBLE = "defaultDoubleParam";
    }

    public static final class Net {
        public static final String FORWARDED_IP_HEADER = "netForwardedIpHeader";
    }

    public static final class Csrf {
        public static final String CSRF_ENABLED = "csrfEnabled";
        public static final String CSRF_COOKIE_SECURE = "csrfCookieSecure";
        public static final String CSRF_COOKIE_LIFETIME = "csrfCookieLifetime";
        public static final String CSRF_COOKIE_NAME = "csrfCookieName";
        public static final String CSRF_REQUEST_HEADER_KEY = "csrfRequestHeaderKey";
        public static final String CSRF_RESPONSE_HEADER_KEY = "csrfResponseHeaderKey";
        public static final String CSRF_DEFERRED_METHODS = "csrfDeferredMethods";
        public static final String CSRF_SECRET = "csrfSecret";
        public static final String CSRF_COOKIE_DOMAIN = "csrfCookieDomain";
    }

    public static final class ParamSources {
        public static final String PARAM_SOURCE_POST = "paramSourcesPost";
        public static final String PARAM_SOURCE_PUT = "paramSourcesPut";
        public static final String PARAM_SOURCE_PATCH = "paramSourcesPatch";
    }
}
