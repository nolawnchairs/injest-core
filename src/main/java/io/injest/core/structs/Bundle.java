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
 * Last Modified: 4/12/19 11:22 PM
 */

package io.injest.core.structs;

import java.util.Set;

/**
 * Mutable collection of multiple data types. Forces implementation
 * of both write-only Receptacle and read-only Parcel interfaces
 * with addition of has() method to determine presence
 */
public interface Bundle extends Parcel, Receptacle {


    /**
     * Get the set of string entries
     * @return KeySet of value keys
     */
    Set<String> keySet();

    /**
     * Get the class type of a bundled value
     * @param key value identifier
     * @return Class type
     */
    Class<?> getValueType(String key);
}
