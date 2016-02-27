/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.attribute;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum PosixFilePermission implements IAttribute {
    GROUP_EXECUTE(java.nio.file.attribute.PosixFilePermission.GROUP_EXECUTE),
    GROUP_READ(java.nio.file.attribute.PosixFilePermission.GROUP_READ),
    GROUP_WRITE(java.nio.file.attribute.PosixFilePermission.GROUP_WRITE),
    OTHERS_EXECUTE(java.nio.file.attribute.PosixFilePermission.OTHERS_EXECUTE),
    OTHERS_READ(java.nio.file.attribute.PosixFilePermission.OTHERS_READ),
    OTHERS_WRITE(java.nio.file.attribute.PosixFilePermission.OTHERS_WRITE),
    OWNER_EXECUTE(java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE),
    OWNER_READ(java.nio.file.attribute.PosixFilePermission.OWNER_READ),
    OWNER_WRITE(java.nio.file.attribute.PosixFilePermission.OWNER_WRITE);

    private java.nio.file.attribute.PosixFilePermission nioPermission;

    private static Map<java.nio.file.attribute.PosixFilePermission, PosixFilePermission> permissionToNioMap;

    PosixFilePermission(java.nio.file.attribute.PosixFilePermission nioPermission) {
        this.nioPermission = nioPermission;
    }

    public java.nio.file.attribute.PosixFilePermission getNioPermission() {
        return nioPermission;
    }

    public static PosixFilePermission fromNioPermission(java.nio.file.attribute.PosixFilePermission nioPermission) {
        if (permissionToNioMap == null) {
            initMap();
        }
        return permissionToNioMap.get(nioPermission);
    }

    private static void initMap() {
        Map<java.nio.file.attribute.PosixFilePermission, PosixFilePermission> map = new HashMap<>();
        for (PosixFilePermission permission : values()) {
            map.put(permission.getNioPermission(), permission);
        }

        permissionToNioMap = Collections.unmodifiableMap(map);
    }
}
