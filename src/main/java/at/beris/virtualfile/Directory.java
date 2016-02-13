/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.provider.IFileOperationProvider;

import java.net.URL;
import java.util.Map;

public class Directory extends FileContainer implements IDirectory {
    public Directory(URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap, IClient client) {
        super(url, model, fileOperationProviderMap, client);
    }

    public Directory(IFile parent, URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap) {
        super(parent, url, model, fileOperationProviderMap);
    }

    public Directory(IFile parent, URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap, IClient client) {
        super(parent, url, model, fileOperationProviderMap, client);
    }
}
