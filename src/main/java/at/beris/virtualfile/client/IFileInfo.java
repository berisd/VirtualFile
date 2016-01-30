/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.Attribute;

import java.util.Date;
import java.util.Set;

public interface IFileInfo {
    String getPath();

    Date getLastModified();

    long getSize();

    boolean isDirectory();

    Set<Attribute> getAttributes();
}
