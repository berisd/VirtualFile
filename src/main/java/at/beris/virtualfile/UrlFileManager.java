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

class UrlFileManager implements VirtualFileManager {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(UrlFileManager.class);

    private VirtualFileContext fileContext;

    public UrlFileManager() {
        super();
        fileContext = new UrlFileContext();
    }

    @Override
    public ContextConfiguration getContextConfiguration() {
        return fileContext.getConfigurator().getContextConfiguration();
    }

    @Override
    public Configuration getConfiguration() {
        return fileContext.getConfigurator().getConfiguration();
    }

    public Configuration getConfiguration(Protocol protocol) {
        return fileContext.getConfigurator().getConfiguration(protocol);
    }

    @Override
    public Configuration getConfiguration(VirtualFile file) {
        return fileContext.getConfigurator().getConfiguration(file);
    }

    @Override
    public VirtualFile newLocalFile(String path) {
        return fileContext.newFile(UrlUtils.getUrlForLocalPath(path));
    }

    @Override
    public VirtualFile newLocalDirectory(String path) {
        return newLocalFile(path + (path.endsWith(File.separator) ? "" : File.separator));
    }

    @Override
    public VirtualFile newFile(String urlString) {
        try {
            return fileContext.newFile(new URL(urlString));
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public VirtualFile newFile(URL url) {
        return fileContext.newFile(url);
    }

    @Override
    public VirtualFile newDirectory(URL url) {
        URL normalizedUrl = url;
        if (!url.getPath().endsWith("/"))
            normalizedUrl = UrlUtils.newUrl(url, url.getPath() + "/");
        return fileContext.newFile(normalizedUrl);
    }

    @Override
    public VirtualArchive newLocalArchive(String path) {
        //TODO Move creation to FileContext
        return new FileArchive(newFile(UrlUtils.getUrlForLocalPath(path)), fileContext);
    }

    @Override
    public VirtualArchive newArchive(String urlString) {
        //TODO Move creation to FileContext
        try {
            return new FileArchive(newFile(new URL(urlString)), fileContext);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void dispose() {
        fileContext.dispose();
    }

    @Override
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


    @Override
    public Set<Protocol> supportedProtocols() {
        return EnumSet.allOf(Protocol.class);
    }

}
