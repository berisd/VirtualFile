/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.sftp.AuthenticationType;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.UrlUtils;

import java.net.URL;
import java.util.Optional;

public class Site {

    private String id;

    private Protocol protocol;

    private String name;

    private int timeout;

    private String username;

    private char[] password;

    private String hostname;

    private int port;

    private boolean strictHostKeyChecking;

    private String knownHostsFile;

    private AuthenticationType authenticationType;

    private String privateKeyFile;

    private Site() {

    }

    public static Site create() {
        return new Site();
    }

    public String getId() {
        return id;
    }

    public Site setId(String id) {
        this.id = id;
        return this;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public Site setProtocol(Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public String getName() {
        return name;
    }

    public Site setName(String name) {
        this.name = name;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public Site setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Site setUsername(String username) {
        this.username = username;
        return this;
    }

    public char[] getPassword() {
        return password;
    }

    public Site setPassword(char[] password) {
        this.password = password;
        return this;
    }

    public String getHostname() {
        return hostname;
    }

    public Site setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Site setPort(int port) {
        this.port = port;
        return this;
    }

    public boolean isStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    public Site setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
        return this;
    }

    public String getKnownHostsFile() {
        return knownHostsFile;
    }

    public Site setKnownHostsFile(String knownHostsFile) {
        this.knownHostsFile = knownHostsFile;
        return this;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public Site setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
        return this;
    }

    public String getPrivateKeyFile() {
        return privateKeyFile;
    }

    public Site setPrivateKeyFile(String privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
        return this;
    }

    public Site fillFromUrl(URL url) {
        setProtocol(UrlUtils.getProtocol(url));
        setHostname(url.getHost());
        Optional<String> username = UrlUtils.getUsernameFromUrl(url);
        if (username.isPresent()) {
            setUsername(username.get());
        }
        Optional<String> password = UrlUtils.getPasswordFromUrl(url);
        if (password.isPresent()) {
            setPassword(password.get().toCharArray());
        }
        int port = url.getPort();
        if (port == -1)
            port = UrlUtils.getProtocol(url).getDefaultPort();
        setPort(port);
        return this;
    }
}
