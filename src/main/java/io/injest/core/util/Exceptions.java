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
 * Last Modified: 7/22/20, 12:21 AM
 */

package io.injest.core.util;

import io.injest.core.res.ResourceValues;

public final class Exceptions {

    public static RuntimeException mainClassNotFound() {
        return new RuntimeException(ResourceValues.getExceptionMessage("mainClassUndefined"));
    }

    public static IllegalArgumentException mainClassNotInstantiated() {
        return new IllegalArgumentException(ResourceValues.getExceptionMessage("mainClassNotInstantiated"));
    }

    public static IllegalStateException bootablesAlreadyInvoked(String className) {
        return new IllegalStateException(String.format(
                ResourceValues.getExceptionMessage("bootablesAlreadyInvoked"),
                className));
    }

    public static IllegalStateException missingCsrfSecret() {
        return new IllegalStateException(ResourceValues.getExceptionMessage("missingCsrfSecret"));
    }

    public static IllegalStateException missingCsrfCookieDomain() {
        return new IllegalStateException(ResourceValues.getExceptionMessage("missingCsrfCookieDomain"));
    }

    public static IllegalStateException duplicateHandlerDefined(String className) {
        return new IllegalStateException(String.format(
                ResourceValues.getExceptionMessage("duplicateHandlerDefined"),
                className));
    }

    public static IllegalStateException handlerNotInstantiated(String className) {
        return new IllegalStateException(String.format(
                ResourceValues.getExceptionMessage("handlerNotInstantiated"),
                className));
    }

    public static IllegalStateException nullAdapterEncountered(String className) {
        return new IllegalStateException(String.format(
                ResourceValues.getExceptionMessage("nullAdapterEncountered"),
                className));
    }

    public static IllegalStateException configFieldNotStatic(String fieldName) {
        return new IllegalStateException(String.format(
                ResourceValues.getExceptionMessage("configFieldNotStatic"),
                fieldName));
    }

    public static IllegalStateException chainableHandlerNotAnnotated(String className) {
        return new IllegalStateException(String.format(
                ResourceValues.getExceptionMessage("chainableHandlerNotAnnotated"),
                className));
    }

    public static IllegalArgumentException parametersInspectedAlready() {
        return new IllegalArgumentException(
                ResourceValues.getExceptionMessage("parametersInspectedAlready"));
    }

    public static IllegalStateException attachingAfterBootstrap() {
        return new IllegalStateException(
                ResourceValues.getExceptionMessage("attachingAfterBootstrap"));
    }

    public static IllegalStateException interceptorNotConstructed(String className) {
        return new IllegalStateException(String.format(
                ResourceValues.getExceptionMessage("interceptorConstructorFailed"),
                className
        ));
    }
}
