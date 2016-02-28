/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.attribute.IAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;

import java.util.HashMap;
import java.util.Map;

public class Sftp {
    static final int S_IRUSR = 00400; // read by owner
    static final int S_IWUSR = 00200; // write by owner
    static final int S_IXUSR = 00100; // execute/search by owner

    static final int S_IRGRP = 00040; // read by group
    static final int S_IWGRP = 00020; // write by group
    static final int S_IXGRP = 00010; // execute/search by group

    static final int S_IROTH = 00004; // read by others
    static final int S_IWOTH = 00002; // write by others
    static final int S_IXOTH = 00001; // execute/search by others

    public static Map<Integer, IAttribute> permissionToAttributeMap = createPermissionToAttributeMap();

    private static HashMap<Integer, IAttribute> createPermissionToAttributeMap() {
        HashMap<Integer, IAttribute> map = new HashMap<>();
        map.put(S_IRUSR, PosixFilePermission.OWNER_READ);
        map.put(S_IWUSR, PosixFilePermission.OWNER_WRITE);
        map.put(S_IXUSR, PosixFilePermission.OWNER_EXECUTE);
        map.put(S_IRGRP, PosixFilePermission.GROUP_READ);
        map.put(S_IWGRP, PosixFilePermission.GROUP_WRITE);
        map.put(S_IXGRP, PosixFilePermission.GROUP_EXECUTE);
        map.put(S_IROTH, PosixFilePermission.OTHERS_READ);
        map.put(S_IWOTH, PosixFilePermission.OTHERS_WRITE);
        map.put(S_IXOTH, PosixFilePermission.OTHERS_EXECUTE);
        return map;
    }

    public static int getPermission(IAttribute attribute) {
        for (Map.Entry<Integer, IAttribute> entry : permissionToAttributeMap.entrySet()) {
            if (entry.getValue().equals(attribute))
                return entry.getKey();
        }
        return 0;
    }

    public static IAttribute getAttribute(int permission) {
        return permissionToAttributeMap.get(permission);
    }
}
