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
 * Last Modified: 7/21/20, 7:21 PM
 */

package io.injest.core.boot;

final public class ConfigKeys {

    public static final String ENABLE_GZIP = "enableGzip";
    public static final String FALLBACK_PORT = "fallbackPort";
    public static final String RESPONSE_CHARSET = "responseCharset";
    public static final String DEFAULT_RESPONSE_CONTENT_TYPE = "defaultResponseContentType";
    public static final String REQUEST_BODY_CHARSET = "requestBodyCharset";
    public static final String MISSING_PARAMETERS_STATUS_CODE = "missingParametersStatusCode";

    public static final class Dev {
        public static final String EMBED_STACK_TRACE = "embedStackTrace";
        public static final String PRINT_STACK_TRACE = "printStackTrace";
    }

    public static final class Json {
        public static final String JSON_INCLUDE_NULL_VALUES = "jsonIncludeNullValues";
        public static final String JSON_INDENT_OUTPUT = "jsonIndentOutput";
    }

    public static final class Net {
        public static final String FORWARDED_IP_HEADER = "netForwardedIpHeader";
    }
}
