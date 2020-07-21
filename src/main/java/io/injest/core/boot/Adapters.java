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
 * Last Modified: 3/10/19 11:31 PM
 */

package io.injest.core.boot;

import io.injest.core.http.ErrorAdapter;
import java.util.HashMap;

public class Adapters {

    public static final int INVALID_REQUEST = 0;
    public static final int MISSING_PARAMS = 1;
    public static final int EXCEPTION_CAUGHT = 2;

    static final Adapters INSTANCE = new Adapters();
    private final HashMap<Integer, ErrorAdapter> errorAdapters = new HashMap<>();

    private Adapters() {
        errorAdapters.put(INVALID_REQUEST, new ErrorAdapter());
        errorAdapters.put(MISSING_PARAMS, new ErrorAdapter());
        errorAdapters.put(EXCEPTION_CAUGHT, new ErrorAdapter());
    }

    void putErrorAdapter(int kind, ErrorAdapter error) {
        errorAdapters.put(kind, error);
    }

    public static ErrorAdapter invalidRequest() {
         return INSTANCE.errorAdapters.get(INVALID_REQUEST);
    }

    public static ErrorAdapter missingParameters() {
        return INSTANCE.errorAdapters.get(MISSING_PARAMS);
    }

    public static ErrorAdapter caughtException() {
        return INSTANCE.errorAdapters.get(EXCEPTION_CAUGHT);
    }
}
