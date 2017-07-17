/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.cache.FileCache;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.client.ClientConfiguration;
import at.beris.virtualfile.content.charset.CharsetDetector;
import at.beris.virtualfile.content.detect.Detector;
import at.beris.virtualfile.content.mime.MimeTypes;
import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.AbstractFileOperationProvider;
import at.beris.virtualfile.provider.ArchiveOperationProvider;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.DisposableObject;
import at.beris.virtualfile.util.ReflectionUtils;
import at.beris.virtualfile.util.StringUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static at.beris.virtualfile.util.CollectionUtils.removeEntriesByValueFromMap;
import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

/**
 * Internal class for managing and caching virtual files and their relations.
 */
public class UrlFileContext {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UrlFileContext.class);

    private Detector contentDetector;
    private CharsetDetector charsetDetector;

    private Map<String, Client> siteUrlToClientMap;
    private Map<Site, FileOperationProvider> siteToFileOperationProviderMap;
    private FileCache fileCache;
    private Map<UrlFile, UrlFile> fileToParentFileMap;
    private ArchiveOperationProvider archiveOperationProvider;

    private Configuration configuration;
    private KeyStoreManager keyStoreManager;
    private SiteManager siteManager;
    private SiteManager temporarySiteManager;
    private Map<Protocol, Class> fileOperationProviderClassMap;

    private UrlFileContext() {
    }

    UrlFileContext(Configuration configuration, SiteManager siteManager, KeyStoreManager keyStoreManager) {
        this.configuration = configuration;
        configuration.setCallbackHandler(new CustomConfigurationCallbackHandler());
        this.keyStoreManager = keyStoreManager;

        fileOperationProviderClassMap = createFileOperationProviderClassMap();

        this.siteUrlToClientMap = new HashMap<>();
        this.siteToFileOperationProviderMap = new HashMap<>();
        this.fileToParentFileMap = new HashMap();

        this.siteManager = siteManager;
        this.temporarySiteManager = SiteManager.create(configuration, keyStoreManager);

        fileCache = new FileCache(configuration.getFileCacheSize());
        fileCache.setCallbackHandler(new CustomFileCacheCallbackHandler());
    }

    private Map<Protocol, Class> createFileOperationProviderClassMap() {
        Map<Protocol, Class> map = new HashMap<>();
        try {
            List<Class> fileOperationProviderClasses = ReflectionUtils.findSubClassesOfClassInPackage(AbstractFileOperationProvider.class);
            for (Class fileOperationProviderClass : fileOperationProviderClasses) {
                String simpleClassName = fileOperationProviderClass.getSimpleName();
                String firstWord = StringUtils.EMPTY_STRING;

                String[] words = StringUtils.getWordsFromCamelCaseString(simpleClassName);
                if (words.length > 0)
                    firstWord = words[0];

                String protocolString = firstWord.equals("Local") ? "FILE" : firstWord.toUpperCase();
                Protocol protocol = Protocol.valueOf(protocolString);
                map.put(protocol, fileOperationProviderClass);
            }
            return map;
        } catch (IOException | ClassNotFoundException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Creates a UrlFile instance for the given url.
     *
     * @param url URL
     * @return New File Instance
     */
    public UrlFile resolveFile(URL url) {
        LOGGER.debug("resolveFile (url: {}) ", maskedUrlString(url));
        URL normalizedUrl = UrlUtils.normalizeUrl(url);
        if ("".equals(normalizedUrl.getPath()))
            normalizedUrl = UrlUtils.newUrl(normalizedUrl.toString() + "/");

        String fullPath = normalizedUrl.getPath();
        UrlFile parentFile = null;
        UrlFile file = null;
        StringBuilder stringBuilder = new StringBuilder();

        String[] pathParts = "/".equals(fullPath) ? new String[]{"/"} : fullPath.split("/");
        for (String pathPart : pathParts) {
            stringBuilder.append(pathPart);
            if (stringBuilder.length() < fullPath.length())
                stringBuilder.append('/');

            String pathUrlString = SiteManager.getSiteUrlString(normalizedUrl) + stringBuilder.toString();
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

    public UrlFile resolveFile(Site site, String path, boolean isDirectory) {
        try {
            initFileOperationProvider(site);

            Client client = (Client) siteToFileOperationProviderMap.get(site).getClient();

            String filePath = StringUtils.EMPTY_STRING;
            if (!path.startsWith("/") || StringUtils.isEmpty(path)) {
                filePath = client.getCurrentDirectory();
                if (!filePath.endsWith("/")) {
                    filePath += '/';
                }
            }
            filePath += path;

            if (isDirectory && !StringUtils.isEmpty(path) && !path.endsWith("/")) {
                filePath += '/';
            }

            URL fileUrl = UrlUtils.normalizeUrl(new URL(siteManager.getSiteUrlString(site) + filePath));

            return createFileInstance(fileUrl);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Replace URL of a UrlFile with a new URL.
     *
     * @param oldUrl Old URL
     * @param newUrl New URL
     */
    public void replaceFileUrl(URL oldUrl, URL newUrl) {
        UrlFile file = fileCache.get(oldUrl.toString());
        removeEntriesByValueFromMap(fileToParentFileMap, file);
        fileToParentFileMap.remove(file.getUrl().toString());
        fileCache.remove(oldUrl.toString());
        file.setUrl(newUrl);
        fileCache.put(newUrl.toString(), file);
    }

    /**
     * Removes a UrlFile from the content and frees it's allocated resources.
     *
     * @param file UrlFile
     */
    public void dispose(UrlFile file) {
        LOGGER.debug("dispose (file : {})", file);
        removeEntriesByValueFromMap(fileToParentFileMap, file);
        fileCache.remove(file.getUrl().toString());
        file.dispose();
    }

    /**
     * Frees all resources allocated by the file content.
     */
    public void dispose() {
        fileToParentFileMap.clear();
        fileCache.clear();
        siteToFileOperationProviderMap.clear();
        temporarySiteManager.dispose();
        disposeMap(siteUrlToClientMap);
    }

    /**
     * Gets parent file of the UrlFile.
     *
     * @param file File
     * @return Parent file
     */
    public UrlFile getParentFile(UrlFile file) {
        UrlFile parentFile = fileToParentFileMap.get(file);

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
     * @param siteUrlString at.beris.virtualfile.Site UrlString
     * @return Client
     */
    public Client getClient(String siteUrlString) {
        return siteUrlToClientMap.get(siteUrlString);
    }

    /**
     * Gets FileOperationProvider for the URL.
     *
     * @param url URL
     * @return FileOperationProvider
     */
    public FileOperationProvider getFileOperationProvider(URL url) {
        Site site = siteManager.getSiteForUrl(url);

        if (site == null)
            site = temporarySiteManager.getSiteForUrl(url);

        return siteToFileOperationProviderMap.get(site);
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

    public Detector getContentDetector() {
        if (contentDetector == null) {
            contentDetector = MimeTypes.getDefaultMimeTypes();
        }
        return contentDetector;
    }

    public CharsetDetector getCharsetDetector() {
        if (charsetDetector == null) {
            charsetDetector = new CharsetDetector();
        }
        return charsetDetector;
    }

    public ArchiveOperationProvider getArchiveOperationProvider() {
        if (archiveOperationProvider == null) {
            archiveOperationProvider = new ArchiveOperationProvider(this);
        }
        return archiveOperationProvider;
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    SiteManager getSiteManager() {
        return this.siteManager;
    }

    public void save() {
        configuration.save();
        keyStoreManager.save();
        siteManager.save();
    }

    Client createClientInstance(URL url) {
        Protocol protocol = UrlUtils.getProtocol(url);
        if (protocol == Protocol.FILE)
            return null;

        Class fileOperationProviderClass = fileOperationProviderClassMap.get(protocol);
        Class clientClass = getClientClass(fileOperationProviderClass);
        Constructor clientClassConstructor = getClientConstructor(clientClass);
        Class clientConfigurationClass = getClientConfigurationClass(clientClassConstructor);

        try {
            ClientConfiguration clientConfiguration = createClientConfiguration(url, clientConfigurationClass);

            return (Client) clientClassConstructor.newInstance(clientConfiguration);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new VirtualFileException(e);
        }
    }

    ClientConfiguration createClientConfiguration(URL url, Class clientConfigurationClass) {
        try {
            Protocol protocol = UrlUtils.getProtocol(url);
            ClientConfiguration clientConfiguration = (ClientConfiguration) clientConfigurationClass.newInstance();
            clientConfiguration.fillFromClientConfiguration(configuration.getClientConfiguration(protocol));
            Site site = siteManager.getSiteForUrlString(url);
            if (site != null) {
                clientConfiguration.fillFromSite(site);
            }
            clientConfiguration.fillFromUrl(url);
            return clientConfiguration;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new VirtualFileException(e);
        }
    }

    private UrlFile createFile(URL url) {
        LOGGER.debug("createFile (url : {})", maskedUrlString(url));

        Protocol protocol = UrlUtils.getProtocol(url);
        if (fileOperationProviderClassMap.get(protocol) == null)
            throw new VirtualFileException(Message.PROTOCOL_NOT_CONFIGURED(protocol));

        try {
            initFileOperationProvider(url);
            return createFileInstance(url);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new VirtualFileException(e);
        }
    }

    private UrlFile createFileInstance(URL url) {
        LOGGER.debug("createFileInstance (url: {})", maskedUrlString(url));

        try {
            Constructor constructor = getUrlFileConstructor();
            return (UrlFile) constructor.newInstance(url, this);
        } catch (ReflectiveOperationException e) {
            throw new VirtualFileException(e);
        }
    }

    private FileOperationProvider createFileOperationProviderInstance(Class instanceClass, Client client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("createFileOperationProviderInstance (instanceClass: {}, client: {})", instanceClass, client);
        try {
            Class clientClass = client != null ? client.getClass() : Client.class;
            Constructor constructor = instanceClass.getConstructor(UrlFileContext.class, clientClass);
            return (FileOperationProvider) constructor.newInstance(this, client);
        } catch (ReflectiveOperationException e) {
            throw new VirtualFileException(e);
        }
    }

    private void initFileOperationProvider(URL url) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("Initialize FileOperationProvider for URL {}", maskedUrlString(url));
        Protocol protocol = UrlUtils.getProtocol(url);
        FileOperationProvider fileOperationProvider = getFileOperationProvider(url);
        if (fileOperationProvider == null) {
            Client client = createClientInstance(UrlUtils.newUrl(url.toString()));
            Class instanceClass = fileOperationProviderClassMap.get(protocol);
            fileOperationProvider = createFileOperationProviderInstance(instanceClass, client);

            Site site = siteManager.getSiteForUrlString(url);
            if (site == null) {
                site = temporarySiteManager.getSiteForUrlString(url);
                if (site == null) {
                    site = Site.create();
                    site.fillFromUrl(url);
                    temporarySiteManager.addSite(site);
                }
            }

            siteToFileOperationProviderMap.put(site, fileOperationProvider);
        }
    }

    private void initFileOperationProvider(Site site) {
        LOGGER.debug("iInitialize FileOperationProvider for site {})", site);
        Protocol protocol = site.getProtocol();

        FileOperationProvider fileOperationProvider = siteToFileOperationProviderMap.get(site);
        if (fileOperationProvider == null) {
            Class fileOperationProviderClass = fileOperationProviderClassMap.get(protocol);
            Class clientClass = getClientClass(fileOperationProviderClass);
            Constructor clientClassConstructor = getClientConstructor(clientClass);
            Class clientConfigurationClass = getClientConfigurationClass(clientClassConstructor);

            try {
                ClientConfiguration clientConfiguration = (ClientConfiguration) clientConfigurationClass.newInstance();
                clientConfiguration.fillFromSite(site);
                Client client = (Client) clientClassConstructor.newInstance(clientConfiguration);
                fileOperationProvider = createFileOperationProviderInstance(fileOperationProviderClass, client);
                siteToFileOperationProviderMap.put(site, fileOperationProvider);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                throw new VirtualFileException(e);
            }
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

    private Constructor getUrlFileConstructor() {
        Class[] requiredParameterTypes = {URL.class, UrlFileContext.class};

        for (Constructor<?> constructor : UrlFile.class.getDeclaredConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), requiredParameterTypes))
                return constructor;
        }
        throw new VirtualFileException(Message.CONSTRUCTOR_NOT_FOUND(UrlFile.class.getName()));
    }

    private Class getClientClass(Class fileOperationProviderClass) {
        return fileOperationProviderClass.getConstructors()[0].getParameterTypes()[1];
    }

    private Constructor getClientConstructor(Class clientClass) {
        return clientClass.getConstructors()[0];
    }

    private Class getClientConfigurationClass(Constructor clientClassConstructor) {
        return clientClassConstructor.getParameterTypes()[0];
    }

    private class CustomFileCacheCallbackHandler implements FileCache.CallbackHandler {

        @Override
        public void afterEntryPurged(UrlFile file) {
            dispose(file);
        }
    }

    private class CustomConfigurationCallbackHandler implements Configuration.CallbackHandler {

        @Override
        public void changedFileCacheSize(int newSize) {
            fileCache.setMaxSize(newSize);
        }
    }
}
