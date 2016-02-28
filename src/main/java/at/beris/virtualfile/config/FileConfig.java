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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileConfig {
    private Map<Protocol, Map<FileType, Class>> fileOperationProviderClassMap;
    private Map<Protocol, Class> clientClassMap;
    private Map<ConfigKey, Object> settings;

    public FileConfig() {
        fileOperationProviderClassMap = new HashMap<>();
        clientClassMap = new HashMap<>();
        put(Protocol.FILE, createLocalFileOperationProviderClassMap(), null);
        put(Protocol.SFTP, createSftpFileOperationProviderClassMap(), SftpClient.class);

        settings = new HashMap<>();
        settings.put(ConfigKey.CLIENT_STRICT_HOSTKEY_CHECKING, true);
        settings.put(ConfigKey.KNOWN_HOSTS_FILE, System.getProperty("user.home") + File.separator + ".ssh" + File.separator + "known_hosts");
        settings.put(ConfigKey.CLIENT_TIMEOUT, 10);
        settings.put(ConfigKey.CLIENT_AUTHENTICATION_TYPE, AuthenticationType.PASSWORD);
        settings.put(ConfigKey.PRIVATE_KEY_FILE, "");
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

    public Map<ConfigKey, Object> getSettings() {
        return settings;
    }

    public FileConfig setSettings(Map<ConfigKey, Object> settings) {
        this.settings = settings;
        return this;
    }

    public boolean isClientStrictHostKeyChecking() {
        return (boolean) settings.get(ConfigKey.CLIENT_STRICT_HOSTKEY_CHECKING);
    }

    public FileConfig setClientStrictHostKeyChecking(boolean strictHostKeyChecking) {
        settings.put(ConfigKey.CLIENT_STRICT_HOSTKEY_CHECKING, strictHostKeyChecking);
        return this;
    }

    public String getKnownHostsFile() {
        return (String) settings.get(ConfigKey.KNOWN_HOSTS_FILE);
    }

    public FileConfig setKnownHostsFile(String knownHostsFile) {
        settings.put(ConfigKey.KNOWN_HOSTS_FILE, knownHostsFile);
        return this;
    }

    public int getClientTimeOut() {
        return (int) settings.get(ConfigKey.CLIENT_TIMEOUT);
    }

    public FileConfig setClientTimeOut(int timeOut) {
        settings.put(ConfigKey.CLIENT_TIMEOUT, timeOut);
        return this;
    }

    public AuthenticationType getClientAuthenticationType() {
        return (AuthenticationType) settings.get(ConfigKey.CLIENT_AUTHENTICATION_TYPE);
    }

    public FileConfig setClientAuthenticationType(AuthenticationType authenticationType) {
        settings.put(ConfigKey.CLIENT_AUTHENTICATION_TYPE, authenticationType);
        return this;
    }

    public String getPrivateKeyFile() {
        return (String) settings.get(ConfigKey.PRIVATE_KEY_FILE);
    }

    public FileConfig setPrivateKeyFile(String privateKeyFile) {
        settings.put(ConfigKey.PRIVATE_KEY_FILE, privateKeyFile);
        return this;
    }
}
