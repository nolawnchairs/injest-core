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

import java.util.Random;

public class RandomString {

    public static final int STRING_NUMERIC = 0;
    public static final int STRING_ALPHA = 1;
    public static final int STRING_ALPHANUMERIC = 2;
    public static final int STRING_HEXADECIMAL = 3;
    public static final int STRING_B64 = 4;

    private static char[][] chars;

    static {
        chars = new char[][] {
                "0123456789".toCharArray(),
                "abcdefghijklmnopqrstuvwxyz".toCharArray(),
                "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray(),
                "0123456789abcdef".toCharArray(),
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+/".toCharArray()
        };
    }

    private int mode;
    private int length;

    public RandomString(int mode, int length) {
        this.mode = mode;
        this.length = length;
    }

    public static RandomString numeric(int length) {
        return new RandomString(STRING_NUMERIC, length);
    }

    public static RandomString alpha(int length) {
        return new RandomString(STRING_ALPHA, length);
    }

    public static RandomString alphaNumeric(int length) {
        return new RandomString(STRING_ALPHANUMERIC, length);
    }

    public static RandomString hex(int length) {
        return new RandomString(STRING_HEXADECIMAL, length);
    }

    public static RandomString base64(int length) {
        return new RandomString(STRING_B64, length);
    }

    public String generate() {
        char[] ca = chars[mode];
        StringBuilder string = new StringBuilder();
        Random random = new Random();
        while (string.length() < length) {
            string.append(ca[random.nextInt(ca.length)]);
        }
        return string.toString();
    }

    public String generateUppercase() {
        return generate().toUpperCase();
    }

    public String generateLowercase() {
        return generate().toLowerCase();
    }

    @Override
    public String toString() {
        return generate();
    }
}
