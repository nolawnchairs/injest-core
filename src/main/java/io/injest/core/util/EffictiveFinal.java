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
 * Last Modified: 7/16/20 5:19 PM
 */

package io.injest.core.util;

/**
 * Holds a value that is effectively final. Useful for occasions where
 * a value can mutate through code flow, but needs to be used in contexts
 * where values must be final or effectively final, such as lambda
 * expressios=ns
 * @param <T>
 */
public class EffictiveFinal<T> {

    private T value;

    /**
     * Holds a value that is effectively final
     * @param value the initial value
     */
    public EffictiveFinal(T value) {
        this.value = value;
    }

    /**
     * Returns the value
     * @return T the value
     */
    public T get() {
        return value;
    }

    /**
     * Sets the value
     * @param value the value
     */
    public void set(T value) {
        this.value = value;
    }
}
