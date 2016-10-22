/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.exception;

public class VirtualFileException extends RuntimeException {

    private Integer id;
    private String message;

    public VirtualFileException() {
        super();
        id = Message.ID_UNKNOWN;
        message = getMessage();
    }

    public VirtualFileException(Message message) {
        super();
        this.id = message.getId();
        this.message = message.getMessage();
    }

    public VirtualFileException(Throwable cause) {
        super(cause);
        id = Message.ID_UNKNOWN;
        message = getMessage();
    }

    public VirtualFileException(Message message, Throwable cause) {
        super(cause);
        this.id = message.getId();
        this.message = message.getMessage();
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
