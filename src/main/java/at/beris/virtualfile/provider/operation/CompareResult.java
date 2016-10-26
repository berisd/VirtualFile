/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

public class CompareResult {
    private boolean isEqual;

    public boolean isEqual() {
        return isEqual;
    }

    //TODO Implement Differences. Left and Right side. what has been added, deleted, modified.
}
