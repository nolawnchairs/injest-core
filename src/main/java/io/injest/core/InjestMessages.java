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
 * Last Modified: 7/22/20, 12:08 AM
 */

package io.injest.core;

import io.injest.core.res.ResourceValues;
import io.injest.core.util.Log;
import io.undertow.server.HttpServerExchange;
import java.util.Set;
import java.util.function.BiConsumer;

public class InjestMessages {

    public static Message errorParsingBodyParameters(HttpServerExchange exchange, String message) {
        return new Message(String.format(
                ResourceValues.getErrorMessage("bodyParamParseError"),
                exchange.getRequestMethod().toString(),
                exchange.getRequestURI(),
                message));
    }

    public static Message errorParsingRequestBody(HttpServerExchange exchange, String message) {
        return new Message(String.format(
                ResourceValues.getErrorMessage("bodyParseError"),
                exchange.getRequestMethod().toString(),
                exchange.getRequestURI(),
                message
        ));
    }

    public static Message handlerClassesHaveMembers() {
        return new Message(ResourceValues.getErrorMessage("handlerClassesHaveMembers"));
    }

    public static Message wrappedHandlerNotImplemented(String className) {
        return new Message(String.format(ResourceValues.getErrorMessage("wrappedHandlerNotImplemented"), className));
    }

    public static Message rootPackageInvalid(String provided, String rootPackage) {
        return new Message(String.format(ResourceValues.getErrorMessage("rootPackageInvalid"), provided, rootPackage));
    }

    public static Message unfulfilledDeferredConfigValues(Set<String> unfulfilled) {
        return new Message(String.format(ResourceValues.getErrorMessage("unfulfilledDeferredConfigValues"), unfulfilled.toString()));
    }

    public static Message invalidConfigValueType(String key, Class<?> type) {
        return new Message(String.format(ResourceValues.getErrorMessage("invalidConfigValueType"), key, type.getName()));
    }

    public static Message invalidCustomAnnotationDeclaration(Class<?> type) {
        return new Message(String.format(ResourceValues.getErrorMessage("invalidCustomAnnotationDeclaration"), type.getName()));
    }

    public static Message interceptorNotLoaded(Class<?> type) {
        return new Message(String.format("Interceptor [%s] failed to load.", type.getName()));
    }

    public static class Message {

        final String content;

        private Message(String content) {
            this.content = content;
        }

        public void log(Object caller, BiConsumer<Log, String> consumer) {
            Log log = Log.with(caller.getClass());
            consumer.accept(log, content);
        }

        public void toErrorLog(Log log) {
            log.e(content);
        }

        public void toInfoLog(Log log) {
            log.i(content);
        }

        public void toWarningLog(Log log) {
            log.w(content);
        }

        public void toDebugLog(Log log) {
            log.d(content);
        }

        public void toStdout() {
            System.out.println(content);
        }

        public void toStderr() {
            System.err.println(content);
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
