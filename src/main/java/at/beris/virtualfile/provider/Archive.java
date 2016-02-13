/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.*;
import at.beris.virtualfile.client.IClient;
import org.apache.commons.lang3.NotImplementedException;

import java.net.URL;
import java.util.Map;

public class Archive extends FileContainer implements IArchive {
    public Archive(URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap, IClient client) {
        super(url, model, fileOperationProviderMap, client);
    }

    public Archive(IFile parent, URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap) {
        super(parent, url, model, fileOperationProviderMap);
    }

    public Archive(IFile parent, URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap, IClient client) {
        super(parent, url, model, fileOperationProviderMap, client);
    }

    @Override
    public void extract() {
        throw new NotImplementedException("");
    }
}
