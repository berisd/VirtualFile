/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.protocol;

public enum Protocol {
    FILE(-1), SFTP(22), FTP(21), HTTP(80), HTTPS(443);

    private int defaultPort;

    Protocol(int defaultPort) {
        this.defaultPort = defaultPort;
    }

    public int getDefaultPort() {
        return defaultPort;
    }
}
