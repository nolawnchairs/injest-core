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

package io.injest.core.boot;

final public class Bootables {

    /**
     * Queue bootable to be added to the pre-scan boot queue
     * using next available priority slot
     * @param b the bootable
     */
    public static void queueBeforePackageScan(Bootable b) {
        BootManager.INSTANCE.add(BootManager.INVOCATION_PRE_SCAN, b);
    }

    /**
     * Queue bootable to be added to the pre-scan boot queue
     * using the defined priority slot
     * @param b Bootable class to add
     * @param order priority order
     */
    public static void queueBeforePackageScan(Bootable b, int order) {
        BootManager.INSTANCE.add(BootManager.INVOCATION_PRE_SCAN, b, order);
    }

    /**
     * Queue bootable to be added to the pre-scan boot queue
     * using next available priority slot
     * @param b the bootable
     */
    public static void queueAfterPackageScan(Bootable b) {
        BootManager.INSTANCE.add(BootManager.INVOCATION_POST_SCAN, b);
    }

    /**
     * Queue bootable to be added to the post-scan boot queue
     * using the defined priority slot
     * @param b the bootable
     * @param order priority order
     */
    public static void queueAfterPackageScan(Bootable b, int order) {
        BootManager.INSTANCE.add(BootManager.INVOCATION_POST_SCAN, b);
    }
}
