/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.FileType;
import at.beris.virtualfile.client.ftp.FtpClient;
import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.*;
import at.beris.virtualfile.util.UrlUtils;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Configurator {
    private Map<Protocol, Map<FileType, Class>> fileOperationProviderClassMap;
    private Map<Protocol, Class> clientClassMap;

    private ContextConfiguration contextConfiguration;

    private Configuration defaultConfiguration;
    private Map<Protocol, Configuration> configurationPerProtocolMap;
    private Map<URL, Configuration> configurationPerUrlMap;

    public Configurator() {
        fileOperationProviderClassMap = new HashMap<>();
        clientClassMap = new HashMap<>();
        configurationPerProtocolMap = new HashMap<>();
        configurationPerUrlMap = new HashMap<>();

        contextConfiguration = new ContextConfiguration();
        contextConfiguration.initValues();

        defaultConfiguration = new Configuration();
        defaultConfiguration.initValues();

        put(Protocol.FILE, createLocalFileOperationProviderClassMap(), null);
        configurationPerProtocolMap.put(Protocol.FILE, new Configuration(defaultConfiguration));
        put(Protocol.SFTP, createSftpClientFileOperationProviderClassMap(), SftpClient.class);
        configurationPerProtocolMap.put(Protocol.SFTP, new Configuration(defaultConfiguration));
        put(Protocol.FTP, createFtpClientFileOperationProviderClassMap(), FtpClient.class);
        configurationPerProtocolMap.put(Protocol.FTP, new Configuration(defaultConfiguration));
    }

    public Map<FileType, Class> getFileOperationProviderClassMap(Protocol protocol) {
        return fileOperationProviderClassMap.get(protocol);
    }

    public Class getClientClass(Protocol protocol) {
        return clientClassMap.get(protocol);
    }

    void put(Protocol protocol, Map<FileType, Class> fileOperationProviderClassForFileExt, Class clientClass) {
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


    private Map<FileType, Class> createSftpClientFileOperationProviderClassMap() {
        Map<FileType, Class> fileProviderClassMap = new HashMap<>();
        fileProviderClassMap.put(FileType.DEFAULT, SftpClientFileOperationProvider.class);
        fileProviderClassMap.put(FileType.ARCHIVED, LocalArchivedFileOperationProvider.class);
        fileProviderClassMap.put(FileType.ARCHIVE, SftpClientFileOperationProvider.class);
        return fileProviderClassMap;
    }

    private Map<FileType, Class> createFtpClientFileOperationProviderClassMap() {
        Map<FileType, Class> fileProviderClassMap = new HashMap<>();
        fileProviderClassMap.put(FileType.DEFAULT, FtpClientFileOperationProvider.class);
        fileProviderClassMap.put(FileType.ARCHIVED, LocalArchivedFileOperationProvider.class);
        fileProviderClassMap.put(FileType.ARCHIVE, FtpClientFileOperationProvider.class);
        return fileProviderClassMap;
    }

    public Configuration createConfiguration(URL url) {
        Protocol protocol = UrlUtils.getProtocol(url);

        Configuration protocolConfig = configurationPerProtocolMap.get(protocol);
        if (protocolConfig == null) {
            protocolConfig = new Configuration(defaultConfiguration);
            configurationPerProtocolMap.put(protocol, protocolConfig);
        }

        Configuration urlConfig = configurationPerUrlMap.get(url);
        if (urlConfig == null) {
            urlConfig = new Configuration(protocolConfig);
            configurationPerUrlMap.put(url, urlConfig);
        }

        return urlConfig;
    }

    public Configuration getConfiguration() {
        return defaultConfiguration;
    }

    public Configuration getConfiguration(Protocol protocol) {
        return configurationPerProtocolMap.get(protocol);
    }

    public Configuration getConfiguration(VirtualFile file) throws IOException {
        return configurationPerUrlMap.get(file.getUrl());
    }

    public ContextConfiguration getContextConfiguration() {
        return contextConfiguration;
    }
}
