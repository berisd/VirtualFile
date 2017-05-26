/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.mime;

import at.beris.virtualfile.content.detect.MagicDetector;
import at.beris.virtualfile.content.metadata.Metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Defines a magic match.
 */
class MagicMatch implements Clause {

    private final MediaType mediaType;

    private final String type;

    private final String offset;

    private final String value;

    private final String mask;

    private MagicDetector detector = null;

    MagicMatch(
            MediaType mediaType,
            String type, String offset, String value, String mask) {
        this.mediaType = mediaType;
        this.type = type;
        this.offset = offset;
        this.value = value;
        this.mask = mask;
    }

    private synchronized MagicDetector getDetector() {
        if (detector == null) {
            detector = MagicDetector.parse(mediaType, type, offset, value, mask);
        }
        return detector;
    }

    public boolean eval(byte[] data) {
        try {
            return getDetector().detect(
                    new ByteArrayInputStream(data), new Metadata())
                    != MediaType.OCTET_STREAM;
        } catch (IOException e) {
            // Should never happen with a ByteArrayInputStream
            return false;
        }
    }

    public int size() {
        return getDetector().getLength();
    }

    public String toString() {
        return mediaType.toString()
                + " " + type + " " + offset + " " +  value + " " + mask;
    }

}
