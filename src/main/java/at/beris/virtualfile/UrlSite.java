/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.UrlUtils;

import java.net.URL;

public class UrlSite implements RemoteSite {
    private Client client;
    private Protocol protocol;
    private String host;
    private int port;
    private String username;
    private char[] password;

    public UrlSite(URL url) {
        protocol = UrlUtils.getProtocol(url);
        host = url.getHost();
        port = url.getPort();
        String userInfoParts[] = url.getUserInfo().split(":");
        username = userInfoParts[0];
        password = userInfoParts[1].toCharArray();
    }

    @Override
    public Client getClient() {
        return client;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
        initClient(client);
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public char[] getPassword() {
        return password;
    }

    @Override
    public void setPassword(char[] password) {
        this.password = password;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    private void initClient(Client client) {
        client.setHost(host);
        client.setPort(port);
        client.setUsername(username);
        client.setPassword(password);
    }
}
