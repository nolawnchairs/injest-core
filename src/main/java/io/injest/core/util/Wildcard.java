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

/**
 * Checks whether a string matches a given wildcard pattern.
 * Possible patterns allow to match single characters ('?') or any count of
 * characters ('*'). Wildcard characters can be escaped (by an '\').
 * <p>
 * This method uses recursive matching, as in linux or windows. regexp works the same.
 * This method is very fast, comparing to similar implementations.
 */
public class Wildcard {

    /**
     * Checks whether a string matches a given wildcard pattern.
     *
     * @param specimen  input string
     * @param pattern pattern to match
     * @return <code>true</code> if string matches the pattern, otherwise <code>false</code>
     */
    public static boolean match(String specimen, String pattern) {
        return match(specimen, pattern, 0, 0);
    }

    /**
     * Checks if two strings are equals or if they {@link #match(String, String)}.
     * Useful for cases when matching a lot of equal strings and speed is important.
     */
    public static boolean equalsOrMatch(String specimen, String pattern) {
        return specimen.equals(pattern) || match(specimen, pattern, 0, 0);
    }


    /**
     * Internal matching recursive function.
     */
    private static boolean match(String specimen, String pattern, int sIndex, int pIndex) {
        if (specimen == null)
            return false;

        int pLen = pattern.length();
        if (pLen == 1) {
            if (pattern.charAt(0) == '*') {     // speed-up
                return true;
            }
        }
        int sLen = specimen.length();
        boolean nextIsNotWildcard = false;

        while (true) {
            // check if end of string and/or pattern occurred
            if (sIndex >= sLen) {   // end of string still may have pending '*' in pattern
                while ((pIndex < pLen) && (pattern.charAt(pIndex) == '*')) {
                    pIndex++;
                }
                return pIndex >= pLen;
            }
            if (pIndex >= pLen) {         // end of pattern, but not end of the string
                return false;
            }
            char p = pattern.charAt(pIndex);    // pattern char

            // perform logic
            if (!nextIsNotWildcard) {

                if (p == '\\') {
                    pIndex++;
                    nextIsNotWildcard =  true;
                    continue;
                }
                if (p == '?') {
                    sIndex++; pIndex++;
                    continue;
                }
                if (p == '*') {
                    char pnext = 0;           // next pattern char
                    if (pIndex + 1 < pLen) {
                        pnext = pattern.charAt(pIndex + 1);
                    }
                    if (pnext == '*') {         // double '*' have the same effect as one '*'
                        pIndex++;
                        continue;
                    }
                    int i;
                    pIndex++;

                    // find recursively if there is any substring from the end of the
                    // line that matches the rest of the pattern !!!
                    for (i = specimen.length(); i >= sIndex; i--) {
                        if (match(specimen, pattern, i, pIndex)) {
                            return true;
                        }
                    }
                    return false;
                }
            } else {
                nextIsNotWildcard = false;
            }

            // check if pattern char and string char are equals
            if (p != specimen.charAt(sIndex)) {
                return false;
            }

            // everything matches for now, continue
            sIndex++; pIndex++;
        }
    }


    /**
     * Matches string to at least one pattern.
     * Returns index of matched pattern, or <code>-1</code> otherwise.
     * @see #match(String, String)
     */
    public static int matchOne(String src, String[] patterns) {
        for (int i = 0; i < patterns.length; i++) {
            if (match(src, patterns[i])) {
                return i;
            }
        }
        return -1;
    }

}