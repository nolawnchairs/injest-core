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
 * Last Modified: 4/21/19 10:30 AM
 */

package io.injest.core.util;

import java.util.Optional;
import java.util.function.Function;

public class Env {

    private static DeploymentMode deploymentMode;

    public static void setDeploymentMode(DeploymentMode mode) {
        deploymentMode = mode;
    }

    public static DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }

    public static boolean isTest() {
        return deploymentMode == DeploymentMode.TEST;
    }

    public static boolean isDevelopment() {
        return deploymentMode == DeploymentMode.DEVELOPMENT;
    }

    public static boolean isStaging() {
        return deploymentMode == DeploymentMode.STAGING;
    }

    public static boolean isProduction() {
        return deploymentMode == DeploymentMode.PRODUCTION;
    }

    public static <T> Optional<T> getVar(String key, Function<String, T> transformFn) {
        String var = System.getenv(key);
        if (var != null)
            return Optional.ofNullable(transformFn.apply(var));
        return Optional.empty();
    }
}
