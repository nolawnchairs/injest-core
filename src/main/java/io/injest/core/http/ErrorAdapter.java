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
 * Last Modified: 5/30/19 10:09 AM
 */

package io.injest.core.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class ErrorAdapter extends Adapter {

    @JsonProperty("message")
    private String errorMessage = "";

    @JsonProperty("stackTrace")
    private List<String> stackTrace;

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    void setStackTrace(List<StackTraceElement> traceElements) {
        this.stackTrace = new ArrayList<>();
        for (StackTraceElement e : traceElements)
            this.stackTrace.add(e.toString());
    }

    String getErrorMessage() {
        return errorMessage;
    }

    List<String> getStackTrace() {
        return stackTrace;
    }

    @Override
    public void clear() {
        super.clear();
        errorMessage = "";
        stackTrace = null;
    }
}
