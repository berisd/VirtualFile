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
import at.beris.virtualfile.config.ClientConfig;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.exception.FileNotFoundException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.logging.FileLoggingWrapper;
import at.beris.virtualfile.operation.*;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class FileContext {
    private Configurator config;

    private Map<String, Site> siteMap;
    private Map<Site, Map<FileType, FileOperationProvider>> siteToFileOperationProvidersMap;
    private Map<FileOperationProvider, Map<FileOperationEnum, FileOperation>> fileOperationProviderToOperationMap;
    private Map<String, File> fileCache;

    public FileContext(Configurator config) {
        registerProtocolURLStreamHandlers();

        this.config = config;
        this.siteMap = new HashMap<>();
        this.siteToFileOperationProvidersMap = Collections.synchronizedMap(new HashMap<Site, Map<FileType, FileOperationProvider>>());
        this.fileOperationProviderToOperationMap = Collections.synchronizedMap(new HashMap<FileOperationProvider, Map<FileOperationEnum, FileOperation>>());
        this.fileCache = Collections.synchronizedMap(new LRUMap<String, File>(config.getBaseConfig().getFileCacheSize()));
    }

    public Configurator getConfig() {
        return config;
    }

    /**
     * Creates a local file. (Convenience method)
     *
     * @param path
     * @return
     */
    public File newLocalFile(String path) {
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
    public File newFile(String url) {
        try {
            return newFile((File) null, new URL(url));
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    public File newFile(URL parentUrl, URL url) {
        return newFile(newFile(parentUrl), url);
    }

    /**
     * Creates a file instance for the corresponding url
     *
     * @param parent
     * @param url
     * @return
     */
    public File newFile(File parent, URL url) {
        return createFile(parent, url);
    }

    public File newFile(URL url) {
        URL normalizedUrl = FileUtils.normalizeUrl(url);
        String fullPath = normalizedUrl.getPath();
        File parentFile = null;

        String[] pathParts;
        if (fullPath.equals("/"))
            return newFile((File) null, normalizedUrl);
        else {
            pathParts = fullPath.split("/");
            String path = "";
            for (int i = 0; i < pathParts.length; i++) {
                path += pathParts[i];

                if ((i < pathParts.length - 1) || fullPath.endsWith("/"))
                    path += "/";

                try {
                    String pathUrlString = UrlUtils.getSiteUrlString(normalizedUrl) + path;
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

    public void removeFileFromCache(File file) {
        fileCache.remove(file.getUrl().toString());
    }

    public void dispose(File file) {
        removeFileFromCache(file);
        file.dispose();
    }

    public Set<Protocol> enabledProtocols() {
        //TODO Only return enabled protocols
        return EnumSet.allOf(Protocol.class);
    }

    Site getSite(URL url) {
        return siteMap.get(UrlUtils.getSiteUrlString(url));
    }

    Map<FileOperationEnum, FileOperation> getFileOperationMap(URL url) {
        FileOperationProvider fileOperationProvider = getFileOperationProvider(url);

        if (fileOperationProvider != null)
            return fileOperationProviderToOperationMap.get(fileOperationProvider);
        return null;
    }

    FileOperationProvider getFileOperationProvider(URL url) {
        Site site = getSite(url);
        FileType fileType = UrlUtils.getFileTypeForUrl(url);

        if (site != null && fileType != null) {
            Map<FileType, FileOperationProvider> fileOperationProviderMap = siteToFileOperationProvidersMap.get(site);
            if (fileOperationProviderMap != null)
                return this.siteToFileOperationProvidersMap.get(site).get(fileType);
        }
        return null;
    }

    private File createFile(File parent, URL url) {
        URL normalizedUrl = FileUtils.normalizeUrl(url);
        Protocol protocol = UrlUtils.getProtocol(normalizedUrl);
        if (config.getFileOperationProviderClassMap(protocol) == null)
            throw new VirtualFileException("No configuration found for protocol: " + protocol);

        File file = null;
        try {
            FileType fileType = UrlUtils.getFileTypeForUrl(normalizedUrl);
            initSite(normalizedUrl);
            initFileOperationProvider(normalizedUrl, protocol, fileType, getSite(normalizedUrl));
            FileOperationProvider fileOperationProvider = this.getFileOperationProvider(url);
            initFileOperationMap(protocol, fileOperationProvider);

            String urlString = normalizedUrl.toString();
            file = fileCache.get(urlString);
            if (file == null) {
                FileModel fileModel = createModelInstance(parent, normalizedUrl, fileOperationProvider);
                file = createFileInstance(parent, normalizedUrl, fileModel);
                fileCache.put(urlString, file);
            }
        } catch (InstantiationException e) {
            throw new VirtualFileException(e);
        } catch (IllegalAccessException e) {
            throw new VirtualFileException(e);
        }
        return file;
    }

    private Client createClientInstance(Class clientClass, ClientConfig clientConfig) {
        Client client = null;

        if (clientClass != null) {
            try {
                Constructor constructor = clientClass.getConstructor(ClientConfig.class);
                client = (Client) constructor.newInstance(clientConfig);
            } catch (ReflectiveOperationException e) {
                throw new VirtualFileException(e);
            }
        }
        return client;
    }

    private Site createSiteInstance(String siteUrlString) {
        try {
            URL url = new URL(siteUrlString);
            Protocol protocol = UrlUtils.getProtocol(url);

            if (protocol == Protocol.FILE)
                return new LocalSite();
            else {
                Constructor constructor = UrlSite.class.getConstructor(URL.class);
                RemoteSite site = (RemoteSite) constructor.newInstance(url);

                Class clientClass = config.getClientClass(protocol);

                ClientConfig siteConfig = config.getClientConfig(site);
                if (siteConfig == null) {
                    siteConfig = config.createClientConfig(site);
                    config.setClientConfig(siteConfig, site);
                }

                Client client = createClientInstance(clientClass, siteConfig);
                site.setClient(client);
                return site;
            }
        } catch (ReflectiveOperationException e) {
            throw new VirtualFileException(e);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    private File createFileInstance(File parent, URL normalizedUrl, FileModel fileModel) {
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
            constructor = instanceClass.getConstructor(File.class, URL.class, FileModel.class, FileContext.class);
            instance = (UrlFile) constructor.newInstance(parent, normalizedUrl, fileModel, this);
        } catch (ReflectiveOperationException e) {
            throw new VirtualFileException(e);
        }

        return new FileLoggingWrapper(instance);
    }

    private FileModel createModelInstance(File parent, URL normalizedUrl, FileOperationProvider fileOperationProvider) {
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

    private FileOperationProvider createFileOperationProviderInstance(Class instanceClass, Site site, FileType fileType) throws InstantiationException, IllegalAccessException {
        FileOperationProvider instance = null;

        Constructor constructor = null;
        try {
            constructor = instanceClass.getConstructor(this.getClass(), Site.class);
            instance = (FileOperationProvider) constructor.newInstance(this, site);
        } catch (ReflectiveOperationException e) {
            throw new VirtualFileException(e);
        }

        return instance;
    }

    private void initFileOperationMap(Protocol protocol, FileOperationProvider fileOperationProvider) {
        Map<FileOperationEnum, FileOperation> fileOperationMap = fileOperationProviderToOperationMap.get(fileOperationProvider);
        if (fileOperationMap == null) {
            fileOperationMap = createFileOperationMap(protocol, fileOperationProvider);
            fileOperationProviderToOperationMap.put(fileOperationProvider, fileOperationMap);
        }
    }

    private void initSite(URL url) {
        Site site = getSite(url);
        if (site == null) {
            String siteUrlString = UrlUtils.getSiteUrlString(url);
            site = createSiteInstance(siteUrlString);
            siteMap.put(siteUrlString, site);
        }
    }

    private void initFileOperationProvider(URL normalizedUrl, Protocol protocol, FileType fileType, Site site) throws InstantiationException, IllegalAccessException {
        FileOperationProvider fileOperationProvider = getFileOperationProvider(normalizedUrl);
        if (fileOperationProvider == null) {
            Map<FileType, FileOperationProvider> fileOperationProviderMap = siteToFileOperationProvidersMap.get(site);
            if (fileOperationProviderMap == null) {
                fileOperationProviderMap = new HashMap<>();
                siteToFileOperationProvidersMap.put(site, fileOperationProviderMap);
            }

            Class instanceClass = config.getFileOperationProviderClassMap(protocol).get(fileType);
            fileOperationProvider = createFileOperationProviderInstance(instanceClass, site, fileType);
            fileOperationProviderMap.put(fileType, fileOperationProvider);
        }
    }
}
