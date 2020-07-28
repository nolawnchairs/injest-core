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
 * Last Modified: 4/11/19 9:06 AM
 */

package injest.test;

import io.injest.core.annotations.directives.Blocking;
import io.injest.core.annotations.method.Post;
import io.injest.core.http.Handler;
import io.injest.core.http.HttpParameters;
import io.injest.core.http.HttpRequest;

@Post("/foo")
@Blocking
public class TestPostHandler extends Handler<TestAdapter> {

    @Override
    protected int handle(HttpRequest request, TestAdapter adapter) throws Exception {

        HttpParameters params = request.body();
        String bar = params.getString("bar");
        String baz = request.query().getString("baz");

        adapter.setData(new String[]{bar, baz});
        return 200;
    }
}
