/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import org.junit.Assert;
import org.junit.Test;

import static at.beris.virtualfile.util.CryptoUtils.CRYPTO_KEY_SYMBOLS;

public class CharUtilsTest {
    private static final String TEST_STRING = "Hello World öäß^";

    private static final byte[] TEST_BYTES = new byte[]{0, 72, 0, 101, 0, 108, 0, 108, 0, 111, 0, 32, 0, 87, 0, 111, 0, 114, 0, 108, 0, 100, 0, 32, 0, -10, 0, -28, 0, -33, 0, 94};

    @Test
    public void generateRandomCharSequence() {
        char[] charSequence = CharUtils.generateCharSequence(20);
        Assert.assertEquals(20, charSequence.length);

        for (char ch : charSequence) {
            Assert.assertTrue(isCharInCryptoKeySymbolTable(ch));
        }
    }

    @Test
    public void charArraytoByteArray() {
        Assert.assertArrayEquals(TEST_BYTES, CharUtils.charArrayToByteArray(TEST_STRING.toCharArray()));
    }

    @Test
    public void bytesArraytoCharArray() {
        Assert.assertEquals(TEST_STRING, new String(CharUtils.byteArrayToCharArray(TEST_BYTES)));
    }

    @Test
    public void charArraytoByteArrayAndReverse() {
        byte[] bytes = CharUtils.charArrayToByteArray(TEST_STRING.toCharArray());
        Assert.assertEquals(TEST_STRING, new String(CharUtils.byteArrayToCharArray(bytes)));
    }

    private boolean isCharInCryptoKeySymbolTable(char ch) {
        for (char symbol : CRYPTO_KEY_SYMBOLS) {
            if (ch == symbol)
                return true;
        }
        return false;
    }
}