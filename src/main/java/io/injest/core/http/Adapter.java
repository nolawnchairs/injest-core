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
 * Last Modified: 6/28/19 12:20 PM
 */

package io.injest.core.http;

import java.io.Serializable;

/**
 * Base class for all response data adapters
 */
abstract public class Adapter implements Serializable {

    private transient Adapter replacement = null;
    private transient boolean isEjected = false;

    final Adapter getReplacement() {
        return this.replacement;
    }

    final boolean isEjected() {
        return isEjected;
    }

    final boolean isReplaced() {
        return this.replacement != null;
    }

    public void replace(Adapter other) {
        this.replacement = other;
    }

    public void eject() {
        this.isEjected = true;
    }

    public void clear() {
        replacement = null;
        isEjected = false;
    }
}
