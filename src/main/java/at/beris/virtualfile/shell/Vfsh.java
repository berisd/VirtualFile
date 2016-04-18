/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.shell;

import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Vfsh {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Vfsh.class);

    public static void main(String args[]) {
        try {
            new Shell().run();
        }
        catch (IOException e) {
            LOGGER.error("Exception", e);
        }
    }
}

