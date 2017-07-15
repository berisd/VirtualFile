/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.http;

import at.beris.virtualfile.client.ClientConfiguration;
import at.beris.virtualfile.protocol.Protocol;

public class HttpClientConfiguration extends ClientConfiguration<HttpClientConfiguration> {
    public HttpClientConfiguration() {
        super();
        setPort(Protocol.HTTP.getDefaultPort());

    }
}
