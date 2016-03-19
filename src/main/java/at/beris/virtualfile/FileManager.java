/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.FileContextConfig;
import at.beris.virtualfile.logging.FileManagerLoggingWrapper;
import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

public class FileManager {
    private static FileContext fileContext;

    private FileManager() {
        super();
    }

    public static FileContextConfig getConfig() {
        return getFileContext().getConfig();
    }

    /**
     * Creates a local file with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static File newLocalFile(String path) {
        return new FileManagerLoggingWrapper(getFileContext().newLocalFile(path));
    }

    /**
     * Creates a local directory with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static Directory newLocalDirectory(String path) {
        return (Directory) new FileManagerLoggingWrapper(getFileContext().newLocalFile(path +
                (path.endsWith(java.io.File.separator) ? "" : java.io.File.separator)));
    }

    /**
     * Creates a local archive with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static Archive newLocalArchive(String path) {
        return (Archive) new FileManagerLoggingWrapper(getFileContext().newLocalFile(path));
    }

    public static File newFile(String url) {
        return new FileManagerLoggingWrapper(getFileContext().newFile(url));
    }

    public static File newFile(URL parentUrl, URL url) {
        return new FileManagerLoggingWrapper(getFileContext().newFile(parentUrl, url));
    }

    public static File newFile(File parent, URL url) {
        return new FileManagerLoggingWrapper(getFileContext().newFile(parent, url));
    }

    public static File newFile(URL url) {
        return new FileManagerLoggingWrapper(getFileContext().newFile(url));
    }

    public static Directory newDirectory(URL url) {
        return (Directory) new FileManagerLoggingWrapper(getFileContext().newFile(url));
    }

    public static Archive newArchive(URL url) {
        return (Archive) new FileManagerLoggingWrapper(getFileContext().newFile(url));
    }


    public static void dispose(File file) {
        getFileContext().dispose(file);
    }

    public static Set<Protocol> enabledProtocols() {
        //TODO only return enabled Protocols
        return EnumSet.allOf(Protocol.class);
    }

    public static Set<Protocol> allProtocols() {
        return EnumSet.allOf(Protocol.class);
    }

    private static FileContext getFileContext() {
        if (fileContext == null) {
            fileContext = new FileContext(new FileContextConfig());
            fileContext.registerProtocolURLStreamHandlers();
        }
        return fileContext;
    }

    public static void registerProtocolURLStreamHandlers() {
        //implicitly calls registerProtocolURLStreamHandlers
        getFileContext();
    }
}
