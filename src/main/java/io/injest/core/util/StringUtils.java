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
 * Last Modified: 3/10/19 10:38 PM
 */

package io.injest.core.util;

public class StringUtils {

    public static String trim(String path, char delimiter) {
        if (path.isEmpty())
            return path;

        int beginIndex = 0;
        while (beginIndex < path.length() && path.charAt(beginIndex) == delimiter)
            beginIndex++;
        if (beginIndex == path.length())
            return "";

        int endIndex = path.length() - 1;
        while (endIndex > beginIndex && path.charAt(endIndex) == delimiter)
            endIndex--;

        return path.substring(beginIndex, endIndex + 1);
    }

    public static String capitalize(String s) {
        String o1 = s.substring(0, 1).toUpperCase();
        String o2 = s.substring(1).toLowerCase();
        return o1 + o2;
    }
}
