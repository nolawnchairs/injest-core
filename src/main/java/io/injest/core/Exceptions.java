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
 * Last Modified: 7/22/20, 12:54 AM
 */

package io.injest.core;

public final class Exceptions {

    public static RuntimeException mainClassNotFound() {
        return new RuntimeException("Main class was not defined in RestApplicationOptions");
    }

    public static IllegalArgumentException mainClassNotInstantiated() {
        return new IllegalArgumentException(
                "Specified main class could not be instantiated. Main class must implement the io.injest.core.boot.InjestApplication interface and must have a no-arg constructor");
    }

    public static IllegalStateException bootablesAlreadyInvoked(String className) {
        return new IllegalStateException(String.format(
                "Attempting to invoke Bootable [%s] after application boot time",
                className));
    }

    public static IllegalStateException missingCsrfSecret() {
        return new IllegalStateException("You must supply a secret string configuration value to use CSRF protection.");
    }

    public static IllegalStateException missingCsrfCookieDomain() {
        return new IllegalStateException("You must supply a cookie domain string configuration value to use CSRF protection.");
    }

    public static IllegalStateException duplicateHandlerDefined(String className) {
        return new IllegalStateException(String.format(
                "Multiple adapters/handlers found annotated with [%s]. Only one class may be assigned to this endpoint.",
                className));
    }

    public static IllegalStateException handlerNotInstantiated(String className) {
        return new IllegalStateException(String.format(
                "Unable to instantiate handler [%s]",
                className));
    }

    public static IllegalStateException nullAdapterEncountered(String className) {
        return new IllegalStateException(String.format(
                "Unable to instantiate Adapter for [%s]. This may be caused by using a non-static inner class or an uncaught exception in the constructor of the Handler or its Adapter.",
                className));
    }

    public static IllegalStateException configFieldNotStatic(String fieldName) {
        return new IllegalStateException(String.format(
                "Configuration fields must be static. Non-static field detected: %s",
                fieldName));
    }

    public static IllegalStateException chainableHandlerNotAnnotated(String className) {
        return new IllegalStateException(String.format(
                "@ChainHandler annotation is missing from ChainableHandler [%s]",
                className));
    }

    public static IllegalArgumentException parametersInspectedAlready() {
        return new IllegalArgumentException(
                "Attempting to define required request parameters after or during handler invocation. Define required arguments by annotating the handler class with @RequireParams");
    }

    public static IllegalStateException attachingAfterBootstrap() {
        return new IllegalStateException(
                "Attachments cannot be added to handlers after the bootstrapping phase");
    }

    public static IllegalStateException interceptorNotConstructed(String className) {
        return new IllegalStateException(String.format(
                "Interceptor [%s] failed to construct. An exception was thrown in its constructor",
                className
        ));
    }
}
