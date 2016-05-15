/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import javax.security.auth.Subject;
import java.nio.file.attribute.UserPrincipal;

public class UnixUserPrincipal implements UserPrincipal {
    Integer uid;
    Integer gid;

    String userName;
    String groupName;

    public UnixUserPrincipal(Integer uid, Integer gid) {
        this.uid = uid;
        this.gid = gid;
    }

    public UnixUserPrincipal(String userName, String groupName) {
        this.userName = userName;
        this.groupName = groupName;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    @Override
    public String getName() {
        return userName != null ? userName : uid.toString();
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
