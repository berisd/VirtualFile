/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.FileType;
import at.beris.virtualfile.client.SftpClient;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.LocalArchiveOperationProvider;
import at.beris.virtualfile.provider.LocalArchivedFileOperationProvider;
import at.beris.virtualfile.provider.LocalFileOperationProvider;
import at.beris.virtualfile.provider.SftpFileOperationProvider;
import at.beris.virtualfile.util.UrlUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Configurator {
    private Map<Protocol, Map<FileType, Class>> fileOperationProviderClassMap;
    private Map<Protocol, Class> clientClassMap;

    private BaseConfig baseConfig;

    private ClientConfig defaultClientConfig;
    private Map<Protocol, ClientConfig> clientConfigPerProtocolMap;
    private Map<URL, ClientConfig> clientConfigPerUrlMap;

    public Configurator() {
        fileOperationProviderClassMap = new HashMap<>();
        clientClassMap = new HashMap<>();
        clientConfigPerProtocolMap = new HashMap<>();
        clientConfigPerUrlMap = new HashMap<>();

        baseConfig = new BaseConfig();
        baseConfig.initValues();

        defaultClientConfig = new ClientConfig();
        defaultClientConfig.initValues();

        put(Protocol.FILE, createLocalFileOperationProviderClassMap(), null);
        clientConfigPerProtocolMap.put(Protocol.FILE, new ClientConfig());
        put(Protocol.SFTP, createSftpFileOperationProviderClassMap(), SftpClient.class);
        clientConfigPerProtocolMap.put(Protocol.SFTP, new ClientConfig());
    }

    public ClientConfig setClientConfig(ClientConfig config, URL url) {
        return clientConfigPerUrlMap.put(url, config);
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

    private Map<FileType, Class> createSftpFileOperationProviderClassMap() {
        Map<FileType, Class> localFileProviderForExtMap = new HashMap<>();
        localFileProviderForExtMap.put(FileType.DEFAULT, SftpFileOperationProvider.class);
        localFileProviderForExtMap.put(FileType.ARCHIVED, LocalArchivedFileOperationProvider.class);
        localFileProviderForExtMap.put(FileType.ARCHIVE, LocalArchiveOperationProvider.class);
        return localFileProviderForExtMap;
    }

    public ClientConfig createClientConfig(URL url) {
        ClientConfig config = new ClientConfig();

        Protocol protocol = UrlUtils.getProtocol(url);

        ClientConfig protocolConfig = clientConfigPerProtocolMap.get(protocol);
        if (protocolConfig == null) {
            protocolConfig = new ClientConfig();
            clientConfigPerProtocolMap.put(protocol, protocolConfig);
        }

        ClientConfig urlConfig = clientConfigPerUrlMap.get(url);
        if (urlConfig == null) {
            urlConfig = new ClientConfig();
            clientConfigPerUrlMap.put(url, urlConfig);
        }

        for (ClientConfigOption configKey : ClientConfigOption.values()) {
            ConfigValue value = urlConfig.get(configKey);

            if (value == null)
                value = protocolConfig.get(configKey);

            if (value == null)
                value = defaultClientConfig.get(configKey);

            if (value != null)
                config.set(configKey, value.clone());
            else
                config.set(configKey, null);
        }

        return config;
    }

    public ClientConfig getClientConfig() {
        return defaultClientConfig;
    }

    public ClientConfig getClientConfig(Protocol protocol) {
        return clientConfigPerProtocolMap.get(protocol);
    }

    public ClientConfig getClientConfig(URL url) {
        return clientConfigPerUrlMap.get(url);
    }

    public BaseConfig getBaseConfig() {
        return baseConfig;
    }
}
