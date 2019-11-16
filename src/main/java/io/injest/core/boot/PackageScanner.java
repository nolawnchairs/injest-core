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
 * Last Modified: 6/25/19 9:54 PM
 */

package io.injest.core.boot;

import io.injest.core.InjestMessages;
import io.injest.core.annotations.directives.*;
import io.injest.core.annotations.handlers.FallbackHandler;
import io.injest.core.annotations.handlers.InvalidMethodHandler;
import io.injest.core.annotations.method.*;
import io.injest.core.http.*;
import io.injest.core.util.DeploymentMode;
import io.injest.core.util.Env;
import io.injest.core.util.Exceptions;
import io.injest.core.util.Log;
import io.injest.core.util.ObjectUtils;
import io.injest.security.cors.Cors;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import org.reflections.Reflections;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import static io.injest.core.boot.ConfigKeys.ENABLE_GZIP;
import static io.injest.core.util.ObjectUtils.createInstanceOf;

final class PackageScanner implements Callable<HttpHandler> {

    private static Log log = Log.with(PackageScanner.class);

    private final String rootPackage;
    private final Reflections reflections;
    private final DeploymentMode mode = Env.getDeploymentMode();
    private final RoutingHandler routingHandler;
    private final RestConfig restConfig = RestConfig.getInstance();
    private final ScanEventListener eventListener;

    /**
     * Scans the provided package for annotations to build handlers, interceptors
     * and bootables in order to bootstrap as a multi-module application
     * @param rootPackage String value of root implementation package
     */
    PackageScanner(String rootPackage) {
        this.rootPackage = rootPackage;
        this.reflections = new Reflections(rootPackage);
        this.routingHandler = Handlers.routing();
        this.eventListener = BootManager.INSTANCE.getScanEventListener();
    }

