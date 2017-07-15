/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import static at.beris.virtualfile.util.CryptoUtils.CRYPTO_KEY_SYMBOLS;

public class CharUtils {

    /**
     * Generate a sequence of random characters.
     *
     * @param length number of characters
     * @return generated string
     */
    public static char[] generateCharSequence(int length) {
        char[] buffer = new char[length];

        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = CRYPTO_KEY_SYMBOLS[RandomUtils.generateRandomInt(CRYPTO_KEY_SYMBOLS.length)];
        }

        return buffer;
    }

    public static byte[] charArrayToByteArray(char[] chars) {
        byte[] b = new byte[chars.length << 1];
        CharBuffer charBuffer = ByteBuffer.wrap(b).asCharBuffer();
        for (int i = 0; i < chars.length; i++) {
            charBuffer.put(chars[i]);

        }
//        Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
        return b;
    }

    public static char[] byteArrayToCharArray(byte[] bytes) {
        CharBuffer charBuffer = ByteBuffer.wrap(bytes).asCharBuffer();

        char[] buf = new char[charBuffer.limit()];
        for (int i = 0; i < charBuffer.limit(); i++) {
            buf[i] = charBuffer.charAt(i);
        }
//        Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
        return buf;
    }

    public static boolean isEmpty(char[] charArray) {
        return charArray == null || charArray.length == 0;
    }

}
