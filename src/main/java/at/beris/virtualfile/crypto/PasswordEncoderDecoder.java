/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.crypto;

import at.beris.virtualfile.util.CharUtils;
import at.beris.virtualfile.util.CryptoUtils;

import java.util.Base64;

public class PasswordEncoderDecoder implements EncoderDecoder {
    @Override
    public char[] encode(char[] decodedCharArray) {
        byte[] base64encodedBytes = Base64.getEncoder().encode(CharUtils.charArrayToByteArray(CryptoUtils.encodeRot32768(decodedCharArray)));
        //TODO To increase security function below should be used but String representation was not correct.
//        return CharUtils.byteArrayToCharArray(base64encodedBytes);
        return new String(base64encodedBytes).toCharArray();
    }

    @Override
    public char[] decode(char[] encodedCharArray) {
        //TODO To increase security function below should be used but String representation was not correct.
//        byte[] base64decodedBytes = Base64.getDecoder().decode(CharUtils.charArrayToByteArray(encodedCharArray));
        byte[] base64decodedBytes = Base64.getDecoder().decode(String.valueOf(encodedCharArray).getBytes());
        return CryptoUtils.decodeRot32768(CharUtils.byteArrayToCharArray(base64decodedBytes));
    }
}
