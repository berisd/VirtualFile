/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

public class CryptoUtils {
    public static final char[] CRYPTO_KEY_SYMBOLS;

    static {
        StringBuilder stringBuilder = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++) {
            stringBuilder.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            stringBuilder.append(ch);
            stringBuilder.append(Character.toUpperCase(ch));
        }

        for (char ch : new char[]{',', ';', '.', ':', '_', '-', '!', '§', '$', '%', '&', '/', '(', ')', '='}) {
            stringBuilder.append(ch);
        }

        CRYPTO_KEY_SYMBOLS = stringBuilder.toString().toCharArray();
    }

    public static char[] encodeRot32768(char[] decodedCharArray) {
        return rot32768(decodedCharArray);
    }

    public static char[] decodeRot32768(char[] encodedCharArray) {
        return rot32768(encodedCharArray);
    }

    private static char[] rot32768(char[] value) {
        int length = value.length;
        char[] result = new char[value.length];

        for (int i = 0; i < length; i++) {
            char c = value[i];
            c ^= 0x8000;
            result[i] = c;
        }

        return result;
    }

}
