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

public class CryptoUtilsTest {

    @Test
    public void encodeRot32768() {
        char[] input = new char[]{'a', 'b', 'c', ' ', 'ö', 'Ä', 'Ü', 'ß'};
        char[] expected = new char[]{32865, 32866, 32867, 32800, 33014, 32964, 32988, 32991};
        Assert.assertArrayEquals(expected, CryptoUtils.encodeRot32768(input));
    }

    @Test
    public void decodeRot32768() {
        char[] input = new char[]{32865, 32866, 32867, 32800, 33014, 32964, 32988, 32991};
        char[] expected = new char[]{'a', 'b', 'c', ' ', 'ö', 'Ä', 'Ü', 'ß'};
        Assert.assertArrayEquals(expected, CryptoUtils.encodeRot32768(input));
    }

}