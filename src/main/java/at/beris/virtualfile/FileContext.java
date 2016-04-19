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
import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.logging.FileLoggingWrapper;
import at.beris.virtualfile.operation.*;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;

public class FileContext {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FileContext.class);

    private Configurator configurator;

    private Map<String, Client> siteUrlToClientMap;
    private Map<Client, Map<FileType, FileOperationProvider>> clientToFileOperationProvidersMap;
    private Map<FileOperationProvider, Map<FileOperationEnum, FileOperation>> fileOperationProviderToOperationMap;
    private Map<String, File> fileCache;

    public FileContext() {
        this(new Configurator());
    }

    public FileContext(Configurator configurator) {
        registerProtocolURLStreamHandlers();

        this.configurator = configurator;
        this.siteUrlToClientMap = new HashMap<>();
        this.clientToFileOperationProvidersMap = Collections.synchronizedMap(new HashMap<Client, Map<FileType, FileOperationProvider>>());
        this.fileOperationProviderToOperationMap = Collections.synchronizedMap(new HashMap<FileOperationProvider, Map<FileOperationEnum, FileOperation>>());
        this.fileCache = Collections.synchronizedMap(new LRUMap<String, File>(configurator.getContextConfiguration().getFileCacheSize()));
    }

    public Configurator getConfigurator() {
        return configurator;
    }

    /**
     * Creates a local file. (Convenience method)
     *
     * @param path
     * @return
     */
    public File newLocalFile(String path) throws IOException {
        LOGGER.debug("newLocalFile (path: {})", path);
        URL url = new java.io.File(path).toURI().toURL();
        if (path.endsWith(java.io.File.separator))
            url = new URL(url.toString() + "/");
        return newFile(url);
    }

    /**
     * Creates a file. (Convenience method)
     *
     * @param urlString
     * @return
     */
    public File newFile(String urlString) throws IOException {
        LOGGER.debug("newFile (urlString: {})", urlString);
        return newFile((File) null, new URL(urlString));
    }

    /**
     * Creates a file instance for the corresponding url
     *
     * @param parent
     * @param url
     * @return
     */
    public File newFile(File parent, URL url) throws IOException {
        LOGGER.debug("newFile (parentFile: {}, url: {})", parent, url);
        return createFile(parent, url);
    }

    public File newFile(URL url) throws IOException {
        LOGGER.debug("newFile (url: {}) ", url);
        URL normalizedUrl = UrlUtils.normalizeUrl(url);
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

                String pathUrlString = UrlUtils.getSiteUrlString(normalizedUrl) + path;
                URL pathUrl = new URL(pathUrlString);
                parentFile = newFile(parentFile, pathUrl);
            }
        }
        return parentFile;
    }

    /**
     * Set property so that URL class will find custom handlers
     */
    public void registerProtocolURLStreamHandlers() {
        LOGGER.debug("registerProtocolURLStreamHandlers");
        String propertyKey = "java.protocol.handler.pkgs";
        String propertyValue = System.getProperties().getProperty(propertyKey);
        if (StringUtils.isEmpty(propertyValue))
            propertyValue = "";
        else
            propertyValue += "|";
        propertyValue += at.beris.virtualfile.protocol.Protocol.class.getPackage().getName();
        System.getProperties().setProperty(propertyKey, propertyValue);
    }

    public void removeFileFromCache(File file) throws IOException {
        LOGGER.debug("removeFileFromCache (file : {})", file);
        fileCache.remove(file.getUrl().toString());
    }

    public void dispose(File file) throws IOException {
        LOGGER.debug("dispose (file : {})", file);
        removeFileFromCache(file);
        file.dispose();
    }

    public Set<Protocol> enabledProtocols() {
        //TODO Only return enabled protocols
        return EnumSet.allOf(Protocol.class);
    }

    public File getFile(String urlString) {
        return fileCache.get(urlString);
    }

    Client getClient(URL url) {
        return siteUrlToClientMap.get(UrlUtils.getSiteUrlString(url));
    }

    Map<FileOperationEnum, FileOperation> getFileOperationMap(URL url) {
        FileOperationProvider fileOperationProvider = getFileOperationProvider(url);

        if (fileOperationProvider != null)
            return fileOperationProviderToOperationMap.get(fileOperationProvider);
        return null;
    }

    FileOperationProvider getFileOperationProvider(URL url) {
        Client client = siteUrlToClientMap.get(UrlUtils.getSiteUrlString(url));
        FileType fileType = UrlUtils.getFileTypeForUrl(url);

        Map<FileType, FileOperationProvider> fileOperationProviderMap = clientToFileOperationProvidersMap.get(client);
        if (fileOperationProviderMap != null)
            return this.clientToFileOperationProvidersMap.get(client).get(fileType);
        return null;
    }

    private File createFile(File parent, URL url) throws IOException {
        LOGGER.debug("createFile (parent: {}, url : {})", parent, url);
        URL normalizedUrl = UrlUtils.normalizeUrl(url);
        Protocol protocol = UrlUtils.getProtocol(normalizedUrl);

        if (configurator.getFileOperationProviderClassMap(protocol) == null)
            throw new IOException("No configuration found for protocol: " + protocol);

        String urlString = normalizedUrl.toString();
        File cachedFile = fileCache.get(urlString);
        if (cachedFile != null)
            return cachedFile;

        File file = null;
        try {
            FileType fileType = UrlUtils.getFileTypeForUrl(normalizedUrl);
            if (protocol != Protocol.FILE)
                initClient(normalizedUrl);
            initFileOperationProvider(normalizedUrl, protocol, fileType, getClient(normalizedUrl));
            FileOperationProvider fileOperationProvider = this.getFileOperationProvider(url);
            initFileOperationMap(protocol, fileOperationProvider);

            file = createFileInstance(parent, normalizedUrl);
            fileCache.put(urlString, file);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private Client createClientInstance(URL url) {
        LOGGER.debug("createClientInstance (url: {})", url);
        Client client = null;

        Class clientClass = configurator.getClientClass(UrlUtils.getProtocol(url));

        if (clientClass != null) {
            try {
                Configuration configuration = configurator.createConfiguration(url);
                Constructor constructor = clientClass.getConstructor(URL.class, Configuration.class);
                client = (Client) constructor.newInstance(url, configuration);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return client;
    }

    private File createFileInstance(File parent, URL url) {
        LOGGER.debug("createFileInstance (parent: {}, url: {})", parent, url);
        UrlFile instance;

        try {
            Constructor constructor = UrlFile.class.getConstructor(File.class, URL.class, FileContext.class);
            instance = (UrlFile) constructor.newInstance(parent, url, this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        return new FileLoggingWrapper(instance);
    }

    private Map<FileOperationEnum, FileOperation> createFileOperationMap(Protocol protocol, FileOperationProvider fileOperationProvider) {
        LOGGER.debug("createFileOperationMap (protocol: {}, fileOperationProvider: {})", protocol, fileOperationProvider);
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

    private FileOperationProvider createFileOperationProviderInstance(Class instanceClass, Client client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("createFileOperationProviderInstance (instanceClass: {}, client: {})", instanceClass, client);
        FileOperationProvider instance = null;

        Constructor constructor = null;
        try {
            constructor = instanceClass.getConstructor(this.getClass(), Client.class);
            instance = (FileOperationProvider) constructor.newInstance(this, client);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }

    private void initFileOperationMap(Protocol protocol, FileOperationProvider fileOperationProvider) {
        LOGGER.debug("initFileOperationMap(protocol: {}, fileOperationProvider : {})", protocol, fileOperationProvider);
        Map<FileOperationEnum, FileOperation> fileOperationMap = fileOperationProviderToOperationMap.get(fileOperationProvider);
        if (fileOperationMap == null) {
            fileOperationMap = createFileOperationMap(protocol, fileOperationProvider);
            fileOperationProviderToOperationMap.put(fileOperationProvider, fileOperationMap);
        }
    }

    private void initClient(URL url) {
        LOGGER.debug("initClient(url: {}", url);
        Client client = getClient(url);
        if (client == null) {
            client = createClientInstance(url);
            siteUrlToClientMap.put(UrlUtils.getSiteUrlString(url), client);
        }
    }

    private void initFileOperationProvider(URL url, Protocol protocol, FileType fileType, Client client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("initFileOperationProvider(url: {}, protocol: {}, fileType: {}, client: {})", url, protocol, fileType, client);
        FileOperationProvider fileOperationProvider = getFileOperationProvider(url);
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
}
