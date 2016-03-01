/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.FileConfig;
import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

public class FileManager {
    private static FileContext fileContext;

    private FileManager() {
        super();
    }

    /**
     * Creates a local file with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static File newLocalFile(String path) {
        return newLocalFile(path, null);
    }

    public static File newLocalFile(String path, FileConfig fileConfig) {
        return getFileContext().newLocalFile(path, fileConfig);
    }

    /**
     * Creates a local directory with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static Directory newLocalDirectory(String path) {
        return newLocalDirectory(path, null);
    }

    public static Directory newLocalDirectory(String path, FileConfig fileConfig) {
        return (Directory) getFileContext().newLocalFile(path +
                (path.endsWith(java.io.File.separator) ? "" : java.io.File.separator), fileConfig);
    }

    /**
     * Creates a local archive with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */

    public static Archive newLocalArchive(String path) {
        return newLocalArchive(path, null);
    }

    public static Archive newLocalArchive(String path, FileConfig fileConfig) {
        return (Archive) getFileContext().newLocalFile(path, fileConfig);
    }


    public static File newFile(String url) {
        return getFileContext().newFile(url, null);
    }

    public static File newFile(String url, FileConfig fileConfig) {
        return getFileContext().newFile(url, fileConfig);
    }

    public static File newFile(URL parentUrl, URL url) {
        return newFile(parentUrl, url, null);
    }

    public static File newFile(URL parentUrl, URL url, FileConfig fileConfig) {
        return getFileContext().newFile(parentUrl, url, fileConfig);
    }

    public static File newFile(File parent, URL url) {
        return newFile(parent, url, null);
    }

    public static File newFile(File parent, URL url, FileConfig fileConfig) {
        return getFileContext().newFile(parent, url, fileConfig);
    }

    public static File newFile(URL url) {
        return newFile(url, (FileConfig) null);
    }

    public static File newFile(URL url, FileConfig fileConfig) {
        return getFileContext().newFile(url, fileConfig);
    }

    public static Directory newDirectory(URL url) {
        return newDirectory(url, null);
    }

    public static Directory newDirectory(URL url, FileConfig fileConfig) {
        return (Directory) getFileContext().newFile(url, fileConfig);
    }

    public static Archive newArchive(URL url) {
        return newArchive(url, null);
    }

    public static Archive newArchive(URL url, FileConfig fileConfig) {
        return (Archive) getFileContext().newFile(url, fileConfig);
    }


    public static void dispose(File file) {
        file.dispose();
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
            fileContext = new FileContext(new FileConfig());
            fileContext.registerProtocolURLStreamHandlers();
        }
        return fileContext;
    }

    public static void registerProtocolURLStreamHandlers() {
        //implicitly calls registerProtocolURLStreamHandlers
        getFileContext();
    }
}
