/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.crypto;

import org.junit.Assert;
import org.junit.Test;

public class PasswordEncoderDecoderTest {

    private EncoderDecoder encoderDecoder = new PasswordEncoderDecoder();

    @Test
    public void encodeAndDecode() throws Exception {
        char[] expectedChars = new char[]{'a', 'b', 'c', 'ö', 'Ä', 'ß', '€'};
        char[] encodedChars = encoderDecoder.encode(expectedChars);
//        System.out.println(String.valueOf(encodedChars));
        char[] decodedChars = encoderDecoder.decode(encodedChars);
//        System.out.println(decodedChars);
        Assert.assertArrayEquals(expectedChars, decodedChars);
    }


}