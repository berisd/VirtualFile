/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.cache.LRUMap;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.config.FileConfig;
import at.beris.virtualfile.exception.FileNotFoundException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileContext {
    private FileConfig defaultFileConfig;

    private Map<String, Client> siteToClientMap;
    private Map<URL, FileConfig> fileConfigMap;
    private Map<String, File> fileCache;

    public FileContext(FileConfig fileConfig) {
        registerProtocolURLStreamHandlers();
        this.defaultFileConfig = fileConfig;
        this.siteToClientMap = Collections.synchronizedMap(new HashMap<String, Client>());
        this.fileConfigMap = new HashMap<>();
        this.fileCache = Collections.synchronizedMap(new LRUMap<String, File>(2048));
    }

    /**
     * Creates a local file. (Convenience method)
     *
     * @param path
     * @return
     */
    public File newLocalFile(String path, FileConfig fileConfig) {
        try {
            URL url = new java.io.File(path).toURI().toURL();
            if (path.endsWith(java.io.File.separator))
                url = new URL(url.toString() + "/");
            return newFile(url, fileConfig);
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
    public File newFile(String url, FileConfig fileConfig) {
        try {
            return newFile((File) null, new URL(url), fileConfig);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    public File newFile(URL parentUrl, URL url, FileConfig fileConfig) {
        return newFile(newFile(parentUrl, fileConfig), url, fileConfig);
    }

    /**
     * Creates a file instance for the corresponding url
     *
     * @param parent
     * @param url
     * @return
     */
    public File newFile(File parent, URL url, FileConfig fileConfig) {
        if (fileConfig == null) {
            fileConfig = fileConfigMap.get(url);
            if (fileConfig == null)
                fileConfig = defaultFileConfig;
        } else {
            fileConfigMap.put(url, fileConfig);
        }

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

        UrlFile file = null;
        try {
            String siteUrlString = getSiteUrlString(url);
            Client client = siteToClientMap.get(siteUrlString);

            if (client == null) {
                client = createClientInstance(normalizedUrl, fileConfig.getClientClass(protocol), fileConfig);
                siteToClientMap.put(siteUrlString, client);
            }

            Map<FileType, FileOperationProvider> fileOperationProviderMap = new HashMap<>();
            for (FileType fileType : FileType.values()) {
                fileOperationProviderMap.put(fileType, (FileOperationProvider) fileConfig.getFileOperationProviderClassMap(protocol).get(fileType).newInstance());
            }

            String urlString = normalizedUrl.toString();
            file = (UrlFile) fileCache.get(urlString);
            if (file == null) {
                file = createFileInstance(parent, normalizedUrl, client, fileOperationProviderMap);
                fileCache.put(urlString, file);
            }
        } catch (InstantiationException e) {
            throw new VirtualFileException(e);
        } catch (IllegalAccessException e) {
            throw new VirtualFileException(e);
        }
        return file;
    }

    public File newFile(URL url, FileConfig fileConfig) {
        URL normalizedUrl = FileUtils.normalizeUrl(url);
        String fullPath = normalizedUrl.getPath();
        File parentFile = null;

        String[] pathParts;
        if (fullPath.equals("/"))
            return newFile((File) null, normalizedUrl, fileConfig);
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
                    parentFile = newFile(parentFile, pathUrl, fileConfig);
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

    private Client createClientInstance(URL url, Class clientClass, FileConfig fileConfig) throws InstantiationException, IllegalAccessException {
        Client client = null;
        if (clientClass != null) {
            try {
                Constructor constructor = clientClass.getConstructor(FileConfig.class);
                client = (Client) constructor.newInstance(fileConfig);
            } catch (ReflectiveOperationException e) {
                throw new VirtualFileException(e);
            }
            client.setHost(url.getHost());
            client.setPort(url.getPort());
            String userInfoParts[] = url.getUserInfo().split(":");
            client.setUsername(userInfoParts[0]);
            client.setPassword(userInfoParts[1]);
        }
        return client;
    }

    private UrlFile createFileInstance(File parent, URL normalizedUrl, Client client, Map<FileType, FileOperationProvider> fileOperationProviderMap) {
        Class instanceClass;
        UrlFile instance;

        FileModel fileModel = new FileModel();
        if (parent != null)
            fileModel.setParent(parent.getModel());
        fileModel.setUrl(normalizedUrl);

        try {
            fileOperationProviderMap.get(fileModel.requiredFileOperationProviderType()).updateModel(client, fileModel);
        } catch (FileNotFoundException e) {
        }

        if (fileModel.isDirectory()) {
            instanceClass = UrlDirectory.class;
        } else if (fileModel.isArchive())
            instanceClass = UrlArchive.class;
        else
            instanceClass = UrlFile.class;

        Constructor constructor = null;
        try {
            constructor = instanceClass.getConstructor(File.class, URL.class, FileModel.class, Map.class, Client.class);
            instance = (UrlFile) constructor.newInstance(parent, normalizedUrl, fileModel, fileOperationProviderMap, client);
        } catch (ReflectiveOperationException e) {
            throw new VirtualFileException(e);
        }

        return instance;
    }

    public void removeFileFromCache(File file) {
        fileCache.remove(file.getUrl().toString());
    }
}
