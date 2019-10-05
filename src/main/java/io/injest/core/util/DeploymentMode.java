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
 * Last Modified: 6/27/19 5:36 PM
 */

package io.injest.core.util;

public enum DeploymentMode {
    TEST("test"),
    DEVELOPMENT("development"),
    DEBUG("debug"),
    QA("qa"),
    ALPHA("alpha"),
    BETA("beta"),
    STAGING("staging"),
    PRODUCTION("production"),
    ANY("any");

    private String value = "any";

    DeploymentMode(String value) {
        this.value = value.toLowerCase();
    }

    public static DeploymentMode fromString(String mode) {
        return fromString(mode.toLowerCase(), DeploymentMode.DEVELOPMENT);
    }

    public static DeploymentMode fromString(String mode, DeploymentMode fallback) {
        for (DeploymentMode m : DeploymentMode.values()) {
            if (m.value.toLowerCase().equals(mode)) return m;
        }
        return fallback;
    }
}
