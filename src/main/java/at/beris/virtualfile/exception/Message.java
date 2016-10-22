/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.exception;

import at.beris.virtualfile.protocol.Protocol;

public class Message {
    public static Integer ID_UNKNOWN = 0;
    public static Integer ID_FILE_ALREADY_EXISTS = 1;
    public static Integer ID_ACCESS_DENIED = 2;
    public static Integer ID_NO_SUCH_FILE = 3;
    public static Integer ID_PROTOCOL_NOT_CONFIGURED = 4;
    public static Integer ID_OPERATION_NOT_SUPPORTED = 5;

    public static Message FILE_ALREADY_EXISTS(String filename) {
        return new Message(ID_FILE_ALREADY_EXISTS, String.format("File already exists: %s.", filename));
    }

    public static Message ACCESS_DENIED() {
        return new Message(ID_ACCESS_DENIED, "Access denied.");
    }

    public static Message NO_SUCH_FILE(String filename) {
        return new Message(ID_NO_SUCH_FILE, String.format("No such file: %s.", filename));
    }

    public static Message PROTOCOL_NOT_CONFIGURED(Protocol protocol) {
        return new Message(ID_PROTOCOL_NOT_CONFIGURED, String.format("Protocol not configured: %s.", protocol));
    }

    public static Message OPERATION_NOT_SUPPORTED(String message) {
        return new Message(ID_OPERATION_NOT_SUPPORTED, String.format("Operation not supported: %s.", message));
    }

    private Integer id;
    private String message;

    private Message(Integer id, String message) {
        this.id = id;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("%d: %s", id, message);
    }
}
