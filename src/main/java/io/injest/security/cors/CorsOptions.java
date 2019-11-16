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
 * Last Modified: 10/23/19 11:47 AM
 */

package io.injest.security.cors;

import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CorsOptions {

    final static CorsOptions INSTANCE = new CorsOptions();

    boolean enabled = true;
    Set<String> origins = Collections.singleton("*");
    Set<String> methods = Sets.newHashSet("GET", "HEAD", "PUT", "PATCH", "POST", "DELETE");
    Set<String> allowedHeaders = new HashSet<>();
    Set<String> exposedHeaders = new HashSet<>();
    boolean credentials = false;
    int optionsSuccessStatus = 204;
    int maxAge = -1;

    private CorsOptions() {
    }

    public CorsOptions setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public CorsOptions setOrigins(Set<String> origins) {
        this.origins = origins;
        return this;
    }

    public CorsOptions setMethods(Set<String> methods) {
        this.methods = methods;
        return this;
    }

    public CorsOptions setAllowedHeaders(Set<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
        return this;
    }

    public CorsOptions setExposedHeaders(Set<String> exposedHeaders) {
        this.exposedHeaders = exposedHeaders;
        return this;
    }

    public CorsOptions setCredentials(boolean credentials) {
        this.credentials = credentials;
        return this;
    }

    public CorsOptions setOptionsSuccessStatus(int optionsSuccessStatus) {
        this.optionsSuccessStatus = optionsSuccessStatus;
        return this;
    }

    public CorsOptions setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public int getOptionsSuccessStatus() {
        return optionsSuccessStatus;
    }

    @Override
    public String toString() {
        return "{" +
                "enabled=" + enabled +
                ", origins=" + origins.toString() +
                ", methods='" + methods.toString() + '\'' +
                ", allowedHeaders=" + allowedHeaders.toString() +
                ", exposedHeaders=" + exposedHeaders.toString() +
                ", credentials=" + credentials +
                ", optionsSuccessStatus=" + optionsSuccessStatus +
                ", maxAge=" + maxAge +
                '}';
    }
}
