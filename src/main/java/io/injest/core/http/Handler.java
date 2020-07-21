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
 * Last Modified: 5/31/19 4:15 PM
 */

package io.injest.core.http;

import io.injest.core.annotations.directives.Blocking;
import io.injest.core.boot.ApplicationState;
import io.injest.core.util.Exceptions;
import io.injest.core.util.WorkerQueue;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AbstractAttachable;
import io.undertow.util.AttachmentKey;
import io.undertow.util.AttachmentList;
import io.undertow.util.Headers;
import io.undertow.util.PooledObject;
import java.io.IOException;
import java.util.Map;

public abstract class Handler<R extends Adapter> extends AbstractAttachable implements HttpHandler {

    private final AdapterPool<R> adapterPool;

    protected Handler() {
        this.adapterPool = new AdapterPool<>(this);
    }

    /**
     * Determine if this handler has been dispatched to
     * a blocking worker thread
     *
     * @return if is off the IO thread
     */
    boolean isBlocking() {
        return getAttachment(Blocking.ATTACHMENT_KEY) != null;
    }

    /**
     * Main HTTP request handler
     *
     * @param serverExchange Undertow HttpServerExchange instance
     * @throws Exception catch anything...
     */
    @Override
    final public void handleRequest(HttpServerExchange serverExchange) throws Exception {

        final boolean isIoThread = serverExchange.isInIoThread();
        final boolean hasContentLength = serverExchange.getRequestHeaders().contains(Headers.CONTENT_LENGTH);

        if (isBlocking() && isIoThread) {
            serverExchange.dispatch(this);
            return;
        }

        if (hasContentLength && isIoThread) {
            serverExchange.dispatch(this);
            return;
        }

        // create exchange
        final HttpExchange exchange = new HttpExchange(serverExchange, this);

        // get response adapter from pool
        final PooledObject<R> pooledAdapter = adapterPool.allocate();

        // if an adapter could not be instantiated, throw
        if (pooledAdapter == null)
            throw Exceptions.nullAdapterEncountered(getClass().getName());

        // get adapter from pooled object
        final R responseAdapter = pooledAdapter.getObject();

        // run handler instance
        final HandlerInstance<R> instance = new HandlerInstance<>(this, exchange, responseAdapter);
        instance.invoke();

        // return adapter to the pool
        pooledAdapter.close();
    }

    /**
     * Dispatch a worker thread to be executed in the background
     *
     * @param runnable Runnable object to run
     */
    final protected void dispatchWorker(Runnable runnable) {
        WorkerQueue.getInstance().add(runnable);
    }


    /**
     * onRequestCreated - called when exchange request data is ready
     *
     * @param request  request
     * @param response response
     */
    protected void onRequestCreated(HttpRequest request, HttpResponse response) {
    }

    /**
     * Run the main logic circuit of this request
     *
     * @param request request
     * @param adapter response
     * @return status code for this request
     * @throws Exception thrown if an error occurred
     */
    protected abstract int handle(HttpRequest request, R adapter) throws Exception;

    /**
     * Called just before the response sends. Useful for cleanup
     *
     * @param request  request
     * @param response response
     */
    protected void onResponseReady(HttpRequest request, HttpResponse response) {
    }

    /**
     * Called when the response was sent successfully
     *
     * @param request  request
     * @param response response
     */
    protected void onResponseSent(HttpRequest request, HttpResponse response) {
    }

    /**
     * Called if there was an IO error that caused the request to fail
     *
     * @param request  request
     * @param response response
     * @param e        IOException that was thrown
     */
    protected void onResponseError(HttpRequest request, HttpResponse response, IOException e) {
    }

    void onExchangeFinished() {

    }


    @Override
    public <T> T putAttachment(AttachmentKey<T> key, T value) {
        if (ApplicationState.getState() == ApplicationState.State.RUNNING)
            throw Exceptions.attachingAfterBootstrap();
        return super.putAttachment(key, value);
    }

    @Override
    protected Map<AttachmentKey<?>, Object> createAttachmentMap() {
        if (ApplicationState.getState() == ApplicationState.State.RUNNING)
            throw Exceptions.attachingAfterBootstrap();
        return super.createAttachmentMap();
    }

    @Override
    public <T> void addToAttachmentList(AttachmentKey<AttachmentList<T>> key, T value) {
        if (ApplicationState.getState() == ApplicationState.State.RUNNING)
            throw Exceptions.attachingAfterBootstrap();
        super.addToAttachmentList(key, value);
    }
}
