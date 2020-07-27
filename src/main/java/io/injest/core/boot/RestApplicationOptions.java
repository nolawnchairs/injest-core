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
 * Last Modified: 6/27/19 4:28 PM
 */

package io.injest.core.boot;

import io.injest.core.InjestApplication;
import io.injest.core.util.DeploymentMode;

public class RestApplicationOptions {

    private int port;
    private String host;
    private DeploymentMode deploymentMode;
    private Class<? extends InjestApplication> mainClass;

    /**
     * Private constructor, only builder class can create a
     * new instance of the options class
     */
    RestApplicationOptions() {
    }

    /**
     * Convenience method to chain to the end of the builder
     * chain, to initiate the startup of the REST application
     * directly after building
     */
    public RestApplicationInitializer build() {
        return new RestApplicationInitializer(this);
    }

    /**
     * Sets the port number to bind the application to. Defaults to 8080
     * @param port port number
     * @return builder instance
     */
    public RestApplicationOptions setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets the hostname to bind the application to. Defaults to "localhost"
     * @param host name or IP of the host
     * @return builder instance
     */
    public RestApplicationOptions setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets the deployment mode (development, staging, test or production)
     * Defaults to development
     * @param deploymentMode development mode enum
     * @return builder instance
     */
    public RestApplicationOptions setDeploymentMode(DeploymentMode deploymentMode) {
        this.deploymentMode = deploymentMode;
        return this;
    }

    /**
     * Required, as there is no default, must be the class where the main()
     * method is located. All annotation scanning is done from the top-down of
     * the package in which this class is located, so it should be in the top-most
     * level of your package hierarchy
     * This class must implement the {@link InjestApplication} interface
     * @param mainClass main class, must extend RestApplication
     * @return builder instance
     */
    public RestApplicationOptions setMainClass(Class<? extends InjestApplication> mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    int getPort() {
        return port;
    }

    DeploymentMode getDeploymentMode() {
        return deploymentMode;
    }

    Class<? extends InjestApplication> getMainClass() {
        return mainClass;
    }

    String getHost() {
        return host;
    }
}
