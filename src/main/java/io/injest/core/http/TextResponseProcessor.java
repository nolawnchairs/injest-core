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
 * Last Modified: 3/11/19 5:06 AM
 */

package io.injest.core.http;

import io.injest.core.boot.ConfigKeys;
import io.injest.core.boot.RestConfig;
import io.injest.core.res.ResourceValues;
import io.injest.core.util.Env;
import java.util.List;

class TextResponseProcessor extends HandlerProcessor {

    private final TextResponseAdapter adapter;

    TextResponseProcessor(HandlerInstance instance) {
        super(instance);
        this.adapter = (TextResponseAdapter) instance.getAdapter();
    }

    @Override
    ResponseBody createResponseBody(ResponseState state, String contentType) {
        if (state.requestStatus == ResponseState.RequestStatus.INVALID) {
            if (state.adapterStatus == ResponseState.AdapterStatus.REPLACED) {
                Adapter replacement = adapter.getReplacement();
                if (replacement != null && replacement instanceof ErrorAdapter) {
                    String errorMessage = ((ErrorAdapter) replacement).getErrorMessage();
                    if (Env.isDevelopment()) {
                        if (!RestConfig.getInstance().getBoolean(ConfigKeys.Dev.EMBED_STACK_TRACE))
                            return new ResponseBody(errorMessage);
                        List<String> stackTrace = ((ErrorAdapter) replacement).getStackTrace();
                        return new ResponseBody(String.format(
                                "%s\n\nStack Trace:\n%s",
                                errorMessage,
                                String.join("\n\t", stackTrace)));
                    } else {
                        return new ResponseBody(errorMessage);
                    }
                } else {
                    return new ResponseBody(String.format(
                            ResourceValues.getErrorMessage("replacementAdapterMissingSignature"),
                            adapter.getClass().getName()));
                }
            } else {
                return new ResponseBody(true);
            }
        } else {
            return new ResponseBody(adapter.getResponseBody());
        }
    }
}
