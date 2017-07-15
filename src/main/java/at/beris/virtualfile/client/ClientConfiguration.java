/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.Site;
import at.beris.virtualfile.client.ftp.FtpClientConfiguration;
import at.beris.virtualfile.client.http.HttpClientConfiguration;
import at.beris.virtualfile.client.https.HttpsClientConfiguration;
import at.beris.virtualfile.client.sftp.SftpClientConfiguration;
import at.beris.virtualfile.util.StringUtils;
import at.beris.virtualfile.util.UrlUtils;

import java.net.URL;
import java.util.Optional;

public abstract class ClientConfiguration<T> {

    public static final int DEFAULT_TIMEOUT = 30;
    public static final String DEFAULT_USERNAME = StringUtils.EMPTY_STRING;
    public static final char[] DEFAULT_PASSWORD = {};


    private int timeout;

    private String username;

    private char[] password;

    private String hostname;

    private int port;

    public ClientConfiguration() {
        setTimeout(DEFAULT_TIMEOUT);
        setUsername(DEFAULT_USERNAME);
        setPassword(DEFAULT_PASSWORD);
    }

    public static SftpClientConfiguration createSFtpConfiguration() {
        return new SftpClientConfiguration();
    }

    public static FtpClientConfiguration createFtpConfiguration() {
        return new FtpClientConfiguration();
    }

    public static HttpClientConfiguration createHttpConfiguration() {
        return new HttpClientConfiguration();
    }

    public static HttpsClientConfiguration createHttpsConfiguration() {
        return new HttpsClientConfiguration();
    }

    public int getTimeout() {
        return timeout;
    }

    public T setTimeout(int timeout) {
        this.timeout = timeout;
        return (T) this;
    }

    public String getUsername() {
        return username;
    }

    public T setUsername(String username) {
        this.username = username;
        return (T) this;
    }

    public char[] getPassword() {
        return password;
    }

    public T setPassword(char[] password) {
        this.password = password;
        return (T) this;
    }

    public String getHostname() {
        return hostname;
    }

    public T setHostname(String hostname) {
        this.hostname = hostname;
        return (T) this;
    }

    public int getPort() {
        return port;
    }

    public T setPort(int port) {
        this.port = port;
        return (T) this;
    }

    public void fillFromUrl(URL url) {
        setHostname(url.getHost());
        setPort(url.getPort());

        Optional<String> username = UrlUtils.getUsernameFromUrl(url);
        if (username.isPresent()) {
            setUsername(username.get());
        }

        Optional<String> password = UrlUtils.getPasswordFromUrl(url);
        if (password.isPresent()) {
            setPassword(password.get().toCharArray());
        }
    }

    public void fillFromSite(Site site) {
        setHostname(site.getHostname());
        setPassword(site.getPassword());
        setPort(site.getPort());
        setUsername(site.getUsername());
        setTimeout(site.getTimeout());
    }

    public void fillFromClientConfiguration(ClientConfiguration clientConfig) {
        setHostname(clientConfig.getHostname());
        setUsername(clientConfig.getUsername());
        setPassword(clientConfig.getPassword());
        setPort(clientConfig.getPort());
        setTimeout(clientConfig.getTimeout());
    }
}
