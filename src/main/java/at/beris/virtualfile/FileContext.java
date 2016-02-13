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
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.IFileOperationProvider;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FileContext {
    private FileConfig fileConfig;

    private Map<String, IClient> siteToClientMap;

    public FileContext(FileConfig fileConfig) {
        registerProtocolURLStreamHandlers();
        this.fileConfig = fileConfig;
        this.siteToClientMap = new HashMap<>();
    }

    /**
     * Creates a local file. (Convenience method)
     *
     * @param path
     * @return
     */
    public IFile newLocalFile(String path) {
        try {
            URL url = new java.io.File(path).toURI().toURL();
            if (path.endsWith(java.io.File.separator))
                url = new URL(url.toString() + "/");
            return newFile(url);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Creates a file. (Convenience method)
     *
     * @param url
     * @return
     */
    public IFile newFile(String url) {
        try {
            return newFile((IFile) null, new URL(url));
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    public IFile newFile(URL parentUrl, URL url) {
        return newFile(newFile(parentUrl), url);
    }

    /**
     * Creates a file instance for the corresponding url
     *
     * @param parent
     * @param url
     * @return
     */
    public IFile newFile(IFile parent, URL url) {
        URL normalizedUrl = FileUtils.normalizeUrl(url);
        String protocolString = normalizedUrl.getProtocol();
        Protocol protocol = null;
        try {
            protocol = Protocol.valueOf(normalizedUrl.getProtocol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VirtualFileException("Unknown protocol: " + protocolString);
        }

        if (fileConfig.getFileOperationProviderClassMap(protocol) == null)
            throw new VirtualFileException("No configuration found for protocol: " + protocolString);

        File file = null;
        try {
            IClient client = createClientInstance(normalizedUrl, fileConfig.getClientClass(protocol));
            Map<FileType, IFileOperationProvider> fileOperationProviderMap = new HashMap<>();
            for (FileType fileType : FileType.values()) {
                fileOperationProviderMap.put(fileType, (IFileOperationProvider) fileConfig.getFileOperationProviderClassMap(protocol).get(fileType).newInstance());
            }

            file = new File(parent, normalizedUrl, new FileModel(), fileOperationProviderMap, client);
            if (file.exists())
                file.updateModel();
        } catch (InstantiationException e) {
            throw new VirtualFileException(e);
        } catch (IllegalAccessException e) {
            throw new VirtualFileException(e);
        }
        return file;
    }

    public IFile newFile(URL url) {
        URL normalizedUrl = FileUtils.normalizeUrl(url);
        String fullPath = normalizedUrl.getPath();
        IFile parentFile = null;

        String[] pathParts;
        if (fullPath.equals("/"))
            return newFile((IFile) null, normalizedUrl);
        else {
            pathParts = fullPath.split("/");
            String path = "";
            for (int i = 0; i < pathParts.length; i++) {
                path += pathParts[i];

                if ((i < pathParts.length - 1) || fullPath.endsWith("/"))
                    path += "/";

                try {
                    String pathUrlString = getSiteUrlString(normalizedUrl) + path;
                    URL pathUrl = new URL(pathUrlString);
                    parentFile = newFile(parentFile, pathUrl);
                } catch (MalformedURLException e) {
                    throw new VirtualFileException(e);
                }
            }
        }
        return parentFile;
    }

    /**
     * Set property so that URL class will find custom handlers
     */
    public void registerProtocolURLStreamHandlers() {
        String propertyKey = "java.protocol.handler.pkgs";
        String propertyValue = System.getProperties().getProperty(propertyKey);
        if (StringUtils.isEmpty(propertyValue))
            propertyValue = "";
        else
            propertyValue += "|";
        propertyValue += at.beris.virtualfile.protocol.Protocol.class.getPackage().getName();
        System.getProperties().setProperty(propertyKey, propertyValue);
    }

    public String getSiteUrlString(URL url) {
        String urlString = url.toString();
        return urlString.substring(0, urlString.indexOf("/", urlString.indexOf("//") + 2));
    }

    private IClient createClientInstance(URL url, Class clientClass) throws InstantiationException, IllegalAccessException {
        IClient client = null;
        if (clientClass != null) {
            String siteUrl = getSiteUrlString(url);
            client = siteToClientMap.get(siteUrl);
            if (client == null) {
                client = (IClient) clientClass.newInstance();
                client.setHost(url.getHost());
                client.setPort(url.getPort());
                String userInfoParts[] = url.getUserInfo().split(":");
                client.setUsername(userInfoParts[0]);
                client.setPassword(userInfoParts[1]);
                siteToClientMap.put(siteUrl, client);
            }
        }
        return client;
    }
}
