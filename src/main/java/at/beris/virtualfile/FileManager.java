/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;
import java.util.EnumSet;
import java.util.Set;

public class FileManager {
    private static FileContext fileContext;

    private FileManager() {

    }

    /**
     * Creates a local file with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static IFile newLocalFile(String path) {
        return getFileContext().newLocalFile(path);
    }

    /**
     * Creates a local directory with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static IDirectory newLocalDirectory(String path) {
        return (IDirectory) getFileContext().newLocalFile(path +
                (path.endsWith(java.io.File.separator) ? "" : java.io.File.separator));
    }

    /**
     * Creates a local archive with with the given path. (Convenience method)
     *
     * @param path
     * @return
     */
    public static IArchive newLocalArchive(String path) {
        return (IArchive) getFileContext().newLocalFile(path);
    }


    public static IFile newFile(String url) {
        return getFileContext().newFile(url);
    }

    public static IFile newFile(URL parentUrl, URL url) {
        return getFileContext().newFile(parentUrl, url);
    }

    public static IFile newFile(IFile parent, URL url) {
        return getFileContext().newFile(parent, url);
    }

    public static IFile newFile(URL url) {
        return getFileContext().newFile(url);
    }

    public static IDirectory newDirectory(URL url) {
        return (IDirectory) getFileContext().newFile(url);
    }

    public static IArchive newArchive(URL url) {
        return (IArchive) getFileContext().newFile(url);
    }


    public static void dispose(IFile file) {
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
