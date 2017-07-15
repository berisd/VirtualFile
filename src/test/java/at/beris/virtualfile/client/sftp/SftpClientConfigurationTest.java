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
import at.beris.virtualfile.client.AbstractClientConfigurationTest;
import at.beris.virtualfile.client.ClientConfiguration;
import org.junit.Assert;
import org.junit.Test;

public class SftpClientConfigurationTest extends AbstractClientConfigurationTest {
    @Test
    public void fillFromSite() {
        Site site = Site.create().setAuthenticationType(AuthenticationType.PUBLIC_KEY)
                .setKnownHostsFile("knownhosts.txt")
                .setPrivateKeyFile("prvkey.txt")
                .setHostname("www.example2.com")
                .setStrictHostKeyChecking(false);

        SftpClientConfiguration configuration = ClientConfiguration.createSFtpConfiguration();
        configuration.fillFromSite(site);
        Assert.assertEquals(AuthenticationType.PUBLIC_KEY, configuration.getAuthenticationType());
        Assert.assertEquals("knownhosts.txt", configuration.getKnownHostsFile());
        Assert.assertEquals("prvkey.txt", configuration.getPrivateKeyFile());
        Assert.assertEquals(false, configuration.isStrictHostKeyChecking());
    }

    @Test
    public void fillFromClientConfiguration() {
        SftpClientConfiguration configurationTemplate = ClientConfiguration.createSFtpConfiguration()
                .setAuthenticationType(AuthenticationType.PUBLIC_KEY)
                .setKnownHostsFile("knownhosts.txt")
                .setPrivateKeyFile("prvkey.txt")
                .setHostname("www.example2.com")
                .setStrictHostKeyChecking(false);

        SftpClientConfiguration configuration = ClientConfiguration.createSFtpConfiguration();
        configuration.fillFromClientConfiguration(configurationTemplate);
        Assert.assertEquals(AuthenticationType.PUBLIC_KEY, configuration.getAuthenticationType());
        Assert.assertEquals("knownhosts.txt", configuration.getKnownHostsFile());
        Assert.assertEquals("prvkey.txt", configuration.getPrivateKeyFile());
        Assert.assertEquals(false, configuration.isStrictHostKeyChecking());
    }

}