/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.config.ContextConfiguration;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.UrlUtils;

import java.io.IOException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

public class FileManager {
    private static final FileContext fileContext;

    static {
        fileContext = new FileContext(new Configurator());
    }

    private FileManager() {
        super();
    }

    public static ContextConfiguration getContextConfiguration() {
        return fileContext.getConfigurator().getContextConfiguration();
    }

    public static Configuration getConfiguration() {
        return fileContext.getConfigurator().getConfiguration();
    }

    public static Configuration getConfiguration(Protocol protocol) {
        return fileContext.getConfigurator().getConfiguration(protocol);
    }

    public static Configuration getConfiguration(File file) throws IOException {
        return fileContext.getConfigurator().getConfiguration(file);
    }

    /**
     * Creates a local file with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static File newLocalFile(String path) throws IOException {
        return fileContext.newLocalFile(path);
    }

    /**
     * Creates a local directory with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static File newLocalDirectory(String path) throws IOException {
        return fileContext.newLocalFile(path + (path.endsWith(java.io.File.separator) ? "" : java.io.File.separator));
    }

    public static File newFile(String url) throws IOException {
        return fileContext.newFile(url);
    }

    public static File newFile(File parent, URL url) throws IOException {
        return fileContext.newFile(parent, url);
    }

    public static File newFile(URL url) throws IOException {
        return fileContext.newFile(url);
    }

    public static File newDirectory(URL url) throws IOException {
        URL normalizedUrl = url;
        if (!url.getPath().endsWith("/"))
            normalizedUrl = UrlUtils.newUrl(url, url.getPath() + "/");
        return fileContext.newFile(normalizedUrl);
    }

    public static void dispose(File file) throws IOException {
        fileContext.dispose(file);
    }

    public static Set<Protocol> enabledProtocols() {
        return fileContext.enabledProtocols();
    }

    public static Set<Protocol> allProtocols() {
        return EnumSet.allOf(Protocol.class);
    }
}
