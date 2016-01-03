/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.jarcommander.filesystem.file.client.IClient;
import at.beris.jarcommander.filesystem.file.provider.IFileOperationProvider;
import at.beris.jarcommander.filesystem.model.FileModel;

import java.net.URL;

public class LocalFile extends File {
    public LocalFile(URL url, FileModel model, IFileOperationProvider operationProvider, IClient client) {
        super(url, model, operationProvider, client);
    }

    public LocalFile(IFile parent, URL url, FileModel model, IFileOperationProvider operationProvider) {
        super(parent, url, model, operationProvider);
    }

    public LocalFile(IFile parent, URL url, FileModel model, IFileOperationProvider operationProvider, IClient client) {
        super(parent, url, model, operationProvider, client);
    }
}
