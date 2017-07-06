/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.http;

import at.beris.virtualfile.client.VirtualClient;
import at.beris.virtualfile.config.UrlFileConfiguration;

import java.net.URL;

public class HttpClient implements VirtualClient {

    public HttpClient(URL url, UrlFileConfiguration config) {
    }

    @Override
    public void dispose() {

    }
}
