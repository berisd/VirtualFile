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
import at.beris.virtualfile.attribute.IAttribute;

import java.util.Set;

public class FileAttributesFilter extends CollectionFilter<Set<IAttribute>, IAttribute> {
    @Override
    protected Set<IAttribute> getValue(IFile file) {
        return file.getAttributes();
    }
}
