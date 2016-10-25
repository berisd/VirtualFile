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
import at.beris.virtualfile.client.ftp.FtpClient;
import at.beris.virtualfile.client.http.HttpClient;
import at.beris.virtualfile.client.https.HttpsClient;
import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.*;
import at.beris.virtualfile.util.UrlUtils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Configurator {
    private Map<Protocol, Class> fileOperationProviderClassMap;
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

        clientClassMap.put(Protocol.FILE, null);
        clientClassMap.put(Protocol.SFTP, SftpClient.class);
        clientClassMap.put(Protocol.FTP, FtpClient.class);
        clientClassMap.put(Protocol.HTTP, HttpClient.class);
        clientClassMap.put(Protocol.HTTPS, HttpsClient.class);

        fileOperationProviderClassMap.put(Protocol.FILE, LocalFileOperationProvider.class);
        fileOperationProviderClassMap.put(Protocol.SFTP, SftpClientFileOperationProvider.class);
        fileOperationProviderClassMap.put(Protocol.FTP, FtpClientFileOperationProvider.class);
        fileOperationProviderClassMap.put(Protocol.HTTP, HttpClientFileOperationProvider.class);
        fileOperationProviderClassMap.put(Protocol.HTTPS, HttpsClientFileOperationProvider.class);

        for (Protocol protocol : Protocol.values()) {
            configurationPerProtocolMap.put(protocol, new Configuration(defaultConfiguration));
        }
    }

    public Class getFileOperationProviderClass(Protocol protocol) {
        return fileOperationProviderClassMap.get(protocol);
    }

    public Class getClientClass(Protocol protocol) {
        return clientClassMap.get(protocol);
    }

    void put(Protocol protocol, Class fileOperationProviderClass, Class clientClass) {
        fileOperationProviderClassMap.put(protocol, fileOperationProviderClass);
        clientClassMap.put(protocol, clientClass);
    }

    public Configuration createConfiguration(URL url) {
        Protocol protocol = UrlUtils.getProtocol(url);

        Configuration protocolConfig = configurationPerProtocolMap.get(protocol);
        if (protocolConfig == null) {
            protocolConfig = new Configuration(defaultConfiguration);
            configurationPerProtocolMap.put(protocol, protocolConfig);
        }

        URL siteUrl = UrlUtils.newUrl(UrlUtils.getSiteUrlString(url.toString()));
        Configuration urlConfig = configurationPerUrlMap.get(siteUrl);
        if (urlConfig == null) {
            urlConfig = new Configuration(protocolConfig);
            configurationPerUrlMap.put(siteUrl, urlConfig);
        }

        return urlConfig;
    }

    public Configuration getConfiguration() {
        return defaultConfiguration;
    }

    public Configuration getConfiguration(Protocol protocol) {
        return configurationPerProtocolMap.get(protocol);
    }

    public Configuration getConfiguration(VirtualFile file) {
        URL siteUrl = UrlUtils.newUrl(UrlUtils.getSiteUrlString(file.getUrl().toString()));
        return configurationPerUrlMap.get(siteUrl);
    }

    public ContextConfiguration getContextConfiguration() {
        return contextConfiguration;
    }
}
