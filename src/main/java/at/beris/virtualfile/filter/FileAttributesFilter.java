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
import at.beris.virtualfile.attribute.FileAttribute;

import java.util.Set;

public class FileAttributesFilter extends CollectionFilter<Set<FileAttribute>, FileAttribute> {
    @Override
    protected Set<FileAttribute> getValue(VirtualFile file) {
        return file.getAttributes();
    }
}
