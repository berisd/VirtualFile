/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.crypto;

public interface EncoderDecoder {
    char[] encode(char[] decodedCharArray);

    char[] decode(char[] encodedCharArray);
}
