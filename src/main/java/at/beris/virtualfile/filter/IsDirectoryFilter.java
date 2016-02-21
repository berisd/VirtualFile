/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.IFile;

public class IsDirectoryFilter extends DefaultFilter<Boolean> {
    @Override
    protected Boolean getValue(IFile file) {
        return file.isDirectory();
    }
}