    /**
     * Perform annotation scanning of the root package.
     *
     * 1) Map Bootables, and invoke blocking, pre-scan Bootables
     * 2) Map Interceptors
     * 3) Map HTTP request handlers
     * 4) Map Error handlers
     * 5) Instantiate Wrap Handlers
     * 6) Set GZIP and Response type
     *
     */
    @Override
    public HttpHandler call() throws Exception {

        final long started = System.currentTimeMillis();
        ApplicationState.setState(ApplicationState.State.BOOT);

        log.i("Scanning for custom annotation handlers...");
        for (Class<?> aClass : reflections.getTypesAnnotatedWith(CustomAnnotation.class)) {
            CustomAnnotation ca = aClass.getAnnotation(CustomAnnotation.class);
            AnnotationHandler handler = (AnnotationHandler) createInstanceOf(aClass);
            BootManager.addCustomAnnotationHandler(ca.value(), handler);
            log.i(String.format(" - Mapped custom annotation handler [%s] to %s", ca.value().getName(), handler.getClass().getName()));
        }


        log.i("Scanning for Bootables...");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Boot.class)) {
            if (!shouldIgnore(clazz)) {
                final Bootable bootable = (Bootable) createInstanceOf(clazz);
                int bootPriority = clazz.getAnnotation(Boot.class).value();
                logBootableMapping(clazz.getName());
                if (bootPriority > 0) {
                    Bootables.queueBeforePackageScan(bootable, bootPriority);
                } else {
                    Bootables.queueAfterPackageScan(bootable);
                }
            }
        }

        BootManager.INSTANCE.invokeBeforeScan(); // <-- blocking op

        ApplicationState.setState(ApplicationState.State.SCAN);

        log.i("Scanning for Request Interceptors...");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(RequestInterceptor.class)) {
            if (!shouldIgnore(clazz)) {
                int priority = clazz.getAnnotation(RequestInterceptor.class).value();
                Interceptors.addRequestInterceptor((Interceptor) createInstanceOf(clazz), priority);
                logInterceptorMapping("request", clazz.getName(), priority);
            }
        }

        log.i("Scanning for Response Interceptors...");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(ResponseInterceptor.class)) {
            if (!shouldIgnore(clazz)) {
                int priority = clazz.getAnnotation(ResponseInterceptor.class).value();
                Interceptors.addResponseInterceptor((Interceptor) createInstanceOf(clazz), priority);
                logInterceptorMapping("response", clazz.getName(), priority);
            }
        }

        log.i("Scanning for Ending Interceptors...");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(EndingInterceptor.class)) {
            if (!shouldIgnore(clazz)) {
                int priority = clazz.getAnnotation(EndingInterceptor.class).value();
                Interceptors.addEndingInterceptor((Interceptor) createInstanceOf(clazz), priority);
                logInterceptorMapping("ending", clazz.getName(), priority);
            }
        }

        final Set<Class<?>> methodHandlers = new HashSet<>();
        final Set<String> methodHandlerNames = new HashSet<>();

        // Scan for handlers...
        log.i("Scanning for Route Handlers...");

        // Map GET handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Get.class)) {
            if (!clazz.isAnnotationPresent(Ignored.class)) {
                String requestUri = clazz.getAnnotation(Get.class).value();
                String[] optionalUris = clazz.getAnnotation(Get.class).also();
                addRouteMappings(RequestMethod.GET, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Map POST handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Post.class)) {
            if (!clazz.isAnnotationPresent(Ignored.class)) {
                String requestUri = clazz.getAnnotation(Post.class).value();
                String[] optionalUris = clazz.getAnnotation(Post.class).also();
                addRouteMappings(RequestMethod.POST, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Map PUT handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Put.class)) {
            if (!clazz.isAnnotationPresent(Ignored.class)) {
                String requestUri = clazz.getAnnotation(Put.class).value();
                String[] optionalUris = clazz.getAnnotation(Put.class).also();
                addRouteMappings(RequestMethod.PUT, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Map DELETE handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Delete.class)) {
            if (!shouldIgnore(clazz)) {
                String requestUri = clazz.getAnnotation(Delete.class).value();
                String[] optionalUris = clazz.getAnnotation(Delete.class).also();
                addRouteMappings(RequestMethod.DELETE, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Map HEAD handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Head.class)) {
            if (!shouldIgnore(clazz)) {
                String requestUri = clazz.getAnnotation(Head.class).value();
                String[] optionalUris = clazz.getAnnotation(Head.class).also();
                addRouteMappings(RequestMethod.HEAD, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Map OPTIONS handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Options.class)) {
            if (!shouldIgnore(clazz)) {
                String requestUri = clazz.getAnnotation(Options.class).value();
                String[] optionalUris = clazz.getAnnotation(Options.class).also();
                addRouteMappings(RequestMethod.OPTIONS, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Map PATCH handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Patch.class)) {
            if (!shouldIgnore(clazz)) {
                String requestUri = clazz.getAnnotation(Patch.class).value();
                String[] optionalUris = clazz.getAnnotation(Patch.class).also();
                addRouteMappings(RequestMethod.PATCH, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Map TRACE handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Trace.class)) {
            if (!shouldIgnore(clazz)) {
                String requestUri = clazz.getAnnotation(Trace.class).value();
                String[] optionalUris = clazz.getAnnotation(Trace.class).also();
                addRouteMappings(RequestMethod.TRACE, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Map CONNECT handlers
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Connect.class)) {
            if (!shouldIgnore(clazz)) {
                String requestUri = clazz.getAnnotation(Connect.class).value();
                String[] optionalUris = clazz.getAnnotation(Connect.class).also();
                addRouteMappings(RequestMethod.CONNECT, clazz, requestUri, optionalUris);
                methodHandlers.add(clazz);
            }
        }

        // Introspect mapped method handlers and issue a warning if
        // there are handler classes that have non-static fields
        for (Class<?> clazz : methodHandlers) {
            Field[] fields = clazz.getDeclaredFields();
            if (fields.length > 0) {
                int i = 0;
                StringJoiner joiner = new StringJoiner(", ");
                String name = "\t\t" + clazz.getName();
                for (Field f : fields) {
                    if (!Modifier.isStatic(f.getModifiers()) && !f.getType().equals(ThreadLocal.class)) {
                        joiner.add(f.getType().getName() +" "+ f.getName());
                        i++;
                    }
                }
                if (i > 0)
                    methodHandlerNames.add(name + " ["+ joiner.toString() +"]");
            }
        }

        // If handler classes have non-static fields, issue warning
        if (methodHandlerNames.size() > 0) {
            InjestMessages.handlerClassesHaveMembers().toWarningLog(this);
            methodHandlerNames.forEach(log::w);
        }

        // Set user-defined fallback handler
        HttpHandler fallbackHandler = (HttpHandler) getFirstOfAnnotatedType(FallbackHandler.class);
        if (fallbackHandler != null) {
            routingHandler.setFallbackHandler(fallbackHandler);
            logHandlerMapping("fallback", fallbackHandler.getClass().getName());
        } else {
            routingHandler.setFallbackHandler(new DefaultHandlers.DefaultFallbackHandler());
        }


        for (Class<?> clazz : reflections.getTypesAnnotatedWith(RequestError.class)) {
            int errorType = clazz.getAnnotation(RequestError.class).value();
            Adapters.INSTANCE.putErrorAdapter(errorType, (ErrorAdapter) ObjectUtils.createInstanceOf(clazz));
            logAdapterMapping("error", clazz.getName());
        }

        // Set user-defined invalid method handler (405 error)
        final HttpHandler invalidMethodHandler = (HttpHandler) getFirstOfAnnotatedType(InvalidMethodHandler.class);
        if (invalidMethodHandler != null) {
            routingHandler.setInvalidMethodHandler(invalidMethodHandler);
            logHandlerMapping("invalid method", invalidMethodHandler.getClass().getName());
        } else {
            routingHandler.setInvalidMethodHandler(new DefaultHandlers.DefaultFallbackHandler());
        }

        // Set default CORS handler
        if (Cors.isEnabled()) {
            routingHandler.add(RequestMethod.OPTIONS.toString(), "*",
                    new DefaultHandlers.DefaultOptionsHandler());
        }

        // Set the root handler to the RoutingHandler instance, and
        // we can attach extra handlers to the chain
        HttpHandler rootHandler = routingHandler;

        // Scan for WrappedHandler annotations in order to attach different handlers to
        // the root handler chain. Add to a TreeMap, so they are iterated in order of
        // annotation value
        log.i("Chaining custom wrapped handlers...");
        final TreeMap<Integer, HandlerWrappable> wrappedHandlers = new TreeMap<>();
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(WrappedHandler.class)) {
            Object instance = createInstanceOf(clazz);
            if (instance instanceof HandlerWrappable) {
                final int priority = clazz.getAnnotation(WrappedHandler.class).value();
                final HandlerWrappable wrappable = (HandlerWrappable) instance;
                wrappedHandlers.put(priority, wrappable);
                logWrapperMapping(clazz.getName(), priority);
            } else {
                InjestMessages.wrappedHandlerNotImplemented(clazz.getName()).toErrorLog(this);
            }
        }

        Map<Class<? extends Annotation>, AnnotationHandler<?>> customAnnotationHandlers = BootManager.INSTANCE.getCustomAnnotationHandlers();
        for (Class<? extends Annotation> entry : customAnnotationHandlers.keySet()) {
            log.i(String.format(" - Invoking handler(s) for annotation [%s]", entry.getName()));
            AnnotationHandler handler = customAnnotationHandlers.get(entry);
            for (Class<?> clazz : reflections.getTypesAnnotatedWith(entry)) {
                Annotation a = clazz.getAnnotation(entry);
                handler.handleAnnotatedClass(a, clazz);
            }
        }

        // Iterate through wrapped handlers and add to chain in user-specified order
        for (Map.Entry<Integer, HandlerWrappable> entry : wrappedHandlers.entrySet()) {
            rootHandler = entry.getValue().wrap(rootHandler);
        }

        // If GZIP is enabled, we add it to the chain
        if (restConfig.getBoolean(ENABLE_GZIP)) {
            log.i("Enable GZIP: enabled");
            rootHandler = new EncodingHandler(new ContentEncodingRepository()
                    .addEncodingHandler("gzip", new GzipEncodingProvider(), 50))
                    .setNext(rootHandler);
        }

        // Set default content-type
        String contentTypeDefault = restConfig.getString(ConfigKeys.DEFAULT_RESPONSE_CONTENT_TYPE);
        log.i(String.format("Default Response Content-Type: '%s'",
                contentTypeDefault));

        log.i(String.format(
                "Finished scanning package [%s] in %dms",
                rootPackage,
                System.currentTimeMillis() - started));

        // When scanning is complete, return built HttpHandler
        return rootHandler;
    }

    /**
     * Add route mappings to main routing handler
     * @param method request method
     * @param clazz handler class
     * @param primaryUri main (required) target URI
     * @param alternateUris alternate (optional) target URIs
     */
    private void addRouteMappings(RequestMethod method, Class<?> clazz, String primaryUri, String[] alternateUris) {
        final ArrayList<String> acceptUris = new ArrayList<>();
        acceptUris.add(primaryUri);
        acceptUris.addAll(Arrays.asList(alternateUris));
        for (String uri : acceptUris) {
            Handler handler = (Handler) createHandlerInstance(clazz);
            HandlerRegistry.getInstance().put(clazz, handler);
            if (clazz.isAnnotationPresent(RequireParams.class)) {
                String[] requiredParams = clazz.getAnnotation(RequireParams.class).value();
                RequiredParameters requiredParameters = new RequiredParameters(requiredParams);
                handler.putAttachment(RequiredParameters.ATTACHMENT_KEY, requiredParameters);
            }
            if (clazz.isAnnotationPresent(ParamSource.class)) {
                ParamSource.Source source = clazz.getAnnotation(ParamSource.class).value();
                if (source != ParamSource.Source.ANY)
                    handler.putAttachment(ParamSource.ATTACHMENT_KEY, source);
            } else {
                ParamSource.Source source = HttpParameters.getDefaultMethodSource(method);
                if (source != ParamSource.Source.ANY)
                    handler.putAttachment(ParamSource.ATTACHMENT_KEY, source);
            }
            if (clazz.isAnnotationPresent(Blocking.class)) {
                handler.putAttachment(Blocking.ATTACHMENT_KEY, true);
            }
            routingHandler.add(method.toString(), uri, handler);
            logRouteMapping(method.toString(), uri, clazz);
            if (eventListener != null) {
                eventListener.onHandlerCreated(method, primaryUri, clazz);
                for (String s : alternateUris)
                    eventListener.onHandlerCreated(method, s, clazz);
            }
        }
    }

    /**
     * Determine if this handler should not be mapped
     * given a certain deployment mode
     * @param clazz handler class
     * @return if it's to be ignored
     */
    private boolean shouldIgnore(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Ignored.class)) {
            DeploymentMode value = clazz.getAnnotation(Ignored.class).value();
            return value == DeploymentMode.ANY || value == mode;
        }
        return false;
    }

    /**
     * Gets first class found with annotation
     * @param clazz annotation class
     * @return first Object found
     */
    private Object getFirstOfAnnotatedType(Class<? extends Annotation> clazz) {
        Set<Class<?>> candidates = reflections.getTypesAnnotatedWith(clazz);
        if (candidates.size() > 0) {
            if (candidates.size() > 1)
                throw Exceptions.duplicateHandlerDefined(clazz.getName());
            Class<?> winner = candidates.iterator().next();
            return createInstanceOf(winner);
        }
        return null;
    }

    /**
     * Create an instance of a handler class
     * @param clazz handler class
     * @return newly instantiated Handler
     */
    private HttpHandler createHandlerInstance(Class<?> clazz) {
        HttpHandler handler = (HttpHandler) createInstanceOf(clazz);
        if (handler == null)
            throw Exceptions.handlerNotInstantiated(clazz.getName());
        return handler;
    }

    /**
     * Log route mapping
     * @param method request method
     * @param requestUri handler URI
     * @param clazz class
     */
    private void logRouteMapping(String method, String requestUri, Class clazz) {
        log.i(String.format(" - Mapped Route Handler %s %s to [%s]",
                method.toUpperCase(), requestUri, clazz.getName()));
    }

    /**
     * Log interceptor mapping
     * @param type interceptor type
     * @param className interceptor class name
     * @param priority interceptor invoke order
     */
    private void logInterceptorMapping(String type, String className, int priority) {
        log.i(String.format(" - Mapped %s Interceptor (priority %d) to [%s]",
                type, priority, className));
    }

    /**
     * Log non-route handler mapping
     * @param type handler type
     * @param className handler class name
     */
    private void logHandlerMapping(String type, String className) {
        log.i(String.format(" - Mapped %s Handler to [%s]", type, className));
    }

    /**
     * Log adapter mapping
     * @param type adapter type
     * @param className adapter class name
     */
    private void logAdapterMapping(String type, String className) {
        log.i(String.format(" - Mapped %s Adapter to [%s]", type, className));
    }

    /**
     * Log bootable mapping
     * @param className bootable class name
     */
    private void logBootableMapping(String className) {
        log.i(String.format(" - Mapped Bootable to [%s]", className));
    }

    /**
     * Log wrapper mapping
     * @param className wrapper class name
     * @param priority chain order priority
     */
    private void logWrapperMapping(String className, int priority) {
        log.i(String.format(" - Chaining Wrappable Handler (priority %d) to [%s]", priority, className));
    }
}
