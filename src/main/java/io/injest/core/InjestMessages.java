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

import io.injest.core.util.Log;
import io.undertow.server.HttpServerExchange;
import java.util.function.BiConsumer;

public class InjestMessages {

    public static Message errorParsingBodyParameters(HttpServerExchange exchange, String message) {
        return new Message(String.format(
                "Error parsing body parameters for request [%s %s] - %s",
                exchange.getRequestMethod().toString(),
                exchange.getRequestURI(),
                message));
    }

    public static Message handlerClassesHaveMembers() {
        return new Message("Handler classes with non-static or ThreadLocal fields were detected during the scan. " +
                "Only a single instance of each handler is created for each route, thus field values will persist through " +
                "the application's lifetime. Handler logic should be functional in nature, contained within provided handler" +
                " override methods. To use a field, it should be static or ThreadLocal. Check the following classes:");
    }

    public static Message wrappedHandlerNotImplemented(String className) {
        return new Message(
                String.format("Class [%s] annotated with @WrappedHandler does not implement the HandlerWrappable interface. " +
                        "This handler will not be attached to the handler chain", className));
    }

    public static Message rootPackageInvalid(String provided, String rootPackage) {
        return new Message(
                String.format("Package specified in PackageRoot [%s] annotation could not be located in the class path. Scanning " +
                        "the package containing the Main class instead [%s].", provided, rootPackage));
    }


    public static Message invalidConfigValueType(String key, Class<?> type) {
        return new Message(
                String.format("Configuration values must be of a primitive type, string, enum or array of primitives or strings. " +
                        "Provided invalid type [%s] for config key '%s'", key, type.getName()));
    }

    public static Message invalidCustomAnnotationDeclaration(Class<?> type) {
        return new Message(
                String.format("Could not invoke custom annotation handler [%s]. AnnotationHandler type argument must be an Annotation",
                        type.getName()));
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
