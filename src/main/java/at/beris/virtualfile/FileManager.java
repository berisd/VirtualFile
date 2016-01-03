/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import java.net.URL;

public class FileManager {
    private static FileContext fileContext;

    private FileManager() {

    }

    public static FileContext newContext() {
        return new FileContext(new FileConfig());
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
     * Creates file with an URL String. (Convenience method)
     *
     * @param url
     * @return
     */
    public static IFile newFile(String url) {
        return getFileContext().newFile(url);
    }

    public static IFile newFile(URL parentUrl, URL url) {
        return getFileContext().newFile(parentUrl, url);
    }

    /**
     * Creates a file instance for the corresponding url
     *
     * @param parent
     * @param url
     * @return
     */
    public static IFile newFile(IFile parent, URL url) {
        return getFileContext().newFile(parent, url);
    }

    public static IFile newFile(URL url) {
        return getFileContext().newFile(url);
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
