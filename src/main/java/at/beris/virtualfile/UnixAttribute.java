/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

public enum UnixAttribute implements Attribute<UnixAttribute> {

    S_IRUSR("R", "Owner Read", 00400),
    S_IWUSR("W", "Owner Write", 00200),
    S_ISUID("S", "Set user ID on execution", 04000),
    S_IXUSR("X", "Owner Execute", 00100),
    S_IRGRP("R", "Group Read", 00040),
    S_IWGRP("W", "Group Write", 00020),
    S_ISGID("S", "Set group ID on execution", 02000),
    S_IXGRP("X", "Group Execute", 00010),
    S_IROTH("R", "Others Read", 00004),
    S_IWOTH("W", "Others Write", 00002),
    S_IXOTH("X", "Others Execute", 00001);

    private String shortName;
    private String longName;
    private int value;

    UnixAttribute(String shortName, String longName, int value) {
        this.shortName = shortName;
        this.longName = longName;
        this.value = value;
    }

    @Override
    public String shortName() {
        return this.shortName;
    }

    @Override
    public String longName() {
        return this.longName;
    }

    public int getValue() {
        return this.value;
    }
}
