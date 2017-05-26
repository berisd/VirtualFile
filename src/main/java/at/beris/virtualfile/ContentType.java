/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.content.mime.MediaType;

public class ContentType {
    private MediaType mediaType;

    protected ContentType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getType() {
        return mediaType.getType();
    }

    public String getSubType() {
        return mediaType.getSubtype();
    }

    @Override
    public String toString() {
        return mediaType.toString();
    }
}
