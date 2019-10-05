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
 * Last Modified: 5/31/19 5:00 PM
 */

package io.injest.core.http;

import io.injest.core.structs.Bundle;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import java.util.Stack;

/**
 * Abstraction layer around the response facet of HttpServerExchange
 * Adds intercept capability and provides delegate methods
 */
final public class HttpResponse implements HttpExchangeFacet {

    private final HttpExchange exchange;
    private final Stack<Adapter> adapterStack = new Stack<>();
    private String contentType;
    private int statusCode = 0;
    private boolean canIntercept = true;
    private boolean wasSuccessful = false;
    private Exception sendError;

    /**
     * Setup the HttpResponse delegate
     * @param exchange Undertow HttpServerExchange
     */
    HttpResponse(HttpExchange exchange) {
        this.exchange = exchange;
        setContentType(ContentType.getDefault());
    }

    /**
     * Gets the response status code
     * @return int status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the HTTP response status code
     * @param statusCode HTTP status code to set
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Copy the current status code to the exchange Status code
     */
    void finalizeStatusCode() {
        exchange.getNativeExchange().setStatusCode(statusCode);
    }

    /**
     * Sets the response content-type header
     * @param type ContentType enum
     */
    public void setContentType(String type) {
        this.contentType = type;
        putHeader(Headers.CONTENT_TYPE, type);
    }

    /**
     * Gets the currently set content type
     * @return content-type
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Add a header to the HTTP response
     * @param headerName HttpString value of header name
     * @param value header value
     */
    public void putHeader(HttpString headerName, String value) {
        exchange.getNativeExchange().getResponseHeaders().put(headerName, value);
    }

    /**
     * Sets a header to the HTTP response
     * @param headerName String value of header name
     * @param value header value
     */
    public void putHeader(String headerName, String value) {
        putHeader(new HttpString(headerName), value);
    }

    /**
     * Add cookie
     * @param cookie Cookie
     */
    public void addCookie(Cookie cookie) {
        exchange.getNativeExchange().setResponseCookie(cookie);
    }

    /**
     * Gets the top adapter from the adapter stack
     * @return response adapter
     */
    public Adapter getResponseAdapter() {
        return adapterStack.peek();
    }

    /**
     * Push an adapter onto the adapter stack
     * @param adapter response adapter
     */
    void setResponseAdapter(Adapter adapter) {
        adapterStack.push(adapter);
    }

    /**
     * Set successful or not
     * @param wasSuccessful if response was successful
     */
    void setSuccess(boolean wasSuccessful) {
        this.wasSuccessful = wasSuccessful;
    }

    /**
     * Ascertain whether or not the response was sent with success
     * @return true if successful, otherwise false
     */
    public boolean wasSuccessful() {
        return wasSuccessful;
    }

    /**
     * Set the IOException that may have occurred
     * @param e the exception
     */
    void setSendError(Exception e) {
        this.sendError = e;
    }

    /**
     * Get the exception that may have occurred
     * @return the exception
     */
    public Exception getSendError() {
        return sendError;
    }

    /**
     * Check if top adapter in the stack has a replacement.
     * If so, take that replacement and push onto the adapter stack
     */
    void updateAdapter() {
        Adapter current = adapterStack.peek();
        if (current.isReplaced()) {
            adapterStack.push(current.getReplacement());
        }
    }

    /**
     * Take (pop) the topmost adapter in the adapter stack
     * @return most recent adapter
     */
    Adapter takeAdapter() {
        return adapterStack.pop();
    }

    @Override
    public Handler getHandler() {
        return exchange.getCurrentHandler();
    }

    @Override
    public HttpServerExchange getExchange() {
        return exchange.getNativeExchange();
    }

    @Override
    public Bundle data() {
        return exchange.getExchangeData();
    }

    @Override
    public void endIntercepts() {
        this.canIntercept = false;
    }

    @Override
    public boolean canIntercept() {
        return canIntercept;
    }
}
