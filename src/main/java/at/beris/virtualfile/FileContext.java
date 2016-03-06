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
import at.beris.virtualfile.operation.*;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class FileContext {
    private FileConfig defaultFileConfig;

    private Map<String, Client> siteToClientMap;
    private Map<Client, Map<FileType, FileOperationProvider>> clientToFileOperationProvidersMap;
    private Map<FileOperationProvider, Map<FileOperationEnum, FileOperation>> fileOperationProviderToOperationMap;
    private Map<URL, FileConfig> fileConfigMap;
    private Map<String, File> fileCache;

    public FileContext(FileConfig fileConfig) {
        registerProtocolURLStreamHandlers();

        this.defaultFileConfig = fileConfig;
        this.fileConfigMap = new HashMap<>();
        this.siteToClientMap = Collections.synchronizedMap(new HashMap<String, Client>());
        this.clientToFileOperationProvidersMap = Collections.synchronizedMap(new HashMap<Client, Map<FileType, FileOperationProvider>>());
        this.fileOperationProviderToOperationMap = Collections.synchronizedMap(new HashMap<FileOperationProvider, Map<FileOperationEnum, FileOperation>>());
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

    public File newFile(URL parent, URL url) {
        return newFile(parent, url, null);
    }

    public File newFile(URL url) {
        return newFile(url, (FileConfig) null);
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
        Protocol protocol = getProtocol(normalizedUrl, fileConfig);

        UrlFile file = null;
        try {
            FileType fileType = getFileTypeForUrl(normalizedUrl);
            Client client = getClientInstance(url, protocol, fileConfig);
            FileOperationProvider fileOperationProvider = getFileOperationProviderInstance(fileConfig, protocol, fileType, client);
            Map<FileOperationEnum, FileOperation> fileOperationMap = getFileOperationMapInstance(protocol, fileOperationProvider);

            String urlString = normalizedUrl.toString();
            file = (UrlFile) fileCache.get(urlString);
            if (file == null) {
                FileModel fileModel = createModelInstance(parent, normalizedUrl, client, fileOperationProvider);
                file = createFileInstance(parent, normalizedUrl, fileModel, fileOperationMap);
                fileCache.put(urlString, file);
            }
        } catch (InstantiationException e) {
            throw new VirtualFileException(e);
        } catch (IllegalAccessException e) {
            throw new VirtualFileException(e);
        }
        return file;
    }

    private Map<FileOperationEnum, FileOperation> getFileOperationMapInstance(Protocol protocol, FileOperationProvider fileOperationProvider) {
        Map<FileOperationEnum, FileOperation> fileOperationMap = fileOperationProviderToOperationMap.get(fileOperationProvider);
        if (fileOperationMap == null) {
            fileOperationMap = createFileOperationMap(protocol, fileOperationProvider);
            fileOperationProviderToOperationMap.put(fileOperationProvider, fileOperationMap);
        }
        return fileOperationMap;
    }

    private FileOperationProvider getFileOperationProviderInstance(FileConfig fileConfig, Protocol protocol, FileType fileType, Client client) throws InstantiationException, IllegalAccessException {
        Map<FileType, FileOperationProvider> fileOperationProviderMap = clientToFileOperationProvidersMap.get(client);
        if (fileOperationProviderMap == null) {
            fileOperationProviderMap = new HashMap<>();
            clientToFileOperationProvidersMap.put(client, fileOperationProviderMap);
        }

        FileOperationProvider fileOperationProvider = fileOperationProviderMap.get(fileType);

        if (fileOperationProvider == null) {
            fileOperationProvider = createFileOperationProvider(fileConfig, protocol, client, fileType);
            fileOperationProviderMap.put(fileType, fileOperationProvider);
        }
        return fileOperationProvider;
    }

    private FileType getFileTypeForUrl(URL url) {
        FileType fileType = FileType.DEFAULT;
        String[] pathParts = url.toString().split("/");

        if (FileUtils.isArchive(pathParts[pathParts.length - 1]))
            fileType = FileType.ARCHIVE;
        else if (FileUtils.isArchived(url))
            fileType = FileType.ARCHIVED;
        return fileType;
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

    public void removeFileFromCache(File file) {
        fileCache.remove(file.getUrl().toString());
    }

    public Set<Protocol> enabledProtocols() {
        //TODO Only return enabled protocols
        return EnumSet.allOf(Protocol.class);
    }

    private Client createClientInstance(URL url, Protocol protocol, FileConfig fileConfig) {
        Client client = null;
        Class clientClass = fileConfig.getClientClass(protocol);
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

    private UrlFile createFileInstance(File parent, URL normalizedUrl, FileModel fileModel, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        Class instanceClass;
        UrlFile instance;

        if (fileModel.isDirectory()) {
            instanceClass = UrlDirectory.class;
        } else if (fileModel.isArchive())
            instanceClass = UrlArchive.class;
        else
            instanceClass = UrlFile.class;

        Constructor constructor = null;
        try {
            constructor = instanceClass.getConstructor(File.class, URL.class, FileModel.class, Map.class);
            instance = (UrlFile) constructor.newInstance(parent, normalizedUrl, fileModel, fileOperationMap);
        } catch (ReflectiveOperationException e) {
            throw new VirtualFileException(e);
        }

        return instance;
    }

    private FileModel createModelInstance(File parent, URL normalizedUrl, Client client, FileOperationProvider fileOperationProvider) {
        FileModel fileModel = new FileModel();
        if (parent != null)
            fileModel.setParent(parent.getModel());
        fileModel.setUrl(normalizedUrl);

        try {
            fileOperationProvider.updateModel(fileModel);
        } catch (FileNotFoundException e) {
        }
        return fileModel;
    }

    private Map<FileOperationEnum, FileOperation> createFileOperationMap(Protocol protocol, FileOperationProvider fileOperationProvider) {
        //TODO Allow file operations supported by the protocol only
        HashMap<FileOperationEnum, FileOperation> map = new HashMap<>();
        map.put(FileOperationEnum.ADD_ATTRIBUTES, new AddAttributesOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.CHECKSUM, new ChecksumOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.COPY, new CopyOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.CREATE, new CreateOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.DELETE, new DeleteOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.EXISTS, new ExistsOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.EXTRACT, new ExtractOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.GET_INPUT_STREAM, new GetInputStreamOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.GET_OUTPUT_STREAM, new GetOutputStreamOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.LIST, new ListOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.REMOVE_ATTRIBUTES, new RemoveAttributesOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.SET_ACL, new SetAclOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.SET_ATTRIBUTES, new SetAttributesOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.SET_CREATION_TIME, new SetCreationTimeOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.SET_GROUP, new SetGroupOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.SET_LAST_ACCESS_TIME, new SetLastAccessTimeOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.SET_LAST_MODIFIED_TIME, new SetLastModifiedTimeOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.SET_OWNER, new SetOwnerOperation(this, fileOperationProvider));
        map.put(FileOperationEnum.UPDATE_MODEL, new UpdateModelOperation(this, fileOperationProvider));

        return map;
    }

    private FileOperationProvider createFileOperationProvider(FileConfig fileConfig, Protocol protocol, Client client, FileType fileType) throws InstantiationException, IllegalAccessException {
        Class instanceClass = fileConfig.getFileOperationProviderClassMap(protocol).get(fileType);
        FileOperationProvider instance = null;

        Constructor constructor = null;
        try {
            constructor = instanceClass.getConstructor(this.getClass(), Client.class);
            instance = (FileOperationProvider) constructor.newInstance(this, client);
        } catch (ReflectiveOperationException e) {
            throw new VirtualFileException(e);
        }

        return instance;
    }

    private Client getClientInstance(URL url, Protocol protocol, FileConfig fileConfig) {
        if (protocol==Protocol.FILE)
            return null;

        String siteUrlString = getSiteUrlString(url);
        Client client = siteToClientMap.get(siteUrlString);

        if (client == null) {
            client = createClientInstance(url, protocol, fileConfig);
            siteToClientMap.put(siteUrlString, client);
        }
        return client;
    }

    private Protocol getProtocol(URL normalizedUrl, FileConfig fileConfig) {
        Protocol protocol = null;
        String protocolString = normalizedUrl.getProtocol();

        try {
            protocol = Protocol.valueOf(normalizedUrl.getProtocol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VirtualFileException("Unknown protocol: " + protocolString);
        }

        if (fileConfig.getFileOperationProviderClassMap(protocol) == null)
            throw new VirtualFileException("No configuration found for protocol: " + protocolString);
        return protocol;
    }
}
