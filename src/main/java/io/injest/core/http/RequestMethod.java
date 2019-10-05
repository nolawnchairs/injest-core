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
 * Last Modified: 3/10/19 10:38 PM
 */

package io.injest.core.http;

import io.injest.core.annotations.method.*;
import java.lang.annotation.Annotation;

public enum RequestMethod {

    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PATCH("PATCH"),
    TRACE("TRACE"),
    CONNECT("CONNECT"),
    NONE("NONE");

    private String value;
    private static Class[] methodAnnotations = new Class[] {
            Get.class,
            Post.class,
            Put.class,
            Delete.class,
            Head.class,
            Options.class,
            Patch.class,
            Trace.class,
            Connect.class
    };
    private static RequestMethod[] requestMethods = new RequestMethod[] {
            RequestMethod.GET,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.DELETE,
            RequestMethod.HEAD,
            RequestMethod.OPTIONS,
            RequestMethod.PATCH,
            RequestMethod.TRACE,
            RequestMethod.CONNECT
    };

    RequestMethod(String method) {
        this.value = method.toUpperCase();
    }

    public String toString() {
        return value;
    }

    public boolean matches(String method) {
        return this.value.equalsIgnoreCase(method);
    }

    public static RequestMethod find(String method) {
        for (RequestMethod m : RequestMethod.values()) {
            if (m.matches(method)) return m;
        }
        return GET;
    }

    public static RequestMethod find(Class<? extends Annotation> annotation) {
        for (int i = 0; i < methodAnnotations.length; i++) {
            if (methodAnnotations[i].equals(annotation))
                return requestMethods[i];
        }
        return GET;
    }
}
