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
 * Last Modified: 3/11/19 5:54 PM
 */

package io.injest.core.http;

import io.injest.core.boot.ConfigKeys;
import io.injest.core.boot.RestConfig;

final public class ContentType {
    public static final String JSON = "application/json";

    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTML = "text/html";
    public static final String TEST_XML = "com/enaturelive/rest/xml";
    public static final String TEXT_CSS = "text/css";
    public static final String TEXT_CSV = "text/csv";

    public static final String OCTET_STREAM = "application/octet-stream";

    public static final String IMAGE_JPEG = "image/jpeg";
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_GIF = "image/gif";
    public static final String IMAGE_ICON = "image/x-icon";
    public static final String IMAGE_SVG = "image/svg+xml";
    public static final String IMAGE_WEBP = "image/webp";

    public static String getDefault() {
        final RestConfig config = RestConfig.getInstance();
        return config.getString(ConfigKeys.DEFAULT_RESPONSE_CONTENT_TYPE);
    }
}