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
 * Last Modified: 6/27/19 5:16 PM
 */

package io.injest.core.boot;

/**
 * This interface must be implemented during application startup.
 * It's recommended that you implement this interface in the same class
 * that houses your main() method. The implementing class must have a
 * no-arg constructor, and be set via {@link RestApplicationOptions}
 */
public interface InjestApplication {


    /**
     * Called before the bootstrapping phase has started, which
     * includes the main package scanner, but after configuration
     * has been loaded.
     */
    void onApplicationPreBootstrap();

    /**
     * Called when the bootstrapping phase has completed, which
     * includes post-scan bootables
     */
    void onApplicationPostBootstrap();

    /**
     * Called when the main REST application has started.
     * This would be the place to start up any additional services
     * that need to be run alongside the main application
     */
    void onApplicationStarted();

    /**
     * Called before the server and main application is shutdown.
     * Will only be called on SIGINT (-2). This method may not be called
     * if running in an IDE or in a debugger, as they may issue different
     * process kill commands such as SIGTERM (-9) or SIGKILL (-15).
     * This will run in a specialized 'shutdown hook' thread. Logging
     * in this method may not be visible since this thread launches
     * after the link to stdio has been severed, and is run in the JVM
     * runtime afterwards
     */
    void onApplicationShutdown();
}
