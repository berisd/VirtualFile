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
import at.beris.virtualfile.provider.IArchiveOperationProvider;
import at.beris.virtualfile.provider.IFileOperationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class Archive extends FileContainer implements IArchive {
    private final static Logger LOGGER = LoggerFactory.getLogger(File.class);

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
    public List<IFile> extract(IFile target) {
        LOGGER.info("Extract " + this + " to " + target);
        IArchiveOperationProvider opProvider = (IArchiveOperationProvider) getFileOperationProvider();
        return opProvider.extract(getClient(), getModel(), target);
    }
}
