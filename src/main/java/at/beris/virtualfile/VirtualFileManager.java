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
import java.util.Set;

public interface VirtualFileManager {
    ContextConfiguration getContextConfiguration();

    Configuration getConfiguration();

    Configuration getConfiguration(Protocol protocol);

    Configuration getConfiguration(VirtualFile file);

    /**
     * Creates a VirtualFile representing a local file with the given path.
     *
     * @param path Path
     * @return New VirtualFile instance
     */
    VirtualFile newLocalFile(String path);

    /**
     * Creates a VirtualFile representing a local directory with the given path.
     *
     * @param path Path
     * @return New VirtualFile Instance
     */
    VirtualFile newLocalDirectory(String path);

    /**
     * Creates a VirtualFile representing a network file with the given URL String.
     *
     * @param urlString Path
     * @return New VirtualFile Instance
     */
    VirtualFile newFile(String urlString);

    /**
     * Creates a VirtualFile representing a network file with the given URL.
     *
     * @param url URL object
     * @return New VirtualFile Instance
     */
    VirtualFile newFile(URL url);

    /**
     * Creates a VirtualFile representing a network directory with the given URL.
     *
     * @param url URL object
     * @return New VirtualFile Instance
     */
    VirtualFile newDirectory(URL url);

    /**
     * Remove and clean up the VirtualFile
     *
     * @param file VirtualFile
     */
    void dispose(VirtualFile file);

    /**
     * Get the protocols currently enabled
     *
     * @return Enabled protocols
     */
    Set<Protocol> enabledProtocols();

    /**
     * Get the protocols supported by this version of the VirtualFile library
     *
     * @return Supported Protocols
     */
    Set<Protocol> supportedProtocols();
}
