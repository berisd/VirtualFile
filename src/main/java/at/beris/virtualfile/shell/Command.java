/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.shell;

public enum Command {
    CD("Change to working directory or archive"),
    CON("Connect to URL"),
    DIS("Disconnect"),
    EMPTY(""),
    HELP("Display help"),
    LCD("Change to local directory or archive"),
    LLS("List local directory or archive"),
    LS("List directory"),
    PWD("print current working directory"),
    LPWD("print current local directory"),
    QUIT("Quit shell"),
    STAT("Display statistics");

    private String description;

    Command(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
