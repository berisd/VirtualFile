/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.protocol.Protocol;

import java.net.URL;

public class SimpleConfigurator {
    private Configurator configurator;

    public SimpleConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    public Integer getFileCacheSize() {
        return configurator.getBaseConfig().getFileCacheSize();
    }

    public SimpleConfigurator setFileCacheSize(int fileCacheSize) {
        configurator.getBaseConfig().setFileCacheSize(fileCacheSize);
        return this;
    }

    public AuthenticationType getAuthenticationType() {
        return configurator.getClientConfig().getAuthenticationType();
    }

    public SimpleConfigurator setAuthenticationType(AuthenticationType authenticationType) {
        configurator.getClientConfig().setAuthenticationType(authenticationType);
        return this;
    }

    public AuthenticationType getAuthenticationType(Protocol protocol) {
        return configurator.getClientConfig(protocol).getAuthenticationType();
    }

    public SimpleConfigurator setAuthenticationType(AuthenticationType authenticationType, Protocol protocol) {
        configurator.getClientConfig(protocol).setAuthenticationType(authenticationType);
        return this;
    }

    public AuthenticationType getAuthenticationType(URL url) {
        return getClientConfigForUrl(url).getAuthenticationType();
    }

    public SimpleConfigurator setAuthenticationType(AuthenticationType authenticationType, URL url) {
        getClientConfigForUrl(url).setAuthenticationType(authenticationType);
        return this;
    }

    public String getKnownHostsFile() {
        return configurator.getClientConfig().getKnownHostsFile();
    }

    public SimpleConfigurator setKnownHostsFile(String knownHostsFile) {
        configurator.getClientConfig().setKnownHostsFile(knownHostsFile);
        return this;
    }

    public String getKnownHostsFile(Protocol protocol) {
        return configurator.getClientConfig(protocol).getKnownHostsFile();
    }

    public SimpleConfigurator setKnownHostsFile(String knownHostsFile, Protocol protocol) {
        configurator.getClientConfig(protocol).setKnownHostsFile(knownHostsFile);
        return this;
    }

    public String getKnownHostsFile(URL url) {
        return getClientConfigForUrl(url).getKnownHostsFile();
    }

    public SimpleConfigurator setKnownHostsFile(String knownHostsFile, URL url) {
        getClientConfigForUrl(url).setKnownHostsFile(knownHostsFile);
        return this;
    }

    public String getPrivateKeyFile() {
        return configurator.getClientConfig().getPrivateKeyFile();
    }

    public SimpleConfigurator setPrivateKeyFile(String privateKeyFile) {
        configurator.getClientConfig().setPrivateKeyFile(privateKeyFile);
        return this;
    }

    public String getPrivateKeyFile(Protocol protocol) {
        return configurator.getClientConfig(protocol).getPrivateKeyFile();
    }

    public SimpleConfigurator setPrivateKeyFile(String privateKeyFile, Protocol protocol) {
        configurator.getClientConfig(protocol).setPrivateKeyFile(privateKeyFile);
        return this;
    }

    public String getPrivateKeyFile(URL url) {
        return getClientConfigForUrl(url).getPrivateKeyFile();
    }

    public SimpleConfigurator setPrivateKeyFile(String privateKeyFile, URL url) {
        getClientConfigForUrl(url).setPrivateKeyFile(privateKeyFile);
        return this;
    }

    public Boolean isStrictHostKeyChecking() {
        return configurator.getClientConfig().isStrictHostKeyChecking();
    }

    public SimpleConfigurator setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        configurator.getClientConfig().setStrictHostKeyChecking(strictHostKeyChecking);
        return this;
    }

    public Boolean isStrictHostKeyChecking(Protocol protocol) {
        return configurator.getClientConfig(protocol).isStrictHostKeyChecking();
    }

    public SimpleConfigurator setStrictHostKeyChecking(boolean strictHostKeyChecking, Protocol protocol) {
        configurator.getClientConfig(protocol).setStrictHostKeyChecking(strictHostKeyChecking);
        return this;
    }

    public Boolean isStrictHostKeyChecking(URL url) {
        return getClientConfigForUrl(url).isStrictHostKeyChecking();
    }

    public SimpleConfigurator setStrictHostKeyChecking(boolean strictHostKeyChecking, URL url) {
        getClientConfigForUrl(url).setStrictHostKeyChecking(strictHostKeyChecking);
        return this;
    }

    public Integer getTimeOut() {
        return configurator.getClientConfig().getTimeOut();
    }

    public SimpleConfigurator setTimeOut(int timeout) {
        configurator.getClientConfig().setTimeOut(timeout);
        return this;
    }

    public Integer getTimeOut(Protocol protocol) {
        return configurator.getClientConfig(protocol).getTimeOut();
    }

    public SimpleConfigurator setTimeOut(int timeout, Protocol protocol) {
        configurator.getClientConfig(protocol).setTimeOut(timeout);
        return this;
    }

    public Integer getTimeOut(URL url) {
        return getClientConfigForUrl(url).getTimeOut();
    }

    public SimpleConfigurator setTimeOut(int timeout, URL url) {
        getClientConfigForUrl(url).setTimeOut(timeout);
        return this;
    }

    public String getUsername() {
        return configurator.getClientConfig().getUsername();
    }


    public String getUsername(URL url) {
        return getClientConfigForUrl(url).getUsername();
    }

    public SimpleConfigurator setUsername(String username) {
        configurator.getClientConfig().setUsername(username);
        return this;
    }

    public SimpleConfigurator setUsername(String username, URL url) {
        getClientConfigForUrl(url).setUsername(username);
        return this;
    }

    public char[] getPassword() {
        return configurator.getClientConfig().getPassword();
    }


    public char[] getPassword(URL url) {
        return getClientConfigForUrl(url).getPassword();
    }

    public SimpleConfigurator setPassword(char[] password) {
        configurator.getClientConfig().setPassword(password);
        return this;
    }

    public SimpleConfigurator setPassword(char[] password, URL url) {
        getClientConfigForUrl(url).setPassword(password);
        return this;
    }

    public SimpleConfigurator setPassword(String password) {
        configurator.getClientConfig().setPassword(password.toCharArray());
        return this;
    }

    public SimpleConfigurator setPassword(String password, URL url) {
        getClientConfigForUrl(url).setPassword(password.toCharArray());
        return this;
    }

    private ClientConfig getClientConfigForUrl(URL url) {
        ClientConfig clientConfig = configurator.getClientConfig(url);
        if (clientConfig == null) {
            clientConfig = configurator.createClientConfig(url);
            configurator.setClientConfig(clientConfig, url);
        }
        return clientConfig;
    }
}
