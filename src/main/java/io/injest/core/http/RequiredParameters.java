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
 * Last Modified: 5/28/19 11:48 AM
 */

package io.injest.core.http;

import io.undertow.util.AttachmentKey;
import java.util.Arrays;
import java.util.HashSet;

public class RequiredParameters {

    public static final AttachmentKey<RequiredParameters> ATTACHMENT_KEY = AttachmentKey.create(RequiredParameters.class);

    private final HashSet<String> values;

    public RequiredParameters(String[] values) {
        this.values = new HashSet<>();
        this.values.addAll(Arrays.asList(values));
    }

    public HashSet<String> getValues() {
        return values;
    }
}
