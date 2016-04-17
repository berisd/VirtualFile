/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.File;

import java.io.IOException;

public class FileSizeFilter extends DefaultFilter<Long> {
    @Override
    protected Long getValue(File file) throws IOException {
        return file.getSize();
    }
}
