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
 * Last Modified: 3/11/19 5:07 PM
 */

package io.injest.core.structs;

import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;

public class Packet {

    private boolean complete = false;
    private Deque<Integer> pointers = new LinkedList<>();
    private Deque<Byte> data = new LinkedList<>();

    public void writeString(String s) {
        byte[] bytes = s.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        addBytes(bb.array());
        pointers.offerLast(bytes.length);
    }

    public void writeInt(int i) {
        ByteBuffer bb = ByteBuffer.allocate(Integer.BYTES);
        bb.putInt(i);
        addBytes(bb.array());
        pointers.offerLast(Integer.BYTES);
    }

    public void writeLong(long l) {
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
        bb.putLong(l);
        addBytes(bb.array());
        pointers.offerLast(Long.BYTES);
    }

    public void writeFloat(float f) {
        ByteBuffer bb = ByteBuffer.allocate(Float.BYTES);
        bb.putFloat(f);
        addBytes(bb.array());
        pointers.offerLast(Float.BYTES);
    }

    public void writeDouble(double d) {
        ByteBuffer bb = ByteBuffer.allocate(Double.BYTES);
        bb.putDouble(d);
        addBytes(bb.array());
        pointers.offerLast(Double.BYTES);
    }

    public void writeChar(char c) {
        ByteBuffer bb = ByteBuffer.allocate(Character.BYTES);
        bb.putChar(c);
        addBytes(bb.array());
        pointers.offerLast(Character.BYTES);
    }

    public void writeBoolean(boolean b) {
        addBytes(new byte[] {(byte) (b ? 1 : 0)});
    }

    public String readString() {
        return new String(readNext());
    }

    public int readInt() {
        return ByteBuffer.wrap(readNext()).getInt();
    }

    public long readLong() {
        return ByteBuffer.wrap(readNext()).getLong();
    }

    public float readFloat() {
        return ByteBuffer.wrap(readNext()).getFloat();
    }

    public double readDouble() {
        return ByteBuffer.wrap(readNext()).getDouble();
    }

    public boolean readBoolean() {
        byte[] b = readNext();
        return (int) b[0] == 1;
    }

    private byte[] readNext() {
        if (complete) {
            int nextLength = pointers.pollFirst();
            byte[] bytes = new byte[nextLength];
            for (int i = 0; i < nextLength; i++)
                bytes[i] = data.pollFirst();
            return bytes;
        } else {
            throw new RuntimeException("Attempt to read from unlocked packet");
        }
    }

    public void lock() {
        complete = true;
    }

    private void addBytes(byte[] bytes) {
        if (complete)
            throw new RuntimeException("Attempt to write to a locked packet");
        for (byte b : bytes)
            data.add(b);
    }
}
