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
import at.beris.virtualfile.provider.ArchiveOperationProvider;
import at.beris.virtualfile.provider.FileOperationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class UrlArchive extends UrlFileContainer implements Archive {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrlArchive.class);

    public UrlArchive(URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(url, model, fileOperationProviderMap, client, fileOperationMap);
    }

    public UrlArchive(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(parent, url, model, fileOperationProviderMap, fileOperationMap);
    }

    public UrlArchive(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(parent, url, model, fileOperationProviderMap, client, fileOperationMap);
    }

    @Override
    public List<File> extract(File target) {
        LOGGER.info("Extract " + this + " to " + target);
        ArchiveOperationProvider opProvider = (ArchiveOperationProvider) getFileOperationProvider();
        return opProvider.extract(getClient(), getModel(), target);
    }
}
