/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;

public class UrlUtils {
    public static Protocol getProtocol(URL url) {
        Protocol protocol = null;
        String protocolString = url.getProtocol();

        try {
            protocol = Protocol.valueOf(url.getProtocol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VirtualFileException("Unknown protocol: " + protocolString);
        }

        return protocol;
    }
}
