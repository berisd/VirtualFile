/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

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

}