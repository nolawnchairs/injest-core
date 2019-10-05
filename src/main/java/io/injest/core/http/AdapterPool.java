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
 * Last Modified: 5/31/19 4:17 PM
 */

package io.injest.core.http;

import io.injest.core.annotations.handlers.ErrorHandler;
import io.undertow.UndertowMessages;
import io.undertow.util.ObjectPool;
import io.undertow.util.PooledObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AdapterPool<A extends Adapter> implements ObjectPool<A> {

    private final Handler<A> handler;
    private final LinkedBlockingDeque<A> pool = new LinkedBlockingDeque<>(8);
    private final AtomicInteger overflowCounter = new AtomicInteger(0);

    AdapterPool(Handler<A> handler) {
        this.handler = handler;
        A preCached = this.supply();
        if (preCached != null)
            this.pool.offer(preCached);
    }

    @Override
    public PooledObject<A> allocate() {
        A adapter = pool.poll();
        if (adapter == null) {
            adapter = this.supply();
            if (adapter == null)
                return null;
            pool.offer(adapter);
        }
        return new PooledAdapter<>(adapter, this);
    }

    @SuppressWarnings("unchecked")
    private A supply() {
        try {
            if (handler.getClass().getSuperclass().isAnnotationPresent(ErrorHandler.class)) {
                return null;
            } else {
                final Class<A> adapterClass = (Class<A>) ((ParameterizedType) handler.getClass()
                        .getGenericSuperclass()).getActualTypeArguments()[0];
                final Constructor<A> rConstructor = adapterClass.getDeclaredConstructor();
                rConstructor.setAccessible(true);
                return rConstructor.newInstance();
            }
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InvocationTargetException
                | InstantiationException e) {
            return null;
        }
    }

    void recycle(A adapter) {
        adapter.clear();
    }

    private static final class PooledAdapter<T extends Adapter> implements PooledObject<T> {

        private static final AtomicIntegerFieldUpdater<PooledAdapter> closedUpdater =
                AtomicIntegerFieldUpdater.newUpdater(PooledAdapter.class, "closed");
        private volatile int closed;
        private final T object;
        private final AdapterPool<T> objectPool;

        PooledAdapter(T object, AdapterPool<T> objectPool) {
            this.object = object;
            this.objectPool = objectPool;
        }

        @Override
        public T getObject() {
            if (closedUpdater.get(this) != 0)
                throw UndertowMessages.MESSAGES.objectIsClosed();
            return object;
        }

        @Override
        public void close() {
            if (closedUpdater.compareAndSet(this, 0, 1)) {
                objectPool.recycle(object);
                if (!objectPool.pool.offer(object)) {
                    // nothing to do. queue is full of reset adapters
                    objectPool.overflowCounter.incrementAndGet();
                }
            }
        }
    }
}
