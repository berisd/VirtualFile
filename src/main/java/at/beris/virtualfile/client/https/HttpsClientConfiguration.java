/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.https;

import at.beris.virtualfile.client.ClientConfiguration;
import at.beris.virtualfile.protocol.Protocol;

public class HttpsClientConfiguration extends ClientConfiguration<HttpsClientConfiguration> {
    public HttpsClientConfiguration() {
        super();
        setPort(Protocol.HTTPS.getDefaultPort());
    }
}
