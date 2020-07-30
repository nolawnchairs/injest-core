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

import java.util.Map;
import java.util.TreeMap;

public final class Interceptors {

    private static final Interceptors INSTANCE = new Interceptors();
    private final TreeMap<Integer, Interceptor> requestInterceptors = new TreeMap<>();
    private final TreeMap<Integer, Interceptor> responseInterceptors = new TreeMap<>();
    private final TreeMap<Integer, Interceptor> endingInterceptors = new TreeMap<>();

    public static void addRequestInterceptor(Interceptor interceptor, int invocationPriority) {
        INSTANCE.requestInterceptors.put(invocationPriority, interceptor);
    }

    public static void addResponseInterceptor(Interceptor interceptor, int invocationPriority) {
        INSTANCE.responseInterceptors.put(invocationPriority, interceptor);
    }

    public static void addEndingInterceptor(Interceptor interceptor, int invocationPriority) {
        INSTANCE.endingInterceptors.put(invocationPriority, interceptor);
    }

    static void invokeRequestInterceptors(HttpRequest request, HttpResponse response) {
        for (Map.Entry<Integer, Interceptor> entry : INSTANCE.requestInterceptors.entrySet()) {
            if (request.canIntercept()) {
                entry.getValue().intercept(request, response);
            }
        }
    }

    static void invokeResponseInterceptors(HttpRequest request, HttpResponse response) {
        for (Map.Entry<Integer, Interceptor> entry : INSTANCE.responseInterceptors.entrySet()) {
            if (response.canIntercept()) {
                entry.getValue().intercept(request, response);
            }
        }
    }

    static void invokeEndingInterceptors(HttpRequest request, HttpResponse response) {
        for (Map.Entry<Integer, Interceptor> entry : INSTANCE.endingInterceptors.entrySet()) {
            if (response.canIntercept()) {
                entry.getValue().intercept(request, response);
            }
        }
    }

    private Interceptors() {
    }
}
