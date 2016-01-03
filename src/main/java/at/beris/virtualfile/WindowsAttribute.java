/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

public enum WindowsAttribute implements Attribute<WindowsAttribute> {
    READ("R", "Read"),
    WRITE("W", "Write"),
    EXECUTE("X", "Execute"),
    HIDDEN("H", "Hidden");

    private String shortName;
    private String longName;

    WindowsAttribute(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    @Override
    public String shortName() {
        return this.shortName;
    }

    @Override
    public String longName() {
        return this.longName;
    }
}
