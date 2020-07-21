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

package io.injest.core.http;

import io.injest.core.annotations.directives.Produces;
import io.injest.core.boot.ConfigKeys;
import io.injest.core.boot.StaticConfig;
import io.injest.core.res.ResourceValues;
import io.injest.core.util.Log;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import java.io.IOException;
import java.nio.charset.Charset;

class HandlerInstance<R extends Adapter> implements IoCallback {

    private final Handler<R> handler;
    private final HttpServerExchange nativeExchange;
    private final HttpExchange exchange;
    private final Sender responseSender;
    private final HttpRequest request;
    private final HttpResponse response;
    private final R definedAdapter;
    private final HeaderMap responseHeaders;
    private final Charset charset;
    private final String contentType;
    private final StaticConfig staticConfig = StaticConfig.getInstance();

    /**
     * Wrapper around handler for individual HTTP requests
     *
     * @param handler  handler for the request
     * @param exchange native Undertow exchange object
     * @param adapter  definedAdapter assigned to the handler
     */
    HandlerInstance(Handler<R> handler, HttpExchange exchange, R adapter) {
        this.handler = handler;
        this.exchange = exchange;
        this.definedAdapter = adapter;
        this.nativeExchange = exchange.getNativeExchange();
        this.responseSender = this.nativeExchange.getResponseSender();
        this.responseHeaders = this.nativeExchange.getResponseHeaders();
        this.request = exchange.getRequest();
        this.response = exchange.getResponse();
        this.contentType = findContentType();
        this.charset = Charset.forName(staticConfig.getString(ConfigKeys.RESPONSE_CHARSET).orElse("UTF-8"));
    }

    /**
     * Process the request
     *
     * @throws Exception thrown exception
     */
    void invoke() throws Exception {

        if (definedAdapter != null) {

            // add definedAdapter to the response
            response.setResponseAdapter(definedAdapter);

            // set request content type from definedAdapter @Produces annotation
            response.setContentType(contentType);

            // request is ready to delegate data
            handler.onRequestCreated(request, response);

            // apply request interceptors for this request
            Interceptors.invokeRequestInterceptors(request, response);

            // create handler processor and ascertain tentative response state
            final HandlerProcessor processor = HandlerProcessorFactory.from(this);
            final ResponseState responseState = processor.processRequest();

            // set status code to that of response state
            response.setStatusCode(responseState.statusCode);

            // replace defined adapter if processing created one
            response.updateAdapter();

            // apply response interceptors for this request
            Interceptors.invokeResponseInterceptors(request, response);

            // replace adapter if response interceptors created a replacement
            response.updateAdapter();

            // response is ready to send
            handler.onResponseReady(request, response);

            // get the response string for this request
            final ResponseBody responseBody = processor.createResponseBody(responseState, contentType);

            // if response body is awaiting dispatch, send the response
            if (responseBody.isWaiting()) {
                response.finalizeStatusCode();
                responseHeaders.put(Headers.STATUS, response.getStatusCode());
                responseHeaders.put(Headers.CONTENT_TYPE, response.getContentType());
                responseSender.send(responseBody.toString(), charset);
                responseSender.close(this);
            }

        } else {
            nativeExchange.setStatusCode(500);
            responseHeaders.put(Headers.STATUS, 500);
            responseHeaders.put(Headers.CONTENT_TYPE, response.getContentType());
            responseSender.send("Invalid Adapter", charset);
            responseSender.close(this);
            logHandlerError(ResourceValues.getExceptionMessage(
                    "nullAdapterEncountered"), getClass().getName());
        }
    }

    /**
     * Output a logging message
     *
     * @param message Message to print
     * @param args    Optional arguments for message String
     */
    private void logHandlerError(String message, Object... args) {
        Log.with(HandlerInstance.class).e(String.format(message, args));
    }

    /**
     * Determine the content-type for this request's response from
     * reading the Produces annotation of the Handler's implementing
     * class. Defaults to JSON (since this is a REST service)
     *
     * @return Content-Type for the response
     */
    private String findContentType() {
        final Class implementingClass = handler.getClass();
        if (implementingClass.isAnnotationPresent(Produces.class)) {
            Produces produces = (Produces) implementingClass.getAnnotation(Produces.class);
            return produces.value();
        } else {
            return ContentType.getDefault();
        }
    }

    Adapter getAdapter() {
        return definedAdapter;
    }

    HttpExchange getHttpExchange() {
        return exchange;
    }

    Handler getHandler() {
        return handler;
    }

    @Override
    public void onComplete(HttpServerExchange httpServerExchange, Sender sender) {
        response.setSuccess(true);
        handler.onResponseSent(request, response);
        Interceptors.invokeEndingInterceptors(request, response);
    }

    @Override
    public void onException(HttpServerExchange httpServerExchange, Sender sender, IOException e) {
        response.setSuccess(false);
        response.setSendError(e);
        handler.onResponseError(request, response, e);
        Interceptors.invokeEndingInterceptors(request, response);
    }
}
