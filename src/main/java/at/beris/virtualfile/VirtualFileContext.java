/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.VirtualClient;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.provider.FileOperationProvider;
import org.apache.tika.detect.DefaultDetector;

import java.net.URL;

/**
 * Manage and cache virtual files and their relations.
 */
//TODO Consider removing the interface because it's not visible outside the library
public interface VirtualFileContext {

    /**
     * Get the context configurator.
     *
     * @return Configurator
     */
    Configurator getConfigurator();

    /**
     * Creates a VirtualFile instance for the given url.
     *
     * @param url URL
     * @return New File Instance
     */
    VirtualFile newFile(URL url);

    /**
     * Replace URL of a VirtualFile with a new URL.
     *
     * @param oldUrl Old URL
     * @param newUrl New URL
     */
    void replaceFileUrl(URL oldUrl, URL newUrl);

    /**
     * Remove a VirtualFile from the context and free it's allocated resources.
     *
     * @param file VirtualFile
     */
    void dispose(VirtualFile file);

    /**
     * Free all resources allocated by the file context.
     */
    void dispose();

    /**
     * Get parent file of the VirtualFile.
     *
     * @param file File
     * @return Parent file
     */
    VirtualFile getParentFile(VirtualFile file);

    /**
     * Get client for a siteUrlString.
     *
     * @param siteUrlString Site UrlString
     * @return Client
     */
    VirtualClient getClient(String siteUrlString);

    /**
     * Get FileOperationProvider for the URL string.
     *
     * @param urlString UrlString
     * @return FileOperationProvider
     */
    FileOperationProvider getFileOperationProvider(String urlString);

    /**
     * Create an empty file model.
     *
     * @return FileModel
     */
    FileModel createFileModel();

    VirtualArchiveEntry createArchiveEntry();

    DefaultDetector getContentDetector();

}
