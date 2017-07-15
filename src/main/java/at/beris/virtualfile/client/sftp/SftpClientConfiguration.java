/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.sftp;

import at.beris.virtualfile.Site;
import at.beris.virtualfile.client.ClientConfiguration;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.StringUtils;

import java.nio.file.Paths;

public class SftpClientConfiguration extends ClientConfiguration<SftpClientConfiguration> {

    private boolean strictHostKeyChecking;

    private String knownHostsFile;

    private AuthenticationType authenticationType;

    private String privateKeyFile;

    public SftpClientConfiguration() {
        super();
        setPort(Protocol.SFTP.getDefaultPort());
        setStrictHostKeyChecking(true);
        setKnownHostsFile(Paths.get(System.getProperty("user.home"), ".ssh", "known_hosts").toString());
        setAuthenticationType(AuthenticationType.PASSWORD);
        setPrivateKeyFile(StringUtils.EMPTY_STRING);
    }

    @Override
    public void fillFromSite(Site site) {
        super.fillFromSite(site);
        setAuthenticationType(site.getAuthenticationType());
        setKnownHostsFile(site.getKnownHostsFile());
        setPrivateKeyFile(site.getPrivateKeyFile());
        setStrictHostKeyChecking(site.isStrictHostKeyChecking());
    }

    @Override
    public void fillFromClientConfiguration(ClientConfiguration clientConfig) {
        super.fillFromClientConfiguration(clientConfig);
        SftpClientConfiguration sftpClientConfig = (SftpClientConfiguration) clientConfig;
        setAuthenticationType(sftpClientConfig.getAuthenticationType());
        setKnownHostsFile(sftpClientConfig.getKnownHostsFile());
        setPrivateKeyFile(sftpClientConfig.getPrivateKeyFile());
        setStrictHostKeyChecking(sftpClientConfig.isStrictHostKeyChecking());
    }

    public boolean isStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    public String getKnownHostsFile() {
        return knownHostsFile;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

    public String getPrivateKeyFile() {
        return privateKeyFile;
    }

    public SftpClientConfiguration setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
        return this;
    }

    public SftpClientConfiguration setKnownHostsFile(String knownHostsFile) {
        this.knownHostsFile = knownHostsFile;
        return this;
    }

    public SftpClientConfiguration setAuthenticationType(AuthenticationType authenticationType) {
        this.authenticationType = authenticationType;
        return this;
    }

    public SftpClientConfiguration setPrivateKeyFile(String privateKeyFile) {
        this.privateKeyFile = privateKeyFile;
        return this;
    }

}
