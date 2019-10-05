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
 * Last Modified: 6/25/19 9:54 PM
 */

package io.injest.core.boot;

import io.injest.core.util.Exceptions;
import io.injest.core.util.Log;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages the boot sequence of non-prioritized Bootable
 * classes. Scoped to io.injest.core.boot package only
 */
final public class BootManager {

    static final BootManager INSTANCE = new BootManager();
    private static Log log = Log.with(BootManager.class);

    private volatile boolean hasCompleted = false;
    private final TreeMap<Integer, Bootable> preScanBootables = new TreeMap<>();
    private final TreeMap<Integer, Bootable> postScanBootables = new TreeMap<>();
    private final HashMap<Class<? extends Annotation>, AnnotationHandler<?>> customAnnotationHandlers = new HashMap<>();
    private final AtomicInteger bootCount = new AtomicInteger(0);

    /**
     * Adds a standard Bootable to the queue for concurrent post-scan invocation
     * @param bootable Bootable
     */
    void add(Bootable bootable) {
        if (hasCompleted)
            throw Exceptions.bootablesAlreadyInvoked(bootable.getClass().getName());
        int next = findNextOrder(postScanBootables.descendingKeySet());
        postScanBootables.put(next, bootable);
    }

    /**
     * Adds a prioritized Bootable to the queue for synchronous pre-scan invocation
     * @param bootable Bootable
     * @param priority boot priority, smaller numbers have higher priority
     */
    void add(Bootable bootable, int priority) {
        if (hasCompleted)
            throw Exceptions.bootablesAlreadyInvoked(bootable.getClass().getName());
        int order = ensureUniqueOrder(preScanBootables.descendingKeySet(), priority);
        preScanBootables.put(order, bootable);
    }

    /**
     * Invoke Bootables that are scheduled to be run before package scanning
     * (e.g. before handler creation). These will be used to obtain values from
     * I/O operations where we can load any data dependencies needed for handler
     * creation.
     *
     * These are all executed on the main thread sequentially and synchronously,
     * with blocking to ensure all tasks are completed before continuing with
     * handler creation
     */
    void invokeBeforeScan() {
        for (Bootable bootable : preScanBootables.values()) {
            log.i(String.format(
                    "Invoking pre-scan Bootable [%s]",
                    bootable.getClass().getName()));
            bootable.onBoot(); // <-- blocking operation
        }
    }

    /**
     * Invoke Bootables that are scheduled to be run after package scanning, and
     * where boot order is not a concern. Run these each in a separate thread
     */
    void invokePostScan(final Runnable completionListener) throws InterruptedException {
        bootCount.set(postScanBootables.size());
        final ExecutorService executor = Executors.newFixedThreadPool(4);
        for (Bootable bootable : postScanBootables.values()) {
            executor.submit(bootable::onBoot);
            log.i(String.format("Invoking bootable [%s]", bootable.getClass().getName()));
        }

        // Shut down executor and wait for all tasks to complete
        executor.shutdown();
        executor.awaitTermination(30000, TimeUnit.MILLISECONDS);

        log.i("All Bootables invoked.");

        // Announce that bootables have all completed
        hasCompleted = true;
        completionListener.run();
    }

    /**
     * Get the next integer in order of the descending key set
     * @param set Key set
     * @return next integer
     */
    private int findNextOrder(NavigableSet<Integer> set) {
        int next = set.size() == 0 ? 0 : set.first();
        return ++next;
    }

    /**
     * Ensure that the priority value is unique to the set - recursive check
     * @param set Key set
     * @param candidate priority
     * @return unique priority value
     */
    private int ensureUniqueOrder(NavigableSet<Integer> set, int candidate) {
        if (set.contains(candidate))
            return ensureUniqueOrder(set, candidate + 1);
        return candidate;
    }

    public HashMap<Class<? extends Annotation>, AnnotationHandler<?>> getCustomAnnotationHandlers() {
        return customAnnotationHandlers;
    }

    public static <T extends Annotation> void addCustomAnnotationHandler(Class<T> annotation, AnnotationHandler<T> handler) {
        INSTANCE.customAnnotationHandlers.put(annotation, handler);
    }
}
