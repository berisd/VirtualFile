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
        if (OS.contains("win"))
            return OsFamily.WINDOWS;
        else if (OS.contains("mac"))
            return OsFamily.MAC;
        else if (OS.contains("sunos"))
            return OsFamily.SOLARIS;
        else if (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0)
            return OsFamily.UNIX;
        else
            return OsFamily.UNKNOWN;
    }
}
