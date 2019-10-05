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
 * Last Modified: 3/27/19 11:05 PM
 */

package io.injest.core.util;

import java.time.Instant;

public class TimeUtils {

    public static long timestampFromISODate(String isoDate) {
        return Instant.parse(isoDate).toEpochMilli();
    }

    public static String isoDateFromTimestamp(long timestamp) {
        return Instant.ofEpochMilli(timestamp).toString();
    }

    public static int intCast(long timestamp) {
        return Math.toIntExact(timestamp / 1000);
    }

    public static long timestamp() {
        return Instant.now().toEpochMilli();
    }

    public static int utime() {
        return intCast(Instant.now().toEpochMilli());
    }
}
