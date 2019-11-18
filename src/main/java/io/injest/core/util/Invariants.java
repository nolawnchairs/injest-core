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
 * Last Modified: 11/18/19 12:36 PM
 */

package io.injest.core.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

public class Invariants {

    public static Result inspectAll(Invariant... invariants) {
        final Result result = new Result();
        for (Invariant i : invariants) {
            if (!i.isSatisfied()) {
                result.errors.add(i.getErrorMessage());
            }
        }
        return result;
    }

    public static InspectionQueue newInspectionQueue() {
        return new InspectionQueue();
    }

    public static class InspectionQueue {

        final Queue<Supplier<Invariant>> queue = new LinkedList<>();

        public InspectionQueue add(Supplier<Invariant> inspector) {
            this.queue.add(inspector);
            return this;
        }

        public Result runInspection() {
            final Result result = new Result();
            for (Supplier<Invariant> invariantSupplier : queue) {
                try {
                    Invariant i = invariantSupplier.get();
                    if (!i.isSatisfied()) {
                        result.errors.add(i.getErrorMessage());
                    }
                } catch (Exception e) {
                    result.errors.add("Invariant supplier failed, considering as failure");
                }
                if (!result.passes())
                    return result;
            }
            return result;
        }
    }

    public static class Result {
        Deque<String> errors = new ArrayDeque<>();

        public boolean passes() {
            return errors.size() == 0;
        }

        public String getFirstError() {
            if (errors.size() > 0)
                return errors.getFirst();
            return null;
        }

        public String getLastError() {
            if (errors.size() > 0)
                return errors.getLast();
            return null;
        }

        public String[] getAllErrors() {
            String[] e = new String[errors.size()];
            return errors.toArray(e);
        }
    }
}
