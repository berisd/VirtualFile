/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.exception;

public class OperationNotSupportedException extends VirtualFileException {
    public OperationNotSupportedException() {
        super();
    }

    public OperationNotSupportedException(String message) {
        super(Message.OPERATION_NOT_SUPPORTED(message));
    }
}
