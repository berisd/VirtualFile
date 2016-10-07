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

import java.io.IOException;

public class FileNameFilter extends StringFilter {
    @Override
    protected String getValue(VirtualFile file) throws IOException {
        return file.getName();
    }
}
