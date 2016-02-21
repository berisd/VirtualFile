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
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.provider.IFileOperationProvider;

import java.net.URL;
import java.util.Map;

public class FileContainer extends File implements IFileContainer {
    public FileContainer(URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap, IClient client) {
        super(url, model, fileOperationProviderMap, client);
    }

    public FileContainer(IFile parent, URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap) {
        super(parent, url, model, fileOperationProviderMap);
    }

    public FileContainer(IFile parent, URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap, IClient client) {
        super(parent, url, model, fileOperationProviderMap, client);
    }

    @Override
    public void add(IFile file) {
        getFileOperationProvider().add(this, file);
    }

    @Override
    public void delete(IFile file) {
        throw new NotImplementedException();
    }
}
