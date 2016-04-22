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
    CD("Change to directory or archive", new int[]{1}),
    CON("Connect to URL", new int[]{1}),
    DIS("Disconnect", new int[]{0}),
    GET("Get file from URL", new int[]{1}),
    HELP("Display help", new int[]{0}),
    LCD("Change to local directory or archive", new int[]{1}),
    LLS("List local directory or archive", new int[]{0}),
    LRM("Remove local file", new int[]{1}),
    LS("List directory", new int[]{0}),
    PUT("Put file to url", new int[]{1}),
    PWD("print current directory", new int[]{0}),
    LPWD("print current local directory", new int[]{0}),
    QUIT("Quit shell", new int[]{0}),
    RM("Remove file", new int[]{1}),
    STAT("Display statistics", new int[]{0}),
    UNKNOWN("", new int[]{0});

    private String description;
    private int[] argumentCounts;

    Command(String description, int[] argumentCounts) {
        this.description = description;
        this.argumentCounts = argumentCounts;
    }

    public String getDescription() {
        return description;
    }

    public int[] getArgumentCounts() {
        return argumentCounts;
    }
}
