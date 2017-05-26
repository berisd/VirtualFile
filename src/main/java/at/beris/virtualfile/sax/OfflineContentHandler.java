/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.sax;

import at.beris.virtualfile.io.ClosedInputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

/**
 * Content handler decorator that always returns an empty stream from the
 * {@link #resolveEntity(String, String)} method to prevent potential
 * network or other external resources from being accessed by an XML parser.
 *
 * @see <a href="https://issues.apache.org/jira/browse/TIKA-185">TIKA-185</a>
 */
public class OfflineContentHandler extends ContentHandlerDecorator {

    public OfflineContentHandler(ContentHandler handler) {
        super(handler);
    }

    /**
     * Returns an empty stream. This will make an XML parser silently
     * ignore any external entities.
     */
    @Override
    public InputSource resolveEntity(String publicId, String systemId) {
        return new InputSource(new ClosedInputStream());
    }

}
