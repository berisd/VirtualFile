/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.RemoteSite;
import at.beris.virtualfile.protocol.Protocol;

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

    public AuthenticationType getAuthenticationType(RemoteSite site) {
        return getClientConfigForSite(site).getAuthenticationType();
    }

    public SimpleConfigurator setAuthenticationType(AuthenticationType authenticationType, RemoteSite site) {
        getClientConfigForSite(site).setAuthenticationType(authenticationType);
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

    public String getKnownHostsFile(RemoteSite site) {
        return getClientConfigForSite(site).getKnownHostsFile();
    }

    public SimpleConfigurator setKnownHostsFile(String knownHostsFile, RemoteSite site) {
        getClientConfigForSite(site).setKnownHostsFile(knownHostsFile);
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

    public String getPrivateKeyFile(RemoteSite site) {
        return getClientConfigForSite(site).getPrivateKeyFile();
    }

    public SimpleConfigurator setPrivateKeyFile(String privateKeyFile, RemoteSite site) {
        getClientConfigForSite(site).setPrivateKeyFile(privateKeyFile);
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

    public Boolean isStrictHostKeyChecking(RemoteSite site) {
        return getClientConfigForSite(site).isStrictHostKeyChecking();
    }

    public SimpleConfigurator setStrictHostKeyChecking(boolean strictHostKeyChecking, RemoteSite site) {
        getClientConfigForSite(site).setStrictHostKeyChecking(strictHostKeyChecking);
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

    public Integer getTimeOut(RemoteSite site) {
        return getClientConfigForSite(site).getTimeOut();
    }

    public SimpleConfigurator setTimeOut(int timeout, RemoteSite site) {
        getClientConfigForSite(site).setTimeOut(timeout);
        return this;
    }

    private ClientConfig getClientConfigForSite(RemoteSite site) {
        ClientConfig clientConfig = configurator.getClientConfig(site);
        if (clientConfig == null) {
            clientConfig = configurator.createClientConfig(site);
            configurator.setClientConfig(clientConfig, site);
        }
        return clientConfig;
    }
}
