/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import java.util.List;

public interface VirtualArchive {
    void add(VirtualFile file);

    void delete(VirtualFile file);

    List<VirtualFile> extract(VirtualFile target);

}
