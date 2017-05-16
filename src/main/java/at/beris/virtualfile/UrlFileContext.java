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
import at.beris.virtualfile.client.VirtualClient;
import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.ArchiveOperationProvider;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.txt.CharsetDetector;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static at.beris.virtualfile.util.CollectionUtils.removeEntriesByValueFromMap;
import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

/**
 * Internal class for managing and caching virtual files and their relations.
 */
public class UrlFileContext {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UrlFileContext.class);

    private Configurator configurator;
    private DefaultDetector contentDetector;
    private CharsetDetector charsetDetector;

    private Map<String, VirtualClient> siteUrlToClientMap;
    private Map<VirtualClient, FileOperationProvider> clientToFileOperationProviderMap;
    private FileCache fileCache;
    private Map<VirtualFile, VirtualFile> fileToParentFileMap;
    private ArchiveOperationProvider archiveOperationProvider;

    public UrlFileContext() {
        this(new Configurator());
    }

    public UrlFileContext(Configurator configurator) {
        this.configurator = configurator;
        this.siteUrlToClientMap = new HashMap<>();
        this.clientToFileOperationProviderMap = new HashMap<>();
        this.fileToParentFileMap = new HashMap();

        fileCache = new FileCache(configurator.getContextConfiguration().getFileCacheSize());
        fileCache.setCallbackHandler(new CustomFileCacheCallbackHandlerHandler());
    }

    /**
     * Get the context configurator.
     *
     * @return Configurator
     */
    public Configurator getConfigurator() {
        return configurator;
    }

    /**
     * Creates a VirtualFile instance for the given url.
     *
     * @param url URL
     * @return New File Instance
     */
    public VirtualFile resolveFile(URL url) {
        LOGGER.debug("resolveFile (url: {}) ", maskedUrlString(url));
        URL normalizedUrl = UrlUtils.normalizeUrl(url);
        if ("".equals(normalizedUrl.getPath()))
            normalizedUrl = UrlUtils.newUrl(normalizedUrl.toString() + "/");

        String fullPath = normalizedUrl.getPath();
        VirtualFile parentFile = null;
        VirtualFile file = null;
        StringBuilder stringBuilder = new StringBuilder();

        String[] pathParts = "/".equals(fullPath) ? new String[]{"/"} : fullPath.split("/");
        for (String pathPart : pathParts) {
            stringBuilder.append(pathPart);
            if (stringBuilder.length() < fullPath.length())
                stringBuilder.append('/');

            String pathUrlString = UrlUtils.getSiteUrlString(normalizedUrl.toString()) + stringBuilder.toString();
            try {
                URL pathUrl = UrlUtils.normalizeUrl(new URL(pathUrlString));
                file = fileCache.get(pathUrl.toString());
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

    /**
     * Replace URL of a VirtualFile with a new URL.
     *
     * @param oldUrl Old URL
     * @param newUrl New URL
     */
    public void replaceFileUrl(URL oldUrl, URL newUrl) {
        VirtualFile file = fileCache.get(oldUrl.toString());
        removeEntriesByValueFromMap(fileToParentFileMap, file);
        fileToParentFileMap.remove(file.getUrl().toString());
        fileCache.remove(oldUrl.toString());
        file.setUrl(newUrl);
        fileCache.put(newUrl.toString(), file);
    }

    /**
     * Removes a VirtualFile from the context and frees it's allocated resources.
     *
     * @param file VirtualFile
     */
    public void dispose(VirtualFile file) {
        LOGGER.debug("dispose (file : {})", file);
        removeEntriesByValueFromMap(fileToParentFileMap, file);
        fileCache.remove(file.getUrl().toString());
        file.dispose();
    }

    /**
     * Frees all resources allocated by the file context.
     */
    public void dispose() {
        fileToParentFileMap.clear();
        fileCache.clear();
        clientToFileOperationProviderMap.clear();
        disposeMap(siteUrlToClientMap);
    }

    /**
     * Gets parent file of the VirtualFile.
     *
     * @param file File
     * @return Parent file
     */
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

    /**
     * Gets client for a siteUrlString.
     *
     * @param siteUrlString Site UrlString
     * @return Client
     */
    public VirtualClient getClient(String siteUrlString) {
        return siteUrlToClientMap.get(siteUrlString);
    }

    /**
     * Gets FileOperationProvider for the URL string.
     *
     * @param urlString UrlString
     * @return FileOperationProvider
     */
    public FileOperationProvider getFileOperationProvider(String urlString) {
        VirtualClient client = siteUrlToClientMap.get(UrlUtils.getSiteUrlString(urlString));
        FileOperationProvider fileOperationProvider = clientToFileOperationProviderMap.get(client);
        return fileOperationProvider;
    }

    /**
     * Creates an empty file model.
     *
     * @return FileModel
     */
    public FileModel createFileModel() {
        return new FileModel();
    }

    public VirtualArchiveEntry createArchiveEntry() {
        return new FileArchiveEntry();
    }

    public DefaultDetector getContentDetector() {
        if (contentDetector == null) {
            contentDetector = new DefaultDetector();
        }
        return contentDetector;
    }

    public CharsetDetector getCharsetDetector() {
        if (charsetDetector == null) {
            charsetDetector = new CharsetDetector();
        }
        return charsetDetector;
    }

    private VirtualClient createClientInstance(URL url) {
        LOGGER.debug("createClientInstance (url: {})", maskedUrlString(url));

        Class clientClass = configurator.getClientClass(UrlUtils.getProtocol(url));
        if (clientClass != null) {
            try {
                Configuration configuration = configurator.createConfiguration(url);
                Constructor constructor = clientClass.getConstructor(URL.class, Configuration.class);
                return (VirtualClient) constructor.newInstance(url, configuration);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private VirtualFile createFile(URL url) {
        LOGGER.debug("createFile (url : {})", maskedUrlString(url));

        Protocol protocol = UrlUtils.getProtocol(url);
        if (configurator.getFileOperationProviderClass(protocol) == null)
            throw new VirtualFileException(Message.PROTOCOL_NOT_CONFIGURED(protocol));

        try {
            if (!EnumSet.of(Protocol.FILE).contains(protocol))
                initClient(url);
            initFileOperationProvider(url, protocol, getClient(UrlUtils.getSiteUrlString(url.toString())));
            return createFileInstance(url);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private VirtualFile createFileInstance(URL url) {
        LOGGER.debug("createFileInstance (url: {})", maskedUrlString(url));

        try {
            Constructor constructor = UrlFile.class.getConstructor(URL.class, UrlFileContext.class);
            return (VirtualFile) constructor.newInstance(url, this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private FileOperationProvider createFileOperationProviderInstance(Class instanceClass, VirtualClient client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("createFileOperationProviderInstance (instanceClass: {}, client: {})", instanceClass, client);
        try {
            Class clientClass = client != null ? client.getClass() : Client.class;
            Constructor constructor = instanceClass.getConstructor(UrlFileContext.class, clientClass);
            return (FileOperationProvider) constructor.newInstance(this, client);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void initClient(URL url) {
        LOGGER.debug("initClient(url: {}", maskedUrlString(url));
        String siteUrlString = UrlUtils.getSiteUrlString(url.toString());
        VirtualClient client = getClient(siteUrlString);
        if (client == null) {
            client = createClientInstance(UrlUtils.newUrl(siteUrlString));
            siteUrlToClientMap.put(siteUrlString, client);
        }
    }

    private void initFileOperationProvider(URL url, Protocol protocol, VirtualClient client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("initFileOperationProvider(url: {}, protocol: {}, client: {})", maskedUrlString(url), protocol, client);
        FileOperationProvider fileOperationProvider = getFileOperationProvider(url.toString());
        if (fileOperationProvider == null) {
            Class instanceClass = configurator.getFileOperationProviderClass(protocol);
            fileOperationProvider = createFileOperationProviderInstance(instanceClass, client);
            clientToFileOperationProviderMap.put(client, fileOperationProvider);
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

    public ArchiveOperationProvider getArchiveOperationProvider() {
        if (archiveOperationProvider == null) {
            archiveOperationProvider = new ArchiveOperationProvider(this);
        }
        return archiveOperationProvider;
    }

    private class CustomFileCacheCallbackHandlerHandler implements FileCacheCallbackHandler {

        @Override
        public void afterEntryPurged(VirtualFile file) {
            dispose(file);
        }
    }
}
