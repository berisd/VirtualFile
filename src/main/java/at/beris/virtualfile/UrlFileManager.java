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
import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

class UrlFileManager implements VirtualFileManager {
    private FileContext fileContext;

    public UrlFileManager() {
        super();
        fileContext = new FileContext();
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
        return fileContext.newLocalFile(path);
    }

    @Override
    public VirtualFile newLocalDirectory(String path) {
        return fileContext.newLocalDirectory(path);
    }

    @Override
    public VirtualFile newFile(String urlString) {
        return fileContext.newFile(urlString);
    }

    @Override
    public VirtualFile newFile(URL url) {
        return fileContext.newFile(url);
    }

    @Override
    public VirtualFile newDirectory(URL url) {
        return fileContext.newDirectory(url);
    }

    @Override
    public void dispose(VirtualFile file) {
        fileContext.dispose(file);
    }

    @Override
    public Set<Protocol> enabledProtocols() {
        return fileContext.enabledProtocols();
    }

    @Override
    public Set<Protocol> supportedProtocols() {
        return EnumSet.allOf(Protocol.class);
    }

    public FileContext getFileContext() {
        return fileContext;
    }
}
