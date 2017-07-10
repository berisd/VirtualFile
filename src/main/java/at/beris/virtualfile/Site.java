/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.protocol.Protocol;

public class Site {

    private Protocol protocol;

    private String name;

    private int timeout;

    private String username;

    private String passwordReference;

    private String hostname;

    private int port;

}
