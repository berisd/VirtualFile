/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.mime;

/**
 * A class to encapsulate MimeType related exceptions.
 */
public class MimeTypeException extends Exception {

    /**
     * Constructs a MimeTypeException with the specified detail message.
     * 
     * @param message the detail message.
     */
    public MimeTypeException(String message) {
        super(message);
    }

    /**
     * Constructs a MimeTypeException with the specified detail message
     * and root cause.
     * 
     * @param message the detail message.
     * @param cause root cause
     */
    public MimeTypeException(String message, Throwable cause) {
        super(message, cause);
    }

}
