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

public interface IFilter<T> {
    IFilter and(IFilter filter);

    IFilter andNot(IFilter filter);

    Object clone();

    IFilter equalTo(T value);

    boolean filter(IFile file);

    IFilter not();

    IFilter or(IFilter filter);

    IFilter orNot(IFilter filter);
}
