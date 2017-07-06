/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.config.value.AuthenticationType;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class ConfiguratorTest {
    private Configurator config;

    @Before
    public void setUp() {
        config = new Configurator();
        UrlUtils.registerProtocolURLStreamHandlers();
    }

    @Test
    public void createClientConfigCheckCloning() throws Exception {
        URL url1 = new URL("file://test:pwd@site1.example.com:22/test.txt");
        URL url2 = new URL("file://test:pwd@site2.example.com:22/test.txt");
        UrlFileConfiguration siteConfig1 = config.createConfiguration(url1);
        UrlFileConfiguration siteConfig2 = config.createConfiguration(url2);

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
        VirtualFile file = Mockito.mock(VirtualFile.class);
        Mockito.when(file.getUrl()).thenReturn(new URL("sftp://test:pwd@site.example.com:22/test.txt"));

        config.getConfiguration().setTimeOut(111);
        UrlFileConfiguration configuration = config.createConfiguration(file.getUrl());
        assertEquals(111, (int) configuration.getTimeOut());

        config.getConfiguration(Protocol.SFTP).setTimeOut(222);
        configuration = config.createConfiguration(file.getUrl());
        assertEquals(222, (int) configuration.getTimeOut());

        config.getConfiguration(file).setTimeOut(333);
        configuration = config.createConfiguration(file.getUrl());
        assertEquals(333, (int) configuration.getTimeOut());
    }

    @Test
    public void createClientConfigCheckRemoval() throws Exception {
        VirtualFile file = Mockito.mock(VirtualFile.class);
        Mockito.when(file.getUrl()).thenReturn(new URL("sftp://test:pwd@site.example.com:22/test.txt"));

        config.createConfiguration(file.getUrl());

        config.getConfiguration().setTimeOut(111);
        config.getConfiguration(Protocol.SFTP).setTimeOut(222);
        config.getConfiguration(file).setTimeOut(333);
        UrlFileConfiguration configuration = config.createConfiguration(file.getUrl());
        assertEquals(333, (int) configuration.getTimeOut());

        config.getConfiguration(file).remove(UrlFileConfigurationOption.TIMEOUT);
        configuration = config.createConfiguration(file.getUrl());
        assertEquals(222, (int) configuration.getTimeOut());

        config.getConfiguration(Protocol.SFTP).remove(UrlFileConfigurationOption.TIMEOUT);
        configuration = config.createConfiguration(file.getUrl());
        assertEquals(111, (int) configuration.getTimeOut());
    }

    @Test
    public void setClientConfigValue() throws Exception {
        VirtualFile file = Mockito.mock(VirtualFile.class);
        Mockito.when(file.getUrl()).thenReturn(new URL("sftp://test:pwd@site.example.com:22/test.txt"));

        config.createConfiguration(file.getUrl());
        config.getConfiguration().setTimeOut(10);
        config.getConfiguration(Protocol.SFTP).setTimeOut(20);
        config.getConfiguration(file).setTimeOut(30);

        assertEquals(10, (int) config.getConfiguration().getTimeOut());
        assertEquals(20, (int) config.getConfiguration(Protocol.SFTP).getTimeOut());
        assertEquals(30, (int) config.getConfiguration(file).getTimeOut());
    }

    @Test
    public void removeClientConfigValues() throws Exception {
        VirtualFile file = Mockito.mock(VirtualFile.class);
        Mockito.when(file.getUrl()).thenReturn(new URL("sftp://test:pwd@site.example.com:22/test.txt"));

        config.createConfiguration(file.getUrl());
        config.getConfiguration().setTimeOut(10);
        config.getConfiguration(Protocol.SFTP).setTimeOut(20);
        config.getConfiguration(file).setTimeOut(30);

        assertEquals(10, (int) config.getConfiguration().getTimeOut());
        assertEquals(20, (int) config.getConfiguration(Protocol.SFTP).getTimeOut());
        assertEquals(30, (int) config.getConfiguration(file).getTimeOut());

        assertEquals(Integer.valueOf(30), config.getConfiguration(file).getTimeOut());
        config.getConfiguration(file).remove(UrlFileConfigurationOption.TIMEOUT);
        assertEquals(Integer.valueOf(20), config.getConfiguration(file).getTimeOut());
        config.getConfiguration(Protocol.SFTP).remove(UrlFileConfigurationOption.TIMEOUT);
        assertEquals(Integer.valueOf(10), config.getConfiguration(file).getTimeOut());
        config.getConfiguration().remove(UrlFileConfigurationOption.TIMEOUT);
        assertNull(config.getConfiguration(file).getTimeOut());
    }
}