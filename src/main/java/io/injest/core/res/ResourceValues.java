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

package io.injest.core.res;

import java.util.HashMap;
import java.util.Map;

public class ResourceValues {

    private static final ResourceValues INSTANCE = new ResourceValues();
    private final Map<String, String> exceptionMessages = new HashMap<>();
    private final Map<String, String> errorMessages = new HashMap<>();
    private ResourceValues() {}

    public static ResourceValues getInstance() {
        return INSTANCE;
    }

    void addExceptionMessages(Map<String, String> values) {
        exceptionMessages.putAll(values);
    }

    void addErrorMessages(Map<String, String> values) {
        errorMessages.putAll(values);
    }

    public static String getExceptionMessage(String key) {
        return INSTANCE.exceptionMessages.get(key);
    }

    public static String getErrorMessage(String key) {
        return INSTANCE.errorMessages.get(key);
    }
}
