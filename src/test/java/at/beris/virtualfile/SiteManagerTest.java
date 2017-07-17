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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class SiteManagerTest {
    @BeforeClass
    public static void beforeTest() {
        UrlUtils.registerProtocolURLStreamHandlers();
    }

    @AfterClass
    public static void afterTest() {
        UrlUtils.unregisterProtocolURLStreamHandlers();
    }

    @Test
    public void getSiteUrlStringFromUrl() throws MalformedURLException {
        Assert.assertEquals("sftp://myuser:mypassword@www.example.com:22", SiteManager.getSiteUrlString(new URL("sftp://myuser:mypassword@www.example.com:22/home/myuser/mydocuments.zip")));
        Assert.assertEquals("file://", SiteManager.getSiteUrlString(new URL("file:/")));
    }

    @Test
    public void getSiteUrlStringFromSite() {
        Site site = Site.create().setHostname("www.example.com").setPort(80).setProtocol(Protocol.HTTP).setUsername("user").setPassword("pwd".toCharArray());
        Assert.assertEquals("http://user:pwd@www.example.com:80", SiteManager.getSiteUrlString(site));
    }

    @Test
    public void saveSites() {
        SiteManager siteManager = TestHelper.createSiteManager();

        char[] password = {'p', 'w', 'd', '1'};
        String shortname = "TESTS1";
        String name = "Testsite1";

        Site site1 = Site.create().setShortName(shortname).setName(name).setProtocol(Protocol.FTP).setPassword(password).setUsername("testuser1")
                .setPort(92).setHostname("www.example1.com").setAuthenticationType(AuthenticationType.PUBLIC_KEY);
        siteManager.addSite(site1);

        char[] password2 = {'p', 'w', 'd', '2'};

        siteManager.addSite(Site.create().setShortName("TESTS2").setName("Testsite2").setProtocol(Protocol.HTTP).setPassword(password2).setUsername("testuser2")
                .setPort(80).setHostname("www.example2.com"));

        siteManager.save();

        SiteManager siteManager2 = TestHelper.createSiteManager();
        siteManager2.load();

        Assert.assertEquals(2, siteManager.getSites().size());
        Assert.assertNotSame(siteManager.getSites().get(0), siteManager.getSites().get(1));
        Assert.assertNotNull(siteManager2.findSiteByName(name));
        Assert.assertEquals(shortname, siteManager.getSites().get(0).getShortName());
        Assert.assertArrayEquals(password, siteManager.getSites().get(0).getPassword());
        Assert.assertArrayEquals(password2, siteManager.getSites().get(1).getPassword());
        Assert.assertEquals(name, siteManager.findSiteById(site1.getId()).get().getName());
        Assert.assertEquals(name, siteManager.findSiteByShortName(shortname).get().getName());

    }

}