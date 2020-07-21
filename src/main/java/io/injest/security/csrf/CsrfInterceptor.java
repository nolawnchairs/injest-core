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
 * Last Modified: 7/21/20, 11:37 PM
 */

package io.injest.security.csrf;

import io.injest.core.http.HttpRequest;
import io.injest.core.http.HttpResponse;
import io.injest.core.http.Interceptor;
import io.injest.core.Exceptions;
import io.injest.core.util.Log;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.UUID;
import java.util.stream.Collectors;

abstract public class CsrfInterceptor implements Interceptor {

    final CsrfOptions options = CsrfOptions.INSTANCE;
    private static final Log LOG = Log.with(CsrfInterceptor.class);

    protected CsrfInterceptor() {
        LOG.i("Configuring CSRF Interceptor...");
        options.enabled = true;
        this.configure(options);

        if (options.secret == null)
            throw Exceptions.missingCsrfSecret();
        if (options.cookieDomain == null)
            throw Exceptions.missingCsrfCookieDomain();

        LOG.i("CSRF Configured: " + options.toString());
    }

    /**
     * Configure CSRF when enabled
     * @param options CSRF options instance
     */
    protected abstract void configure(CsrfOptions options);

    @Override
    public void intercept(HttpRequest request, HttpResponse response) {
        if (shouldEnforce(request, response)) {
            final String csrfHeaderValue = request.getHeader(options.requestHeaderKey);
            final boolean isMethodDeferred = options.deferredMethods.stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.toList())
                    .contains(request.getRequestMethod().toString());
            if (!isMethodDeferred) {
                final String tokenValue = getHashedValue(request);
                if (tokenValue == null) {
                    response.putHeader(options.responseHeaderKey, csrfHeaderValue);
                    onCsrfViolation(request, response);
                    return;
                }
                final String hashed = encodeToken(tokenValue, options.secret);
                final boolean csrfPassed = hashed.equals(csrfHeaderValue);
                if (csrfPassed) {
                    final String newUuid = UUID.randomUUID().toString();
                    final String newToken = encodeToken(newUuid, options.secret);
                    response.putHeader(options.responseHeaderKey, newToken);
                    setCookieValue(newUuid, request, response);
                    setSessionValue(newUuid, request, response);
                } else {
                    response.putHeader(options.responseHeaderKey, csrfHeaderValue);
                    onCsrfViolation(request, response);
                }
            }
        }
    }

    /**
     * Determine whether or not to carry out the CSRF detection
     * @param request HttpRequest
     * @param response HttpResponse
     * @return whether or not to carry out the CSRF detection
     */
    protected boolean shouldEnforce(HttpRequest request, HttpResponse response) {
        return options.enabled;
    }

    /**
     *
     * @param cookieValue plaintext cookie value
     * @param secret secret to act as crypto salt
     * @return encoded SHA-256 string of cookie value with secret
     */
    protected String encodeToken(String cookieValue, String secret) {
        final String s = String.format("%s:%s", cookieValue, secret);
        return DigestUtils.sha256Hex(s);
    }

    /**
     * Empty method to be overridden by subclass to add the CSRF cookie
     * to the response
     * @param cookieValue cookie value string
     * @param request HttpRequest
     * @param response HttpResponse
     */
    protected void setCookieValue(String cookieValue, HttpRequest request, HttpResponse response) {
    }

    /**
     * Empty method to be overridden by subclass to add the CSRF session attribute
     * to the response
     * @param sessionValue value to put into session
     * @param request HttpRequest
     * @param response HttpResponse
     */
    protected void setSessionValue(String sessionValue, HttpRequest request, HttpResponse response) {
    }

    /**
     * Callback that we invoke when CSRF is detected
     * @param request HttpRequest
     * @param response HttpResponse
     */
    protected abstract void onCsrfViolation(HttpRequest request, HttpResponse response);

    /**
     * Contract method to obtain the hashed value from the session or cookie
     * @param request HttpRequest
     * @return session or cookie value
     */
    protected abstract String getHashedValue(HttpRequest request);

//    static class CsrfOptions {
//        boolean enabled = false;
//        boolean secure = false;
//        String cookieName = "_csrf";
//        String requestHeaderKey = "x-csrf-token";
//        String responseHeaderKey = "x-csrf-token";
//        String cookieDomain = "";
//        int cookieLifetime = 43200;
//        List<String> deferredMethods = Arrays.asList("GET", "HEAD", "OPTIONS");
//        String secret = "";
//
//        @Override
//        public String toString() {
//            return "{" +
//                    "enabled=" + enabled +
//                    ", secure=" + secure +
//                    ", cookieName='" + cookieName + '\'' +
//                    ", requestHeaderKey='" + requestHeaderKey + '\'' +
//                    ", responseHeaderKey='" + responseHeaderKey + '\'' +
//                    ", cookieDomain='" + cookieDomain + '\'' +
//                    ", cookieLifetime=" + cookieLifetime +
//                    ", deferredMethods=" + deferredMethods.toString() +
//                    ", secret='******'" +
//                    '}';
//        }
//    }
}
