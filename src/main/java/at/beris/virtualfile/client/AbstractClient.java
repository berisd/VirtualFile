/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.config.UrlFileConfiguration;

import java.net.URL;

public abstract class AbstractClient<T> implements Client<T> {
    protected UrlFileConfiguration config;
    protected URL url;

    public AbstractClient(URL url, UrlFileConfiguration config) {
        this.config = config;
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    protected String username() {
        String usernameFromUrl = getUsernameFromUrl();
        if (usernameFromUrl != null)
            return usernameFromUrl;
        else if (config.getUsername() != null)
            return config.getUsername();
        else
            return null;
    }

    protected char[] password() {
        String passwordFromUrl = getPasswordFromUrl();
        if (passwordFromUrl != null)
            return passwordFromUrl.toCharArray();
        else if (config.getPassword() != null)
            return config.getPassword();
        else
            return null;
    }

    protected String host() {
        return url.getHost();
    }

    protected int port() {
        int port = url.getPort();
        return port != -1 ? port : defaultPort();
    }

    protected abstract int defaultPort();

    private String getUsernameFromUrl() {
        String userInfo = url.getUserInfo();
        if (userInfo != null) {
            String userInfoParts[] = url.getUserInfo().split(":");
            return userInfoParts[0];
        }
        return null;
    }

    private String getPasswordFromUrl() {
        String userInfo = url.getUserInfo();
        if (userInfo != null) {
            String userInfoParts[] = url.getUserInfo().split(":");
            if (userInfoParts.length > 1) {
                return userInfoParts[1];
            }
        }
        return null;
    }
}
