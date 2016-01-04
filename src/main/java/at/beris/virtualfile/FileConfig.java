/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.SftpClient;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.LocalArchiveOperationProvider;
import at.beris.virtualfile.provider.LocalArchivedFileOperationProvider;
import at.beris.virtualfile.provider.LocalFileOperationProvider;
import at.beris.virtualfile.provider.SftpFileOperationProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileConfig {
    private Map<Protocol, Map<FileType, Class>> fileOperationProviderClassMap;
    private Map<Protocol, Class> clientClassMap;

    public FileConfig() {
        fileOperationProviderClassMap = new HashMap<>();
        clientClassMap = new HashMap<>();
        put(Protocol.FILE, createLocalFileOperationProviderClassMap(), null);
        put(Protocol.SFTP, Collections.singletonMap(FileType.DEFAULT, (Class) SftpFileOperationProvider.class), SftpClient.class);
    }

    public Map<FileType, Class> getFileOperationProviderClassMap(Protocol protocol) {
        return fileOperationProviderClassMap.get(protocol);
    }

    public Class getClientClass(Protocol protocol) {
        return clientClassMap.get(protocol);
    }

    public void put(Protocol protocol, Map<FileType, Class> fileOperationProviderClassForFileExt, Class clientClass) {
        fileOperationProviderClassMap.put(protocol, fileOperationProviderClassForFileExt);
        clientClassMap.put(protocol, clientClass);
    }

    private Map<FileType, Class> createLocalFileOperationProviderClassMap() {
        Map<FileType, Class> localFileProviderForExtMap = new HashMap<>();
        localFileProviderForExtMap.put(FileType.DEFAULT, LocalFileOperationProvider.class);
        localFileProviderForExtMap.put(FileType.ARCHIVED, LocalArchivedFileOperationProvider.class);
        localFileProviderForExtMap.put(FileType.ARCHIVE, LocalArchiveOperationProvider.class);
        return localFileProviderForExtMap;
    }
}
