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
 * Last Modified: 5/30/19 10:09 AM
 */

package io.injest.core.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferedResponseAdapter extends TextResponseAdapter {

    private transient InputStream inputStream;
    private transient long length = 0L;

    public void setBody(File file) throws IOException {
        this.inputStream = new FileInputStream(file);
        this.length = file.length();
    }

    @Override
    public void setBody(String s) {
        try {
            this.setBody(new File(s));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void setBody(ByteBuffer byteBuffer) {
        this.inputStream = new ByteArrayInputStream(byteBuffer.array());
    }

    InputStream getInputStream() {
        return inputStream;
    }

    long getStreamLength() {
        return length;
    }

    @Override
    public void clear() {
        super.clear();
        inputStream = null;
        length = 0L;
    }
}
