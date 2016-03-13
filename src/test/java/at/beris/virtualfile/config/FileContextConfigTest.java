/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.RemoteSite;
import at.beris.virtualfile.UrlSite;
import at.beris.virtualfile.protocol.Protocol;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class FileContextConfigTest {
    private FileContextConfig config;

    @Before
    public void setUp() {
        config = new FileContextConfig();
        new FileContext(config).registerProtocolURLStreamHandlers();
    }

    @Test
    public void createClientConfigCheckCloning() throws Exception {
        RemoteSite site1 = new UrlSite(new URL("file://test:pwd@site1.example.com:22/test.txt"));
        RemoteSite site2 = new UrlSite(new URL("file://test:pwd@site2.example.com:22/test.txt"));
        ClientConfig siteConfig1 = config.createClientConfig(site1);
        ClientConfig siteConfig2 = config.createClientConfig(site2);

        assertNotSame(siteConfig1, siteConfig2);
        siteConfig1.setTimeOut(1);
        siteConfig2.setTimeOut(2);
        assertNotSame(siteConfig1.getTimeOut(), siteConfig2.getTimeOut());

        siteConfig1.setStrictHostKeyChecking(false);
        siteConfig2.setStrictHostKeyChecking(true);
        assertNotSame(siteConfig1.isStrictHostKeyChecking(), siteConfig2.isStrictHostKeyChecking());

        siteConfig1.setAuthenticationType(AuthenticationType.PASSWORD);
        siteConfig2.setAuthenticationType(AuthenticationType.PUBLIC_KEY);
        assertNotSame(siteConfig1.getAuthenticationType(), siteConfig2.getAuthenticationType());
    }

    @Test
    public void createClientConfigCheckInheritance() throws Exception {
        RemoteSite site = new UrlSite(new URL("sftp://test:pwd@site.example.com:22/test.txt"));

        config.getClientConfig().setTimeOut(111);
        ClientConfig clientConfig = config.createClientConfig(site);
        assertEquals(111, (int) clientConfig.getTimeOut());

        config.getClientConfig(Protocol.SFTP).setTimeOut(222);
        clientConfig = config.createClientConfig(site);
        assertEquals(222, (int) clientConfig.getTimeOut());

        config.getClientConfig(site).setTimeOut(333);
        clientConfig = config.createClientConfig(site);
        assertEquals(333, (int) clientConfig.getTimeOut());
    }

    @Test
    public void createClientConfigCheckRemoval() throws Exception {
        RemoteSite site = new UrlSite(new URL("sftp://test:pwd@site.example.com:22/test.txt"));
        config.setClientConfig(new ClientConfig(), site);

        config.getClientConfig().setTimeOut(111);
        config.getClientConfig(Protocol.SFTP).setTimeOut(222);
        config.getClientConfig(site).setTimeOut(333);
        ClientConfig clientConfig = config.createClientConfig(site);
        assertEquals(333, (int) clientConfig.getTimeOut());

        config.getClientConfig(site).remove(ClientConfigOption.TIMEOUT);
        clientConfig = config.createClientConfig(site);
        assertEquals(222, (int) clientConfig.getTimeOut());

        config.getClientConfig(Protocol.SFTP).remove(ClientConfigOption.TIMEOUT);
        clientConfig = config.createClientConfig(site);
        assertEquals(111, (int) clientConfig.getTimeOut());
    }

    @Test
    public void setClientConfigValue() throws Exception {
        RemoteSite site = new UrlSite(new URL("sftp://test:pwd@site.example.com:22/test.txt"));
        config.setClientConfig(new ClientConfig(), site);
        config.getClientConfig().setTimeOut(10);
        config.getClientConfig(Protocol.SFTP).setTimeOut(20);
        config.getClientConfig(site).setTimeOut(30);

        assertEquals(10, (int) config.getClientConfig().getTimeOut());
        assertEquals(20, (int) config.getClientConfig(Protocol.SFTP).getTimeOut());
        assertEquals(30, (int) config.getClientConfig(site).getTimeOut());
    }

    @Test
    public void removeClientConfigValues() throws Exception {
        RemoteSite site = new UrlSite(new URL("sftp://test:pwd@site.example.com:22/test.txt"));
        config.setClientConfig(new ClientConfig(), site);
        config.getClientConfig().setTimeOut(10);
        config.getClientConfig(Protocol.SFTP).setTimeOut(20);
        config.getClientConfig(site).setTimeOut(30);

        assertEquals(10, (int) config.getClientConfig().getTimeOut());
        assertEquals(20, (int) config.getClientConfig(Protocol.SFTP).getTimeOut());
        assertEquals(30, (int) config.getClientConfig(site).getTimeOut());

        config.getClientConfig(site).remove(ClientConfigOption.TIMEOUT);
        assertNull(config.getClientConfig(site).getTimeOut());
        config.getClientConfig(Protocol.SFTP).remove(ClientConfigOption.TIMEOUT);
        assertNull(config.getClientConfig(Protocol.SFTP).getTimeOut());
        config.getClientConfig(site).remove(ClientConfigOption.TIMEOUT);
        assertNull(config.getClientConfig(site).getTimeOut());
    }
}