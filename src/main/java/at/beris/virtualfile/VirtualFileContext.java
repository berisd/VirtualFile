/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.provider.FileOperationProvider;

import java.net.URL;

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
     * @param file
     * @return
     */
    VirtualFile getParentFile(VirtualFile file);

    /**
     * Get client for a siteUrlString.
     *
     * @param siteUrlString
     * @return
     */
    Client getClient(String siteUrlString);

    /**
     * Get FileOperationProvider for the URL string.
     *
     * @param urlString
     * @return
     */
    FileOperationProvider getFileOperationProvider(String urlString);

    /**
     * Create an empty file model.
     *
     * @return
     */
    FileModel createFileModel();
}
