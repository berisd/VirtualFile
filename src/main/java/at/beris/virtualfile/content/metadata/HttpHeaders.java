/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.metadata;

/**
 * A collection of HTTP header names.
 * 
 * @see <a href="http://rfc-ref.org/RFC-TEXTS/2616/">Hypertext Transfer Protocol --
 *      HTTP/1.1 (RFC 2616)</a>
 */
public interface HttpHeaders {

    String CONTENT_ENCODING = "Content-Encoding";

    String CONTENT_LANGUAGE = "Content-Language";

    String CONTENT_LENGTH = "Content-Length";

    String CONTENT_LOCATION = "Content-Location";

    String CONTENT_DISPOSITION = "Content-Disposition";

    String CONTENT_MD5 = "Content-MD5";

    String CONTENT_TYPE = "Content-Type";

    Property LAST_MODIFIED =
        Property.internalDate("Last-Modified");

    String LOCATION = "Location";

}
