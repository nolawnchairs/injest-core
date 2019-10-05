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
 * Last Modified: 6/24/19 4:17 PM
 */

package injest.test;

import io.injest.core.annotations.handlers.FallbackHandler;
import io.injest.core.http.ErrorAdapter;
import io.injest.core.http.Handler;
import io.injest.core.http.HttpRequest;

@FallbackHandler
public class NotFound extends Handler<ErrorAdapter> {

    @Override
    protected int handle(HttpRequest request, ErrorAdapter adapter) throws Exception {
        adapter.setErrorMessage("Invalid route: "+ request.getRequestUri());
        return 404;
    }
}
