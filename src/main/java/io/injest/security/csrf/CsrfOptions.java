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
 * Last Modified: 7/21/20, 11:09 PM
 */

package io.injest.security.csrf;

import java.util.Arrays;
import java.util.List;

public class CsrfOptions {

    final static CsrfOptions INSTANCE = new CsrfOptions();

    boolean enabled = false;
    boolean secure = false;
    String cookieName = "_csrf";
    String requestHeaderKey = "x-csrf-token";
    String responseHeaderKey = "x-csrf-token";
    String cookieDomain;
    int cookieLifetime = 43200;
    List<String> deferredMethods = Arrays.asList("GET", "HEAD", "OPTIONS");
    String secret;

    private CsrfOptions() {
    }

    public CsrfOptions setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public CsrfOptions setSecure(boolean secure) {
        this.secure = secure;
        return this;
    }

    public CsrfOptions setCookieName(String cookieName) {
        this.cookieName = cookieName;
        return this;
    }

    public CsrfOptions setRequestHeaderKey(String requestHeaderKey) {
        this.requestHeaderKey = requestHeaderKey;
        return this;
    }

    public CsrfOptions setResponseHeaderKey(String responseHeaderKey) {
        this.responseHeaderKey = responseHeaderKey;
        return this;
    }

    public CsrfOptions setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
        return this;
    }

    public CsrfOptions setCookieLifetime(int cookieLifetime) {
        this.cookieLifetime = cookieLifetime;
        return this;
    }

    public CsrfOptions setDeferredMethods(List<String> deferredMethods) {
        this.deferredMethods = deferredMethods;
        return this;
    }

    public CsrfOptions setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    @Override
    public String toString() {
        return "CsrfOptions{" +
                "enabled=" + enabled +
                ", secure=" + secure +
                ", cookieName='" + cookieName + '\'' +
                ", requestHeaderKey='" + requestHeaderKey + '\'' +
                ", responseHeaderKey='" + responseHeaderKey + '\'' +
                ", cookieDomain='" + cookieDomain + '\'' +
                ", cookieLifetime=" + cookieLifetime +
                ", deferredMethods=" + deferredMethods +
                '}';
    }
}
