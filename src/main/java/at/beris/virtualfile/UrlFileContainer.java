/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;


import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import at.beris.virtualfile.provider.FileOperationProvider;

import java.net.URL;
import java.util.Map;

public class UrlFileContainer extends UrlFile implements FileContainer {
    public UrlFileContainer(URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(url, model, fileOperationProviderMap, client, fileOperationMap);
    }

    public UrlFileContainer(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(parent, url, model, fileOperationProviderMap, fileOperationMap);
    }

    public UrlFileContainer(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(parent, url, model, fileOperationProviderMap, client, fileOperationMap);
    }

    @Override
    public void add(File file) {
        getFileOperationProvider().add(this, file);
    }

    @Override
    public void delete(File file) {
        throw new NotImplementedException();
    }
}
