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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final public class Log {

    private Logger log;

    private Log(Logger log) {
        this.log = log;
    }

    public static Log with(Class c) {
        return new Log(LoggerFactory.getLogger(c));
    }

    public Logger getLogger() {
        return this.log;
    }

    public void e(Object message) {
        log.error(message != null ? message.toString() : "null");
    }

    public void w(Object message) {
        log.warn(message != null ? message.toString() : "null");
    }

    public void i(Object message) {
        log.info(message != null ? message.toString() : "null");
    }

    public void d(Object message) {
        log.debug(message != null ? message.toString() : "null");
    }

    public void t(Object message) {
        log.trace(message != null ? message.toString() : "null");
    }
}