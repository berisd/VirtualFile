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
import at.beris.virtualfile.config.value.AuthenticationType;
import at.beris.virtualfile.protocol.Protocol;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ConfiguratorTest {
    private Configurator config;

    @Before
    public void setUp() {
        config = new Configurator();
        new FileContext(config).registerProtocolURLStreamHandlers();
    }

    @Test
    public void createClientConfigCheckCloning() throws Exception {
        URL url1 = new URL("file://test:pwd@site1.example.com:22/test.txt");
        URL url2 = new URL("file://test:pwd@site2.example.com:22/test.txt");
        ClientConfig siteConfig1 = config.createClientConfig(url1);
        ClientConfig siteConfig2 = config.createClientConfig(url2);

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
        URL url = new URL("sftp://test:pwd@site.example.com:22/test.txt");

        config.getClientConfig().setTimeOut(111);
        ClientConfig clientConfig = config.createClientConfig(url);
        assertEquals(111, (int) clientConfig.getTimeOut());

        config.getClientConfig(Protocol.SFTP).setTimeOut(222);
        clientConfig = config.createClientConfig(url);
        assertEquals(222, (int) clientConfig.getTimeOut());

        config.getClientConfig(url).setTimeOut(333);
        clientConfig = config.createClientConfig(url);
        assertEquals(333, (int) clientConfig.getTimeOut());
    }

    @Test
    public void createClientConfigCheckRemoval() throws Exception {
        URL url = new URL("sftp://test:pwd@site.example.com:22/test.txt");
        config.createClientConfig(url);

        config.getClientConfig().setTimeOut(111);
        config.getClientConfig(Protocol.SFTP).setTimeOut(222);
        config.getClientConfig(url).setTimeOut(333);
        ClientConfig clientConfig = config.createClientConfig(url);
        assertEquals(333, (int) clientConfig.getTimeOut());

        config.getClientConfig(url).remove(ClientConfigOption.TIMEOUT);
        clientConfig = config.createClientConfig(url);
        assertEquals(222, (int) clientConfig.getTimeOut());

        config.getClientConfig(Protocol.SFTP).remove(ClientConfigOption.TIMEOUT);
        clientConfig = config.createClientConfig(url);
        assertEquals(111, (int) clientConfig.getTimeOut());
    }

    @Test
    public void setClientConfigValue() throws Exception {
        URL url = new URL("sftp://test:pwd@site.example.com:22/test.txt");
        config.createClientConfig(url);
        config.getClientConfig().setTimeOut(10);
        config.getClientConfig(Protocol.SFTP).setTimeOut(20);
        config.getClientConfig(url).setTimeOut(30);

        assertEquals(10, (int) config.getClientConfig().getTimeOut());
        assertEquals(20, (int) config.getClientConfig(Protocol.SFTP).getTimeOut());
        assertEquals(30, (int) config.getClientConfig(url).getTimeOut());
    }

    @Test
    public void removeClientConfigValues() throws Exception {
        URL url = new URL("sftp://test:pwd@site.example.com:22/test.txt");
        config.createClientConfig(url);
        config.getClientConfig().setTimeOut(10);
        config.getClientConfig(Protocol.SFTP).setTimeOut(20);
        config.getClientConfig(url).setTimeOut(30);

        assertEquals(10, (int) config.getClientConfig().getTimeOut());
        assertEquals(20, (int) config.getClientConfig(Protocol.SFTP).getTimeOut());
        assertEquals(30, (int) config.getClientConfig(url).getTimeOut());

        assertEquals(Integer.valueOf(30), config.getClientConfig(url).getTimeOut());
        config.getClientConfig(url).remove(ClientConfigOption.TIMEOUT);
        assertEquals(Integer.valueOf(20), config.getClientConfig(url).getTimeOut());
        config.getClientConfig(Protocol.SFTP).remove(ClientConfigOption.TIMEOUT);
        assertEquals(Integer.valueOf(10), config.getClientConfig(url).getTimeOut());
        config.getClientConfig().remove(ClientConfigOption.TIMEOUT);
        assertNull(config.getClientConfig(url).getTimeOut());
    }
}