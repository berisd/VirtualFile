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

public interface Filter<T> {
    Filter and(Filter filter);

    Filter andNot(Filter filter);

    Object clone();

    Filter equalTo(T value);

    boolean filter(VirtualFile file);

    Filter not();

    Filter or(Filter filter);

    Filter orNot(Filter filter);
}
