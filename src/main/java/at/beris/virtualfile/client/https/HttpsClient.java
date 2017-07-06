/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.https;

import at.beris.virtualfile.client.http.HttpClient;
import at.beris.virtualfile.config.UrlFileConfiguration;

import java.net.URL;

public class HttpsClient extends HttpClient {

    public HttpsClient(URL url, UrlFileConfiguration config) {
        super(url, config);
    }
}
