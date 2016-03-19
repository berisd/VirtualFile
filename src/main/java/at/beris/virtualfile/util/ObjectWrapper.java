/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

/**
 * Interface for an object wrapper.
 * e.g. to apply the Decorator Pattern
 *
 * @param <T> Type of object being wrapped
 */
public interface ObjectWrapper<T> {
    T getWrappedObject();
}
