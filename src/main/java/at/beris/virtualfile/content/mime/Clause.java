/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.mime;

import java.io.Serializable;

/**
 * Defines a clause to be evaluated.
 */
interface Clause extends Serializable {

    /**
     * Evaluates this clause with the specified chunk of data.
     */
    boolean eval(byte[] data);

    /**
     * Returns the size of this clause. The size of a clause is the number of
     * chars it is composed of.
     */
    int size();

}
