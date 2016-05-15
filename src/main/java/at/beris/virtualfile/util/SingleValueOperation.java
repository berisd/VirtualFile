/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import java.io.IOException;

public interface SingleValueOperation<O, T> {
    void setValue(O object, T value) throws IOException;

    T getValue(O object) throws IOException;
}
