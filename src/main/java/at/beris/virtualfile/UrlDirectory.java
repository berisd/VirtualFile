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
import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import at.beris.virtualfile.provider.FileOperationProvider;

import java.net.URL;
import java.util.Map;

public class UrlDirectory extends UrlFileContainer implements Directory {
    public UrlDirectory(URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(url, model, fileOperationProviderMap, client, fileOperationMap);
    }

    public UrlDirectory(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(parent, url, model, fileOperationProviderMap, fileOperationMap);
    }

    public UrlDirectory(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(parent, url, model, fileOperationProviderMap, client, fileOperationMap);
    }
}
