/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.config.Configurator;
import at.beris.virtualfile.config.ContextConfiguration;
import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;
import java.util.Set;

/**
 * Public API for virtual file management
 */
public interface VirtualFileManager {

    static VirtualFileManager createManager() {
        return new UrlFileManager(new UrlFileContext(new Configurator()));
    }

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
    VirtualFile resolveLocalFile(String path);

    /**
     * Creates a VirtualFile representing a local directory with the given path.
     *
     * @param path Path
     * @return New VirtualFile Instance
     */
    VirtualFile resolveLocalDirectory(String path);

    /**
     * Creates a VirtualFile representing a network file with the given URL String.
     *
     * @param urlString Path
     * @return New VirtualFile Instance
     */
    VirtualFile resolveFile(String urlString);

    /**
     * Creates a VirtualFile representing a network file with the given URL.
     *
     * @param url URL object
     * @return New VirtualFile Instance
     */
    VirtualFile resolveFile(URL url);

    /**
     * Creates a VirtualFile representing a network directory with the given URL.
     *
     * @param url URL object
     * @return New VirtualFile Instance
     */
    VirtualFile resolveDirectory(URL url);

    /**
     * Creates a VirtualArchive representing a local archive with the given path.
     * @param path Path
     * @return VirtualArchive
     */
    VirtualArchive resolveLocalArchive(String path);

    /**
     * Creates an Archive represented by the given URL.
     *
     * @param urlString URL String
     * @return Archive
     */
    VirtualArchive resolveArchive(String urlString);

    /**
     * Frees all resources allocated by the VirtualFileManager.
     */
    void dispose();

    /**
     * Frees all resources allocated by the VirtualFile.
     */
    void dispose(VirtualFile file);

    /**
     * Returns the protocols currently enabled.
     *
     * @return Enabled protocols.
     */
    Set<Protocol> enabledProtocols();


    /**
     * Returns the protocols supported by this version of the VirtualFile library.
     *
     * @return Supported Protocols.
     */
    Set<Protocol> supportedProtocols();
}
