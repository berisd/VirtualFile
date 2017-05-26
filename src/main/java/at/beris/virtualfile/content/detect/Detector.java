/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.detect;

import at.beris.virtualfile.content.metadata.Metadata;
import at.beris.virtualfile.content.mime.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Content type detector. Implementations of this interface use various
 * heuristics to detect the content type of a document based on given
 * input metadata or the first few bytes of the document stream.
 *
 * @since Apache Tika 0.3
 */
public interface Detector extends Serializable {

    /**
     * Detects the content type of the given input document. Returns
     * <code>application/octet-stream</code> if the type of the document
     * can not be detected.
     * <p>
     * If the document input stream is not available, then the first
     * argument may be <code>null</code>. Otherwise the detector may
     * read bytes from the start of the stream to help in type detection.
     * The given stream is guaranteed to support the
     * {@link InputStream#markSupported() mark feature} and the detector
     * is expected to {@link InputStream#mark(int) mark} the stream before
     * reading any bytes from it, and to {@link InputStream#reset() reset}
     * the stream before returning. The stream must not be closed by the
     * detector.
     * <p>
     * The given input metadata is only read, not modified, by the detector.
     *
     * @param input document input stream, or <code>null</code>
     * @param metadata input metadata for the document
     * @return detected media type, or <code>application/octet-stream</code>
     * @throws IOException if the document input stream could not be read
     */
    MediaType detect(InputStream input, Metadata metadata) throws IOException;

}
