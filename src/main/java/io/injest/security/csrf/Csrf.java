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
 * Last Modified: 7/21/20, 7:34 PM
 */

package io.injest.security.csrf;

public class Csrf {

    /**
     * Get the CSRF options singleton instance
     * @return CSRF Options
     */
    public static CsrfOptions getOptionsInstance() {
        return CsrfOptions.INSTANCE;
    }

    /**
     * Determine if CSRF protection is currently enabled
     * @return true if enabled
     */
    public static boolean isEnabled() {
        return CsrfOptions.INSTANCE.enabled;
    }
}
