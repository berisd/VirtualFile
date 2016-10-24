/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import java.time.Instant;

/**
 * Representation of Entry inside an archive
 */
public interface VirtualArchiveEntry {
    String getName();

    void setName(String name);

    String getPath();

    void setPath(String path);

    Instant getLastModified();

    void setLastModified(Instant lastModified);

    long getSize();

    void setSize(long size);

    boolean isDirectory();

    void setDirectory(boolean directory);
}
