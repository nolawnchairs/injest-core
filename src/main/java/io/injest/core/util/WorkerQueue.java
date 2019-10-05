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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class WorkerQueue {

    private static final WorkerQueue INSTANCE = new WorkerQueue();
    private final BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile boolean running = true;

    public static WorkerQueue getInstance() {
        return INSTANCE;
    }

    private WorkerQueue() {
        new Thread(this::start, "worker-delegate").start();
    }

    public void add(Runnable worker) {
        queue.add(worker);
    }

    public void kill() {
        running = false;
    }

    private void start() {
        Log.with(WorkerQueue.class).i("Started worker queue. Awaiting tasks");
        while (running) {
            try {
                Runnable runnable = queue.take();
                executorService.execute(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
