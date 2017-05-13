/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.config.ContextConfiguration;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class VirtualFileManager {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(VirtualFileManager.class);

    private UrlFileContext fileContext;

    public VirtualFileManager() {
        super();
        fileContext = new UrlFileContext();
    }

    public ContextConfiguration getContextConfiguration() {
        return fileContext.getConfigurator().getContextConfiguration();
    }

    public Configuration getConfiguration() {
        return fileContext.getConfigurator().getConfiguration();
    }

    public Configuration getConfiguration(Protocol protocol) {
        return fileContext.getConfigurator().getConfiguration(protocol);
    }

    public Configuration getConfiguration(VirtualFile file) {
        return fileContext.getConfigurator().getConfiguration(file);
    }

    /**
     * Creates a VirtualFile representing a local file with the given path.
     *
     * @param path Path
     * @return New VirtualFile instance
     */
    public VirtualFile resolveLocalFile(String path) {
        return fileContext.resolveFile(UrlUtils.getUrlForLocalPath(path));
    }

    /**
     * Creates a VirtualFile representing a local directory with the given path.
     *
     * @param path Path
     * @return New VirtualFile Instance
     */
    public VirtualFile resolveLocalDirectory(String path) {
        return resolveLocalFile(path + (path.endsWith(File.separator) ? "" : File.separator));
    }

    /**
     * Creates a VirtualFile representing a network file with the given URL String.
     *
     * @param urlString Path
     * @return New VirtualFile Instance
     */
    public VirtualFile resolveFile(String urlString) {
        try {
            return fileContext.resolveFile(new URL(urlString));
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Creates a VirtualFile representing a network file with the given URL.
     *
     * @param url URL object
     * @return New VirtualFile Instance
     */
    public VirtualFile resolveFile(URL url) {
        return fileContext.resolveFile(url);
    }

    /**
     * Creates a VirtualFile representing a network directory with the given URL.
     *
     * @param url URL object
     * @return New VirtualFile Instance
     */
    public VirtualFile resolveDirectory(URL url) {
        URL normalizedUrl = url;
        if (!url.getPath().endsWith("/"))
            normalizedUrl = UrlUtils.newUrl(url, url.getPath() + "/");
        return fileContext.resolveFile(normalizedUrl);
    }

    /**
     * Creates a VirtualArchive representing a local archive with the given path.
     * @param path Path
     * @return VirtualArchive
     */
    public VirtualArchive resolveLocalArchive(String path) {
        //TODO Move creation to FileContext
        return new FileArchive(resolveFile(UrlUtils.getUrlForLocalPath(path)), fileContext);
    }

    /**
     * Creates an Archive represented by the given URL.
     *
     * @param urlString URL String
     * @return Archive
     */
    public VirtualArchive resolveArchive(String urlString) {
        //TODO Move creation to FileContext
        try {
            return new FileArchive(resolveFile(new URL(urlString)), fileContext);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Frees all resources allocated by the VirtualFileManager.
     */
    public void dispose() {
        fileContext.dispose();
    }

    /**
     * Frees all resources allocated by the VirtualFile.
     */
    public void dispose(VirtualFile file) {
        fileContext.dispose(file);
    }

    /**
     * Returns the protocols currently enabled.
     *
     * @return Enabled protocols.
     */
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
            } catch (ClassNotFoundException ignored) {
            }
            if (!enabledProtocols.contains(protocol))
                LOGGER.info(protocolLibrary.getLeft() + " not installed. No support for protocol " + protocol);
        }

        return Collections.unmodifiableSet(enabledProtocols);
    }


    /**
     * Returns the protocols supported by this version of the VirtualFile library.
     *
     * @return Supported Protocols.
     */
    public Set<Protocol> supportedProtocols() {
        return EnumSet.allOf(Protocol.class);
    }

}
