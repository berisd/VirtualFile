/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

public enum FileOperation {
    ADD_ATTRIBUTES,
    CHECKSUM,
    COPY,
    CREATE,
    DELETE,
    EXISTS,
    EXTRACT,
    GET_INPUT_STREAM,
    GET_OUTPUT_STREAM,
    LIST,
    REMOVE_ATTRIBUTES,
    SET_ACL,
    SET_ATTRIBUTES,
    SET_CREATION_TIME,
    SET_GROUP,
    SET_LAST_ACCESS_TIME,
    SET_LAST_MODIFIED_TIME,
    SET_OWNER,
    UPDATE_MODEL
}
