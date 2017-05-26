/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    public static final String EMPTY_STRING = "";

    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
    private static final int PAD_LIMIT = 8192;

    /**
     * <p>Splits the provided text into an array, separator specified.</p>
     * The separator is not included in the returned String array.
     *
     * @param str  the String to parse, may be null
     * @param separatorChar  the character used as the delimiter
     * @return an array of parsed Strings
     */
    public static String[] split(String str, char separatorChar) {
        if (str == null || str.length() == 0) {
            return new String[0];
        }

        final List<String> list = new ArrayList<>();
        int strlen = str.length();
        int startpos = 0;
        int i;

        for(i = 0; i < strlen; i++) {
            if (str.charAt(i) == separatorChar) {
                list.add(str.substring(startpos, i));
                startpos = ++i;
            }
        }

        list.add(str.substring(startpos, i > strlen ? strlen : i));

        return list.toArray(new String[list.size()]);
    }

    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Joins the elements of the provided {@code Iterable} into
     * a single String containing the provided elements.</p>
     *
     * <p>No delimiter is added before or after the list. Null objects or empty
     * strings within the iteration are represented by empty strings.</p>
     *
     * @param iterable  the {@code Iterable} providing the values to join together, may be null
     * @param separator  the separator character to use
     * @return the joined String, {@code null} if null iterator input
     */
    public static String join(final Iterable<?> iterable, final char separator) {
        if (iterable == null || ! iterable.iterator().hasNext()) {
            return EMPTY_STRING;
        }

        StringBuilder sb = new StringBuilder(256);
        for (Object o : iterable) {
            if (sb.length() > 0)
                sb.append(separator);
            if (o != null)
                sb.append(o);
        }

        return sb.toString();
    }

    public static String join(Object[] array, char separator, final int startIndex, final int endIndex) {
        if (array == null)
            return EMPTY_STRING;

        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY_STRING;
        }

        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }

        return buf.toString();
    }

    /**
     * <p>Repeat a String {@code repeat} times to form a
     * new String.</p>
     *
     * @param str  the String to repeat, may be null
     * @param repeat  number of times to repeat str, negative treated as zero
     * @return a new String consisting of the original String repeated,
     *  {@code null} if null String input
     */
    public static String repeat(final String str, final int repeat) {
        if (str == null || repeat <= 0)
            return EMPTY_STRING;

        final int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            sb.append(str);
        }

        return sb.toString();
    }

    public static String repeat(final char chr, final int repeat) {
        return repeat(String.valueOf(chr), repeat);
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String,.</p>
     *
     * @param str  the String to be trimmed, may be null
     * @return the trimmed string
     */
    public static String trim(final String str) {
        return str == null ? EMPTY_STRING : str.trim();
    }

    /**
     * <p>Right pad a String with a specified character.</p>
     *
     * <p>The String is padded to the size of {@code size}.</p>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return right padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return EMPTY_STRING;
        }

        if (size <= str.length()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(str);

        for (int i = 0; i < Math.abs(str.length() - size); i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }

    /**
     * <p>Left pad a String with a specified character.</p>
     *
     * @param str  the String to pad out, may be null
     * @param size  the size to pad to
     * @param padChar  the character to pad with
     * @return left padded String or original String if no padding is necessary,
     *  {@code null} if null String input
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return EMPTY_STRING;
        }

        if (size <= str.length()) {
            return str;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < Math.abs(str.length() - size); i++) {
            sb.append(padChar);
        }

        sb.append(str);

        return sb.toString();
    }

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     *
     * @param cs  the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

}
