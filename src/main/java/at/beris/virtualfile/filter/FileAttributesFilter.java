/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.Attribute;
import at.beris.virtualfile.IFile;

import java.util.Set;

public class FileAttributesFilter extends CollectionFilter<Set<Attribute>, Attribute> {
    @Override
    protected Set<Attribute> getValue(IFile file) {
        return file.getAttributes();
    }
}