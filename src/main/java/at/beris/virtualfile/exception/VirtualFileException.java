/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.exception;

public class VirtualFileException extends RuntimeException {
    public VirtualFileException() {
        super();
    }

    public VirtualFileException(Throwable cause) {
        super(cause);
    }

    public VirtualFileException(String message) {
        super(message);
    }
}
