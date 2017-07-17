/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.ftp.FtpClientConfiguration;
import at.beris.virtualfile.client.http.HttpClientConfiguration;
import at.beris.virtualfile.client.https.HttpsClientConfiguration;
import at.beris.virtualfile.client.sftp.AuthenticationType;
import at.beris.virtualfile.client.sftp.SftpClientConfiguration;
import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Public API for a virtualfile manager.
 */
public interface VirtualFileManager {

    /**
     * Create a FileManager with the supplied configuration.
     *
     * @param configuraton Configuration
     * @return Filemanager instance
     */
    static VirtualFileManager createManager(Configuration configuraton) {
        return UrlFileManager.create(configuraton);
    }

    /**
     * Create a FileManager.
     *
     * @return Filemanager instance
     */
    static VirtualFileManager createManager() {
        return UrlFileManager.create();
    }

    /**
     * Save the current configuration, sites, cryptograhic keys and certificates.
     */
    void save();

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
     *
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
     *
     * @param file File to dispose
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

    /**
     * Return the master password. This is the password used to protect all sensitive data.
     *
     * @return Master password
     */
    char[] getMasterPassword();

    /**
     * Get the path where the configuration resides.
     *
     * @return Path
     */
    String getHome();

    /**
     * Get the default configuration for a new SFTP Client.
     * When a new SFTP client is created it will be configured with this configuration.
     *
     * @return SFTP Client default configuration
     */
    SftpClientConfiguration getClientDefaultConfigurationSftp();

    /**
     * Get the default configuration for a new FTP Client.
     * When a new FTP client is created it will be configured with this configuration.
     *
     * @return FTP Client default configuration
     */
    FtpClientConfiguration getClientDefaultConfigurationFtp();

    /**
     * Get the default configuration for a new HTTP Client.
     * When a new HTTP client is created it will be configured with this configuration.
     *
     * @return HTTP Client default configuration
     */
    HttpClientConfiguration getClientDefaultConfigurationHttp();

    /**
     * Get the default configuration for a new HTTPS Client.
     * When a new HTTPS client is created it will be configured with this configuration.
     *
     * @return HTTPS Client default configuration
     */
    HttpsClientConfiguration getClientDefaultConfigurationHttps();

    /**
     * Set AuthenticationType for all Client Default Configurations that support it (sugar function)
     *
     * @param authenticationType
     * @return
     */
    VirtualFileManager setAuthenticationType(AuthenticationType authenticationType);

    /**
     * Set PrivateKeyFile for all Client Default Configurations that support it (sugar function)
     *
     * @param privateKeyFile
     * @return
     */
    VirtualFileManager setPrivateKeyFile(String privateKeyFile);

    /**
     * Set Timeout for all Client Default Configurations that support it (sugar function)
     *
     * @param timeout
     * @return
     */
    VirtualFileManager setTimeout(int timeout);

    /**
     * Set Username for all Client Default Configurations that support it (sugar function)
     *
     * @param username
     * @return
     */
    VirtualFileManager setUsername(String username);

    /**
     * Set Password for all Client Default Configurations that support it (sugar function)
     *
     * @param password
     * @return
     */
    VirtualFileManager setPassword(char[] password);

    /**
     * Set Password for all Client Default Configurations that support it (sugar function)
     *
     * @param password
     * @return
     */
    VirtualFileManager setPassword(String password);

    /**
     * Add a site to the list of sites.
     *
     * @param site
     * @return
     */
    VirtualFileManager addSite(Site site);

    /**
     * Remove a site from the list of sites.
     *
     * @param site
     * @return
     */
    VirtualFileManager removeSite(Site site);

    /**
     * Clear the list of sites.
     *
     * @return
     */
    VirtualFileManager clearSites();

    /**
     * Load the list of sites. (List will be cleared before.)
     *
     * @return
     */
    VirtualFileManager loadSites();

    /**
     * Save the list of sites.
     *
     * @return
     */
    VirtualFileManager saveSites();

    /**
     * Get the list of sites.
     *
     * @return List of sites
     */
    List<Site> getSites();

    /**
     * Find a site by ID.
     *
     * @param id
     * @return Site
     */
    Optional<Site> findSiteById(String id);

    /**
     * Find a site by name.
     *
     * @param name
     * @return Site
     */
    Optional<Site> findSiteByName(String name);

    /**
     * Find a site by shortname.
     *
     * @param shortName
     * @return Site
     */
    Optional<Site> findSiteByShortName(String shortName);

    /**
     * Resolve a file using a site.
     * Connects to the site and returns the current directory.
     *
     * @param site
     * @return
     */
    VirtualFile resolveFile(Site site);

    /**
     * Resolve a file connecting to a site with a path relative to the current directory.
     *
     * @param site Site
     * @param path Absolute or relative path.
     * @return
     */
    VirtualFile resolveFile(Site site, String path);

    /**
     * Resolve a directory connecting to a site with a path relative to the current directory.
     *
     * @param site
     * @param path Absolute or relative path.
     * @return
     */
    VirtualFile resolveDirectory(Site site, String path);
}
