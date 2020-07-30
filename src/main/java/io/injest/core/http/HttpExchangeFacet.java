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
 * Last Modified: 5/14/19 9:12 PM
 */

package io.injest.core.http;

import io.injest.core.structs.Bundle;
import io.undertow.server.HttpServerExchange;

public interface HttpExchangeFacet {

    Handler<?> getHandler();

    /**
     * Stops interceptors from being invoked
     */
    void endIntercepts();

    /**
     * Determine if interceptors can still be invoked
     *
     * @return boolean
     */
    boolean canIntercept();

    /**
     * Get the HttpExchange data bundle
     *
     * @return Bundle of custom data for the HttpExchange
     */
    Bundle data();

    /**
     * Gets the native Undertow HttpServerExchange object
     * associated with the current request
     *
     * @return HttpServerRequest instance
     */
    HttpServerExchange getExchange();
}
