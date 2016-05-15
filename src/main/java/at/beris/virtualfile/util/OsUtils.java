/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.os.OsFamily;

public class OsUtils {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static OsFamily detectOSFamily() {
        if (OS.indexOf("win") >= 0)
            return OsFamily.WINDOWS;
        else if (OS.indexOf("mac") >= 0)
            return OsFamily.MAC;
        else if (OS.indexOf("sunos") >= 0)
            return OsFamily.SOLARIS;
        else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0)
            return OsFamily.UNIX;
        else
            return OsFamily.UNKNOWN;
    }
}
