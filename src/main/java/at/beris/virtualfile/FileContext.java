/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.cache.LRUMap;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;

import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

public class FileContext {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FileContext.class);

    private Configurator configurator;

    private Map<String, Client> siteUrlToClientMap;
    private Map<Client, Map<FileType, FileOperationProvider>> clientToFileOperationProvidersMap;
    private Map<String, VirtualFile> fileCache;
    private Map<VirtualFile, VirtualFile> fileToParentFileMap;

    public FileContext() {
        this(new Configurator());
    }

    public FileContext(Configurator configurator) {
        UrlUtils.registerProtocolURLStreamHandlers();

        this.configurator = configurator;
        this.siteUrlToClientMap = new HashMap<>();
        this.clientToFileOperationProvidersMap = new HashMap<>();
        this.fileToParentFileMap = new HashMap();
        this.fileCache = new LRUMap<>(configurator.getContextConfiguration().getFileCacheSize());
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
    public VirtualFile newLocalFile(String path) throws IOException {
        LOGGER.debug("newLocalFile (path: {})", path);
        URL url = new File(path).toURI().toURL();
        if (path.endsWith(File.separator))
            url = new URL(url.toString() + "/");
        return newFile(url);
    }

    /**
     * Creates a file. (Convenience method)
     *
     * @param urlString
     * @return
     */
    public VirtualFile newFile(String urlString) throws IOException {
        return newFile(new URL(urlString));
    }

    /**
     * Creates a file instance for the corresponding url
     *
     * @param url
     * @return
     * @throws IOException
     */
    public VirtualFile newFile(URL url) throws IOException {
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
            URL pathUrl = UrlUtils.normalizeUrl(new URL(pathUrlString));

            file = fileCache.get(pathUrl);
            if (file == null) {
                file = createFile(pathUrl);
                fileCache.put(pathUrl.toString(), file);
            }

            if (parentFile != null) {
                fileToParentFileMap.put(file, parentFile);
            }

            parentFile = file;
        }
        return file;
    }

    public void replaceFileUrl(URL oldUrl, URL newUrl) throws IOException {
        VirtualFile file = fileCache.get(oldUrl.toString());
        fileCache.remove(oldUrl.toString());
        file.setUrl(newUrl);
        fileCache.put(newUrl.toString(), file);
    }

    public void removeFileFromCache(VirtualFile file) throws IOException {
        LOGGER.debug("removeFileFromCache (file : {})", file);
        fileCache.remove(file.getUrl().toString());
    }

    public void dispose(VirtualFile file) throws IOException {
        LOGGER.debug("dispose (file : {})", file);
        removeFileFromCache(file);
        file.dispose();
    }

    public void dispose() throws IOException {
        disposeFileCache();
        disposeClientToFileOperationProvidersMap();
        disposeSiteUrlToClientMap();
    }

    public Set<Protocol> enabledProtocols() {
        Map<Protocol, Pair<String, String>> protocolClassMap = new HashMap<>();
        protocolClassMap.put(Protocol.SFTP, Pair.of("JSch", "com.jcraft.jsch.JSch"));
        protocolClassMap.put(Protocol.FTP, Pair.of("Apache Commons Net", "org.apache.commons.net.ftp.FTP"));

        Set<Protocol> enabledProtocols = new HashSet<>();
        enabledProtocols.add(Protocol.FILE);

        for (Map.Entry<Protocol, Pair<String, String>> entry : protocolClassMap.entrySet()) {
            Protocol protocol = entry.getKey();
            Pair<String, String> protocolLibrary = entry.getValue();
            try {
                if (Class.forName(protocolLibrary.getRight()) != null)
                    enabledProtocols.add(protocol);
            } catch (ClassNotFoundException e) {
            }
            if (!enabledProtocols.contains(protocol))
                LOGGER.info(protocolLibrary.getLeft() + " not installed. No support for protocol " + protocol);
        }

        return Collections.unmodifiableSet(enabledProtocols);
    }

    VirtualFile getFile(String urlString) {
        return fileCache.get(urlString);
    }

    VirtualFile getParentFile(VirtualFile file) {
        return fileToParentFileMap.get(file);
    }

    Client getClient(String siteUrlString) {
        return siteUrlToClientMap.get(siteUrlString);
    }

    FileOperationProvider getFileOperationProvider(String urlString) {
        Client client = siteUrlToClientMap.get(UrlUtils.getSiteUrlString(urlString));
        FileType fileType = UrlUtils.getFileTypeForUrl(urlString);

        Map<FileType, FileOperationProvider> fileOperationProviderMap = clientToFileOperationProvidersMap.get(client);
        if (fileOperationProviderMap != null)
            return this.clientToFileOperationProvidersMap.get(client).get(fileType);
        return null;
    }

    private VirtualFile createFile(URL url) throws IOException {
        LOGGER.debug("createFile (url : {})", maskedUrlString(url));

        Protocol protocol = UrlUtils.getProtocol(url);
        if (configurator.getFileOperationProviderClassMap(protocol) == null)
            throw new IOException("No configuration found for protocol: " + protocol);

        try {
            FileType fileType = UrlUtils.getFileTypeForUrl(url.toString());
            if (protocol != Protocol.FILE)
                initClient(url);
            initFileOperationProvider(url, protocol, fileType, getClient(UrlUtils.getSiteUrlString(url.toString())));
            VirtualFile file = createFileInstance(url);
            return file;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Client createClientInstance(URL url) throws IOException {
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

    private VirtualFile createFileInstance(URL url) {
        LOGGER.debug("createFileInstance (url: {})", maskedUrlString(url));

        try {
            Constructor constructor = UrlFile.class.getConstructor(URL.class, FileContext.class);
            UrlFile instance = (UrlFile) constructor.newInstance(url, this);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private FileOperationProvider createFileOperationProviderInstance(Class instanceClass, Client client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("createFileOperationProviderInstance (instanceClass: {}, client: {})", instanceClass, client);

        try {
            Class clientClass = client != null ? client.getClass() : Client.class;
            Constructor constructor = instanceClass.getConstructor(this.getClass(), clientClass);
            return (FileOperationProvider) constructor.newInstance(this, client);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void initClient(URL url) throws IOException {
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

    private void disposeFileCache() throws IOException {
        Iterator<Map.Entry<String, VirtualFile>> it = fileCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, VirtualFile> entry = it.next();
            entry.getValue().dispose();
            it.remove();
        }
    }

    private void disposeClientToFileOperationProvidersMap() throws IOException {
        Iterator<Map.Entry<Client, Map<FileType, FileOperationProvider>>> it = clientToFileOperationProvidersMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Client, Map<FileType, FileOperationProvider>> entry = it.next();
            disposeFileTypeToFileOperationProviderMap(entry.getValue());
            it.remove();
        }
    }

    private void disposeFileTypeToFileOperationProviderMap(Map<FileType, FileOperationProvider> map) {
        Iterator<Map.Entry<FileType, FileOperationProvider>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<FileType, FileOperationProvider> next = it.next();
            next.getValue().dispose();
            it.remove();
        }
    }

    private void disposeSiteUrlToClientMap() throws IOException {
        Iterator<Map.Entry<String, Client>> it = siteUrlToClientMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Client> next = it.next();
            next.getValue().dispose();
            it.remove();
        }
    }
}
