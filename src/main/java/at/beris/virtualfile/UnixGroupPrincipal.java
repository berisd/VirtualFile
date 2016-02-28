/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import javax.security.auth.Subject;
import java.nio.file.attribute.GroupPrincipal;

public class UnixGroupPrincipal implements GroupPrincipal {
    Integer gid;

    public UnixGroupPrincipal(Integer gid) {
        this.gid = gid;
    }

    public Integer getGid() {
        return gid;
    }

    @Override
    public String getName() {
        return gid.toString();
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
