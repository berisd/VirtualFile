/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.config.SimpleConfigurator;
import at.beris.virtualfile.logging.FileManagerLoggingWrapper;
import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

public class FileManager {
    private static final FileContext fileContext;
    private static final SimpleConfigurator configurator;

    static {
        fileContext = new FileContext(new Configurator());
        fileContext.registerProtocolURLStreamHandlers();
        configurator = new SimpleConfigurator(fileContext.getConfig());
    }

    private FileManager() {
        super();
    }

    public static SimpleConfigurator getConfig() {
        return configurator;
    }

    /**
     * Creates a local file with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static File newLocalFile(String path) {
        return new FileManagerLoggingWrapper(fileContext.newLocalFile(path));
    }

    /**
     * Creates a local directory with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static Directory newLocalDirectory(String path) {
        return new FileManagerLoggingWrapper(fileContext.newLocalFile(path +
                (path.endsWith(java.io.File.separator) ? "" : java.io.File.separator)));
    }

    /**
     * Creates a local archive with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static Archive newLocalArchive(String path) {
        return new FileManagerLoggingWrapper(fileContext.newLocalFile(path));
    }

    public static File newFile(String url) {
        return new FileManagerLoggingWrapper(fileContext.newFile(url));
    }

    public static File newFile(URL parentUrl, URL url) {
        return new FileManagerLoggingWrapper(fileContext.newFile(parentUrl, url));
    }

    public static File newFile(File parent, URL url) {
        return new FileManagerLoggingWrapper(fileContext.newFile(parent, url));
    }

    public static File newFile(URL url) {
        return new FileManagerLoggingWrapper(fileContext.newFile(url));
    }

    public static Directory newDirectory(URL url) {
        return new FileManagerLoggingWrapper(fileContext.newFile(url));
    }

    public static Archive newArchive(URL url) {
        return new FileManagerLoggingWrapper(fileContext.newFile(url));
    }

    public static void dispose(File file) {
        fileContext.dispose(file);
    }

    public static Set<Protocol> enabledProtocols() {
        return fileContext.enabledProtocols();
    }

    public static Set<Protocol> allProtocols() {
        return EnumSet.allOf(Protocol.class);
    }

    public static void registerProtocolURLStreamHandlers() {
        fileContext.registerProtocolURLStreamHandlers();
    }
}
