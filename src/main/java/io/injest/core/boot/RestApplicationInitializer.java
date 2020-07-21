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
 * Last Modified: 7/21/20, 7:34 PM
 */

package io.injest.core.boot;

import io.injest.core.Injest;
import io.injest.core.InjestMessages;
import io.injest.core.annotations.directives.ConfigValue;
import io.injest.core.annotations.directives.PackageRoot;
import io.injest.core.res.ErrorMessageLoader;
import io.injest.core.res.ExceptionMessageLoader;
import io.injest.core.util.Env;
import io.injest.core.util.Exceptions;
import io.injest.core.util.Log;
import io.injest.core.util.ObjectUtils;
import io.undertow.server.HttpHandler;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 */
final public class RestApplicationInitializer {

    private static final Log log = Log.with(RestApplicationInitializer.class);

    private final RestApplication restApplication;
    private final InjestApplication baseApplication;
    private final RestApplicationOptions options;
    private final StaticConfig staticConfig = StaticConfig.getInstance();
    private String rootPackageName;
    private HttpHandler rootHandler;

    /**
     * Bootstraps the REST application
     * @param options Application start-up options
     */
    RestApplicationInitializer(RestApplicationOptions options) {

        // Load resources
        this.loadResources();

        // Make sure the Main class is valid
        if (options.getMainClass() == null)
            throw Exceptions.mainClassNotFound();

        Class<?> mainClass = options.getMainClass();
        this.baseApplication = (InjestApplication) ObjectUtils.createInstanceOf(mainClass);

        if (baseApplication == null)
            throw Exceptions.mainClassNotInstantiated();

        // create new RestApplication
        this.restApplication = new RestApplication(baseApplication);
        this.options = options;

        // Set deployment mode
        Env.setDeploymentMode(options.getDeploymentMode());

        this.baseApplication.onApplicationPreBootstrap();
    }

    /**
     * Start bootstrapping the application
     */
    public void start() {

        log.i("Injest "+ Injest.VERSION);
        log.i(String.format("Bootstrapping Injest for %s mode", options.getDeploymentMode().name()));

        // Set the package of Main class so reflections knows where to start
        final Class<?> mainClass = options.getMainClass();

        // Find root package implementation
        Package rootPackage;
        if (mainClass.isAnnotationPresent(PackageRoot.class)) {
            final String providedPackageName = mainClass.getAnnotation(PackageRoot.class).value();
            if (providedPackageName.length() > 0) {
                rootPackage = Package.getPackage(providedPackageName);
                if (rootPackage == null) {
                    rootPackage = mainClass.getPackage();
                    InjestMessages.rootPackageInvalid(providedPackageName, rootPackage.getName())
                            .toErrorLog(this);
                }
            } else {
                rootPackage = mainClass.getPackage();
            }
        } else {
            rootPackage = mainClass.getPackage();
        }

        // Set the root implementation package name
        this.rootPackageName = rootPackage.getName();
        log.i(String.format("Root application package: [%s]", rootPackageName));

        // Scan for configuration details
        this.scanConfig();
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final PackageScanner scanner = new PackageScanner(rootPackageName);
        final Future<HttpHandler> futureHandler = executor.submit(scanner);
        executor.shutdown();

        try {
            // Wait for scanner callable to finish, get root handler
            this.rootHandler = futureHandler.get();

            log.i("Scanning completed, root handler created.");
            log.i("Invoking post-scan Bootables");

            // Invoke lower-priority Bootables
            BootManager.INSTANCE.invokePostScan(this::launchApplication);
            this.baseApplication.onApplicationPostBootstrap();
        } catch (Exception e) {
            log.e("An exception was thrown during application bootstrap. Shutting down...");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     *
     */
    private void scanConfig() {
        Reflections reflections = new Reflections(rootPackageName,
                new FieldAnnotationsScanner());
        reflections.getFieldsAnnotatedWith(ConfigValue.class)
                .forEach(staticConfig::assignValueFromField);
    }

    /**
     *
     */
    private void loadResources() {
        Arrays.asList(
                new ExceptionMessageLoader(),
                new ErrorMessageLoader()
        ).forEach(Runnable::run);
    }

    /**
     * Launches the REST application after bootstrap has completed
     */
    private void launchApplication() {
        log.i("Starting REST application");
        restApplication.start(options.getPort(), options.getHost(), rootHandler);
    }
}
