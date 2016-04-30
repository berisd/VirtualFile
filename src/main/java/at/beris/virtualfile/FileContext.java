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
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

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
    private Map<String, File> fileCache;

    public FileContext() {
        this(new Configurator());
    }

    public FileContext(Configurator configurator) {
        UrlUtils.registerProtocolURLStreamHandlers();

        this.configurator = configurator;
        this.siteUrlToClientMap = Collections.synchronizedMap(new HashMap<String, Client>());
        this.clientToFileOperationProvidersMap = Collections.synchronizedMap(new HashMap<Client, Map<FileType, FileOperationProvider>>());
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
        return newFile((File) null, new URL(urlString));
    }

    /**
     * Creates a file instance for the corresponding url
     *
     * @param url
     * @return
     * @throws IOException
     */
    public File newFile(URL url) throws IOException {
        LOGGER.debug("newFile (url: {}) ", maskedUrlString(url));
        URL normalizedUrl = UrlUtils.normalizeUrl(url);
        if ("".equals(normalizedUrl.getPath()))
            normalizedUrl = UrlUtils.newUrl(normalizedUrl.toString() + "/");

        String fullPath = normalizedUrl.getPath();
        if (fullPath.equals("/"))
            return newFile((File) null, normalizedUrl);

        File parentFile = null;
        StringBuilder stringBuilder = new StringBuilder();

        for (String pathPart : fullPath.split("/")) {
            stringBuilder.append(pathPart);
            if (stringBuilder.length() < fullPath.length())
                stringBuilder.append('/');

            String pathUrlString = UrlUtils.getSiteUrlString(normalizedUrl.toString()) + stringBuilder.toString();
            URL pathUrl = UrlUtils.normalizeUrl(new URL(pathUrlString));
            parentFile = newFile(parentFile, pathUrl);
        }
        return parentFile;
    }

    File newFile(File parent, URL url) throws IOException {
        LOGGER.debug("newFile (parentFile: {}, url: {})", parent, maskedUrlString(url));

        String urlString = url.toString();
        File cachedFile = fileCache.get(urlString);
        if (cachedFile != null)
            return cachedFile;

        return createFile(parent, url);
    }

    public void replaceFileUrl(URL oldUrl, URL newUrl) throws IOException {
        File file = fileCache.get(oldUrl.toString());
        fileCache.remove(oldUrl.toString());
        file.setUrl(newUrl);
        fileCache.put(newUrl.toString(), file);
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

        for(Map.Entry<Protocol, Pair<String, String>> entry : protocolClassMap.entrySet()) {
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

    public File getFile(String urlString) {
        return fileCache.get(urlString);
    }

    Client getClient(String urlString) {
        return siteUrlToClientMap.get(UrlUtils.getSiteUrlString(urlString));
    }

    FileOperationProvider getFileOperationProvider(String urlString) {
        Client client = siteUrlToClientMap.get(UrlUtils.getSiteUrlString(urlString));
        FileType fileType = UrlUtils.getFileTypeForUrl(urlString);

        Map<FileType, FileOperationProvider> fileOperationProviderMap = clientToFileOperationProvidersMap.get(client);
        if (fileOperationProviderMap != null)
            return this.clientToFileOperationProvidersMap.get(client).get(fileType);
        return null;
    }

    private File createFile(File parent, URL url) throws IOException {
        LOGGER.debug("createFile (parent: {}, url : {})", parent, maskedUrlString(url));

        Protocol protocol = UrlUtils.getProtocol(url);
        if (configurator.getFileOperationProviderClassMap(protocol) == null)
            throw new IOException("No configuration found for protocol: " + protocol);

        try {
            FileType fileType = UrlUtils.getFileTypeForUrl(url.toString());
            if (protocol != Protocol.FILE)
                initClient(url);
            initFileOperationProvider(url, protocol, fileType, getClient(url.toString()));
            File file = createFileInstance(parent, url);
            fileCache.put(url.toString(), file);
            return file;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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

    private File createFileInstance(File parent, URL url) {
        LOGGER.debug("createFileInstance (parent: {}, url: {})", parent, maskedUrlString(url));

        try {
            Constructor constructor = UrlFile.class.getConstructor(File.class, URL.class, FileContext.class);
            UrlFile instance = (UrlFile) constructor.newInstance(parent, url, this);
            return new FileLoggingWrapper(instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private FileOperationProvider createFileOperationProviderInstance(Class instanceClass, Client client) throws InstantiationException, IllegalAccessException {
        LOGGER.debug("createFileOperationProviderInstance (instanceClass: {}, client: {})", instanceClass, client);

        try {
            Constructor constructor = instanceClass.getConstructor(this.getClass(), Client.class);
            return (FileOperationProvider) constructor.newInstance(this, client);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private void initClient(URL url) {
        LOGGER.debug("initClient(url: {}", maskedUrlString(url));
        Client client = getClient(url.toString());
        if (client == null) {
            client = createClientInstance(url);
            siteUrlToClientMap.put(UrlUtils.getSiteUrlString(url.toString()), client);
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
        Iterator<Map.Entry<String, File>> it = fileCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, File> entry = it.next();
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
