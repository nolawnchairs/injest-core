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
 * Last Modified: 6/27/19 4:36 PM
 */

package io.injest.core.boot;

import io.injest.core.util.Log;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

public class RestApplication {

    private Undertow server;
    private final InjestApplication application;
    private static final Log LOG = Log.with(RestApplication.class);
    private static final long TICKER = System.currentTimeMillis();

    RestApplication(InjestApplication application) {
        this.application = application;
    }

    void start(int port, String host, HttpHandler rootHandler) {
        this.server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(rootHandler)
                .build();

        LOG.i("Starting HTTP server");
        server.start();

        LOG.i("HTTP server started on port "+ port);
        LOG.i(String.format("Application started in %d ms", System.currentTimeMillis() - TICKER));

        application.onApplicationStarted();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        ApplicationState.setState(ApplicationState.State.RUNNING);
    }

    private void stop() {
        application.onApplicationShutdown();
        server.stop();
        LOG.i("HTTP server stopped");
    }

    /**
     * Create new instance of RestApplicationOptions
     * @return new instance of Options
     */
    public static RestApplicationOptions create() {
        return new RestApplicationOptions();
    }

    /**
     * Get application uptime in milliseconds
     * @return uptime in milliseconds
     */
    public static long uptime() {
        return System.currentTimeMillis() - TICKER;
    }
}
