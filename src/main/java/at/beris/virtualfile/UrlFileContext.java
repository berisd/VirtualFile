/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.cache.DisposableObject;
import at.beris.virtualfile.cache.FileCache;
import at.beris.virtualfile.cache.FileCacheCallbackHandler;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.UrlUtils;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static at.beris.virtualfile.util.CollectionUtils.removeEntriesByValueFromMap;
import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

class UrlFileContext implements VirtualFileContext {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UrlFileContext.class);

    private Configurator configurator;

    private Map<String, Client> siteUrlToClientMap;
    private Map<Client, Map<FileType, FileOperationProvider>> clientToFileOperationProvidersMap;
    private Map<String, VirtualFile> fileCache;
    private Map<VirtualFile, VirtualFile> fileToParentFileMap;

    public UrlFileContext() {
        this(new Configurator(), new FileCache(1024));
    }

    public UrlFileContext(Configurator configurator, FileCache fileCache) {
        UrlUtils.registerProtocolURLStreamHandlers();

        this.configurator = configurator;
        this.siteUrlToClientMap = new HashMap<>();
        this.clientToFileOperationProvidersMap = new HashMap<>();
        this.fileToParentFileMap = new HashMap();

        fileCache.setSize(configurator.getContextConfiguration().getFileCacheSize());
        fileCache.setCallbackHandler(new CustomFileCacheCallbackHandlerHandler());
        this.fileCache = fileCache;
    }

    @Override
    public Configurator getConfigurator() {
        return configurator;
    }

    @Override
    public VirtualFile newFile(URL url) {
        LOGGER.debug("newFile (url: {}) ", maskedUrlString(url));
        URL normalizedUrl = UrlUtils.normalizeUrl(url);
        if ("".equals(normalizedUrl.getPath()))
            normalizedUrl = UrlUtils.newUrl(normalizedUrl.toString() + "/");

        String fullPath = normalizedUrl.getPath();
        VirtualFile parentFile = null;
        VirtualFile file = null;
        StringBuilder stringBuilder = new StringBuilder();

        for (String pathPart : fullPath.split("/")) {
            stringBuilder.append(pathPart);
            if (stringBuilder.length() < fullPath.length())
                stringBuilder.append('/');

            String pathUrlString = UrlUtils.getSiteUrlString(normalizedUrl.toString()) + stringBuilder.toString();
            try {
                URL pathUrl = UrlUtils.normalizeUrl(new URL(pathUrlString));
                file = fileCache.get(pathUrl);
                if (file == null) {
                    file = createFile(pathUrl);
                    fileCache.put(pathUrl.toString(), file);
                }
            } catch (MalformedURLException e) {
                throw new VirtualFileException(e);
            }

            if (parentFile != null) {
                fileToParentFileMap.put(file, parentFile);
            }

            parentFile = file;
        }
        return file;
    }

    @Override
    public void replaceFileUrl(URL oldUrl, URL newUrl) {
        VirtualFile file = fileCache.get(oldUrl.toString());
        removeEntriesByValueFromMap(fileToParentFileMap, file);
        fileToParentFileMap.remove(file.getUrl().toString());
        fileCache.remove(oldUrl.toString());
        file.setUrl(newUrl);
        fileCache.put(newUrl.toString(), file);
    }

    @Override
    public void dispose(VirtualFile file) {
        LOGGER.debug("dispose (file : {})", file);
        removeEntriesByValueFromMap(fileToParentFileMap, file);
        fileToParentFileMap.remove(file.getUrl().toString());
        fileCache.remove(file.getUrl().toString());
        file.dispose();
    }

    @Override
    public void dispose() {
        fileToParentFileMap.clear();
        disposeMap(fileCache);
        disposeClientToFileOperationProvidersMap();
        disposeMap(siteUrlToClientMap);
    }

    @Override
    public VirtualFile getParentFile(VirtualFile file) {
        VirtualFile parentFile = fileToParentFileMap.get(file);

        if (parentFile == null) {
            URL parentUrl = UrlUtils.getParentUrl(file.getUrl());
            if (parentUrl == null)
                return null;

            parentFile = fileCache.get(parentUrl.toString());
            if (parentFile == null) {
                parentFile = createFile(parentUrl);
                fileCache.put(parentUrl.toString(), parentFile);
            }
        }

        return fileToParentFileMap.get(file);
    }

    @Override
    public Client getClient(String siteUrlString) {
        return siteUrlToClientMap.get(siteUrlString);
    }

    @Override
    public FileOperationProvider getFileOperationProvider(String urlString) {
        Client client = siteUrlToClientMap.get(UrlUtils.getSiteUrlString(urlString));
        FileType fileType = UrlUtils.getFileTypeForUrl(urlString);

        Map<FileType, FileOperationProvider> fileOperationProviderMap = clientToFileOperationProvidersMap.get(client);
        if (fileOperationProviderMap != null)
            return this.clientToFileOperationProvidersMap.get(client).get(fileType);
        return null;
    }

    @Override
    public FileModel createFileModel() {
        return new FileModel();
    }

    private Client createClientInstance(URL url) {
        LOGGER.debug("createClientInstance (url: {})", maskedUrlString(url));

        Class clientClass = configurator.getClientClass(UrlUtils.getProtocol(url));
        if (clientClass != null) {
            try {
                Configuration configuration = configurator.createConfiguration(url);
                Constructor constructor = clientClass.getConstructor(URL.class, Configuration.class);
                return (Client) constructor.newInstance(url, configuration);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private VirtualFile createFile(URL url) {
        LOGGER.debug("createFile (url : {})", maskedUrlString(url));

        Protocol protocol = UrlUtils.getProtocol(url);
        if (configurator.getFileOperationProviderClassMap(protocol) == null)
            throw new VirtualFileException(Message.PROTOCOL_NOT_CONFIGURED(protocol));

        try {
            FileType fileType = UrlUtils.getFileTypeForUrl(url.toString());
            if (protocol != Protocol.FILE)
                initClient(url);
            initFileOperationProvider(url, protocol, fileType, getClient(UrlUtils.getSiteUrlString(url.toString())));
            return createFileInstance(url);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private VirtualFile createFileInstance(URL url) {
        LOGGER.debug("createFileInstance (url: {})", maskedUrlString(url));

        try {
            Constructor constructor = UrlFile.class.getConstructor(URL.class, UrlFileContext.class);
            return (UrlFile) constructor.newInstance(url, this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private FileOperationProvider createFileOperationProviderInstance(Class instanceClass, Client client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("createFileOperationProviderInstance (instanceClass: {}, client: {})", instanceClass, client);
        try {
            Class clientClass = client != null ? client.getClass() : Client.class;
            Constructor constructor = instanceClass.getConstructor(VirtualFileContext.class, clientClass);
            return (FileOperationProvider) constructor.newInstance(this, client);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void initClient(URL url) {
        LOGGER.debug("initClient(url: {}", maskedUrlString(url));
        String siteUrlString = UrlUtils.getSiteUrlString(url.toString());
        Client client = getClient(siteUrlString);
        if (client == null) {
            client = createClientInstance(UrlUtils.newUrl(siteUrlString));
            siteUrlToClientMap.put(siteUrlString, client);
        }
    }

    private void initFileOperationProvider(URL url, Protocol protocol, FileType fileType, Client client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("initFileOperationProvider(url: {}, protocol: {}, fileType: {}, client: {})", maskedUrlString(url), protocol, fileType, client);
        FileOperationProvider fileOperationProvider = getFileOperationProvider(url.toString());
        if (fileOperationProvider == null) {
            Map<FileType, FileOperationProvider> fileOperationProviderMap = clientToFileOperationProvidersMap.get(client);
            if (fileOperationProviderMap == null) {
                fileOperationProviderMap = new HashMap<>();
                clientToFileOperationProvidersMap.put(client, fileOperationProviderMap);
            }

            Class instanceClass = configurator.getFileOperationProviderClassMap(protocol).get(fileType);
            fileOperationProvider = createFileOperationProviderInstance(instanceClass, client);
            fileOperationProviderMap.put(fileType, fileOperationProvider);
        }
    }

    private <K, V extends DisposableObject> void disposeMap(Map<K, V> map) {
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            entry.getValue().dispose();
            it.remove();
        }
    }

    private void disposeClientToFileOperationProvidersMap() {
        Iterator<Map.Entry<Client, Map<FileType, FileOperationProvider>>> it = clientToFileOperationProvidersMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Client, Map<FileType, FileOperationProvider>> entry = it.next();
            disposeMap(entry.getValue());
            it.remove();
        }
    }

    private class CustomFileCacheCallbackHandlerHandler implements FileCacheCallbackHandler {

        @Override
        public void beforeEntryRemoved(VirtualFile value) {
            removeEntriesByValueFromMap(fileToParentFileMap, value);
        }
    }
}
