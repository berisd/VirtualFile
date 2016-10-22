/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.VirtualFile;

public class FileSizeFilter extends DefaultFilter<Long> {
    @Override
    protected Long getValue(VirtualFile file) {
        return file.getSize();
    }
}
