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
 * Last Modified: 7/21/20, 3:42 AM
 */

package io.injest.core.http;

import io.injest.core.annotations.handlers.ChainHandler;
import io.injest.core.structs.Receptacle;
import io.injest.core.util.Exceptions;

public abstract class ChainedHandler<R extends Adapter> extends Handler<R> {

    @Override
    @SuppressWarnings("unchecked")
    protected final int handle(HttpRequest request, R adapter) throws Exception {
        final Class<?> handlerClass = getAttachment(ChainHandler.ATTACHMENT_KEY);
        final Handler<R> handler = HandlerRegistry.getInstance().get(handlerClass);
        if (handler != null) {
            final InjectableParams params = new InjectableParams();
            final int first = this.handle(request, params);
            if (!request.isValid()) {
                return first;
            }
            request.params().inject(params);
            return handler.handle(request, adapter);
        } else {
            throw Exceptions.chainableHandlerNotAnnotated(getClass().getName());
        }
    }

    protected abstract int handle(HttpRequest request, Receptacle nextParams) throws Exception;
}
