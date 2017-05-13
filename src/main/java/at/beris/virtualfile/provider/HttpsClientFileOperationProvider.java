/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.UrlFileContext;
import at.beris.virtualfile.client.https.HttpsClient;

public class HttpsClientFileOperationProvider extends HttpClientFileOperationProvider {
    public HttpsClientFileOperationProvider(UrlFileContext fileContext, HttpsClient httpsURLConnection) {
        super(fileContext, httpsURLConnection);
    }
}
