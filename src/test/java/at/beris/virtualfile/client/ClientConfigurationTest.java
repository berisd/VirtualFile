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
import at.beris.virtualfile.client.sftp.SftpClientConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;

public class ClientConfigurationTest extends AbstractClientConfigurationTest {

    @Test
    public void fillFromUrl() throws Exception {
        URL url = new URL("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip");
        SftpClientConfiguration configuration = ClientConfiguration.createSFtpConfiguration();
        configuration.fillFromUrl(url);

        Assert.assertEquals("myuser", configuration.getUsername());
        Assert.assertArrayEquals("mypassword".toCharArray(), configuration.getPassword());
        Assert.assertEquals("www.example.com", configuration.getHostname());
        Assert.assertEquals(22, configuration.getPort());
    }

    @Test
    public void fillFromSite() {
        Site site = Site.create()
                .setHostname("www.example2.com")
                .setPassword("mypwd".toCharArray())
                .setUsername("myuser")
                .setPort(55)
                .setTimeout(120);

        SftpClientConfiguration configuration = ClientConfiguration.createSFtpConfiguration();
        configuration.fillFromSite(site);
        Assert.assertEquals("www.example2.com", configuration.getHostname());
        Assert.assertArrayEquals("mypwd".toCharArray(), configuration.getPassword());
        Assert.assertEquals(55, configuration.getPort());
        Assert.assertEquals(120, configuration.getTimeout());
        Assert.assertEquals("myuser", configuration.getUsername());
    }

    @Test
    public void fillFromClientConfiguration() {
        SftpClientConfiguration configurationTemplate = ClientConfiguration.createSFtpConfiguration();
        configurationTemplate.setHostname("www.example3.com");
        configurationTemplate.setUsername("usr");
        configurationTemplate.setPassword("pwd".toCharArray());
        configurationTemplate.setPort(12);
        configurationTemplate.setTimeout(56);

        SftpClientConfiguration configuration = ClientConfiguration.createSFtpConfiguration();
        configuration.fillFromClientConfiguration(configurationTemplate);
        Assert.assertEquals("www.example3.com", configuration.getHostname());
        Assert.assertArrayEquals("pwd".toCharArray(), configuration.getPassword());
        Assert.assertEquals(12, configuration.getPort());
        Assert.assertEquals(56, configuration.getTimeout());
        Assert.assertEquals("usr", configuration.getUsername());
    }

}