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
 * Last Modified: 3/31/19 6:54 AM
 */

package io.injest.core.http;

class ResponseState {

    int statusCode = 200;
    RequestStatus requestStatus;
    AdapterStatus adapterStatus;

    ResponseState(int statusCode, RequestStatus requestStatus) {
        this.statusCode = statusCode;
        this.requestStatus = requestStatus;
        this.adapterStatus = AdapterStatus.ORIGINAL;
    }

    ResponseState(int statusCode, RequestStatus requestStatus, AdapterStatus adapterStatus) {
        this.statusCode = statusCode;
        this.requestStatus = requestStatus;
        this.adapterStatus = adapterStatus;
    }

    enum RequestStatus {
        VALID,
        INVALID
    }

    enum AdapterStatus {
        ORIGINAL,
        REPLACED,
        EJECTED
    }
}
