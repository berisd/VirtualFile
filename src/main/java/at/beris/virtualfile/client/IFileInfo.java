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

import java.nio.file.attribute.FileTime;
import java.util.Set;

public interface IFileInfo {
    String getPath();

    FileTime getLastModified();

    long getSize();

    boolean isDirectory();

    Set<IAttribute> getAttributes();
}
