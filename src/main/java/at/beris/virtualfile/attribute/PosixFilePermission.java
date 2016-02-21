/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.attribute;

public enum PosixFilePermission implements IAttribute {
    GROUP_EXECUTE,
    GROUP_READ,
    GROUP_WRITE,
    OTHERS_EXECUTE,
    OTHERS_READ,
    OTHERS_WRITE,
    OWNER_EXECUTE,
    OWNER_READ,
    OWNER_WRITE,
}
