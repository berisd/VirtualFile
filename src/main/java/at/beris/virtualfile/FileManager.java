/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
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
import java.util.Optional;
import java.util.Set;

/**
 * Public interface to the VirtualFile library
 */
public final class FileManager {
    private static Optional<UrlFileManager> fileManager = Optional.empty();

    private FileManager() {
        super();
    }

    public static ContextConfiguration getContextConfiguration() {
        return fileManager().getContextConfiguration();
    }

    public static Configuration getConfiguration() {
        return fileManager().getConfiguration();
    }

    public static Configuration getConfiguration(Protocol protocol) {
        return fileManager().getConfiguration(protocol);
    }

    public static Configuration getConfiguration(VirtualFile file) {
        return fileManager().getConfiguration(file);
    }

    public static VirtualFile newLocalFile(String path) {
        return fileManager().newLocalFile(path);
    }

    public static VirtualFile newLocalDirectory(String path) {
        return fileManager().newLocalDirectory(path);
    }

    public static VirtualFile newFile(String urlString) {
        return fileManager().newFile(urlString);
    }

    public static VirtualFile newFile(URL url) {
        return fileManager().newFile(url);
    }

    public static VirtualFile newDirectory(URL url) {
        return fileManager().newDirectory(url);
    }

    public static VirtualFileManager newManager() {
        return new UrlFileManager();
    }

    public static void dispose() {
        fileManager().dispose();
    }

    public static void dispose(VirtualFile file) {
        fileManager().dispose(file);
    }

    public static Set<Protocol> enabledProtocols() {
        return fileManager().enabledProtocols();
    }

    public static Set<Protocol> supportedProtocols() {
        return EnumSet.allOf(Protocol.class);
    }

    private static UrlFileManager fileManager() {
        if (!fileManager.isPresent())
            fileManager = Optional.of(new UrlFileManager());
        return fileManager.get();
    }
}
