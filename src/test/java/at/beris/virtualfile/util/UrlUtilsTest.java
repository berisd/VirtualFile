/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.FileType;
import at.beris.virtualfile.protocol.Protocol;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static at.beris.virtualfile.util.UrlUtils.PROPERTY_KEY_PROTOCOL_HANDLER_PKGS;

public class UrlUtilsTest {

    public static final String FILE_URL_STRING = "ftp://www.example.com/test.txt";

    @Test
    public void getProtocol() throws MalformedURLException {
        Assert.assertEquals(Protocol.FTP, UrlUtils.getProtocol(new URL(FILE_URL_STRING)));
    }

    @Test
    public void getSiteUrlString() {
        Assert.assertEquals("ftp://www.example.com", UrlUtils.getSiteUrlString(FILE_URL_STRING));
        Assert.assertEquals("ftp://www.example.com", UrlUtils.getSiteUrlString("ftp://www.example.com/test/"));
    }

    @Test
    public void getFileTypeForUrl() {
        Assert.assertEquals(FileType.DEFAULT, UrlUtils.getFileTypeForUrl(FILE_URL_STRING));
        Assert.assertEquals(FileType.ARCHIVE, UrlUtils.getFileTypeForUrl("ftp://www.example.com/test.zip"));
        Assert.assertEquals(FileType.ARCHIVED, UrlUtils.getFileTypeForUrl("ftp://www.example.com/test.zip/test.txt"));
    }

    @Test
    public void normalizeUrl() throws MalformedURLException {
        Assert.assertEquals("ftp://www.example.com/release/file.zip", UrlUtils.normalizeUrl(new URL("ftp://www.example.com/test/../release/file.zip")).toString());
    }

    @Test
    public void newUrl() {
        Assert.assertEquals(FILE_URL_STRING, UrlUtils.newUrl(FILE_URL_STRING).toString());
    }

    @Test
    public void newUrlWithSpec() throws MalformedURLException {
        Assert.assertEquals("ftp://www.example.com/mydir/newsubdir", UrlUtils.newUrl(new URL("ftp://www.example.com/mydir/oldsubdir"), "newsubdir").toString());
    }

    @Test
    public void newUrlReplacePath() throws MalformedURLException {
        Assert.assertEquals("ftp://www.example.com/newsubdir", UrlUtils.newUrlReplacePath(new URL("ftp://www.example.com/mydir/oldsubdir"), "/newsubdir").toString());
    }

    @Test
    public void getUrlForLocalPath() throws MalformedURLException {
        Assert.assertEquals(new File("home/test").toURI().toURL().toString(), UrlUtils.getUrlForLocalPath("home/test").toString());
    }

    @Test
    public void maskedUrlString() throws MalformedURLException {
        Assert.assertEquals("ftp://tester:***@www.example.com", UrlUtils.maskedUrlString(new URL("ftp://tester:testpwd@www.example.com")));
    }

    @Test
    public void getParentUrl() throws MalformedURLException {
        Assert.assertEquals("file://home/tester/", UrlUtils.getParentUrl(new URL("file://home/tester/test.txt")).toString());
        Assert.assertNull(UrlUtils.getParentUrl(new URL("file:///")));
        Assert.assertNull(UrlUtils.getParentUrl(new URL("ftp://ftp.test.com")));
        Assert.assertNull(UrlUtils.getParentUrl(new URL("ftp://ftp.test.com/")));
    }

    @Test
    public void getParentPath() throws MalformedURLException {
        Assert.assertEquals("file://home/tester/", UrlUtils.getParentPath("file://home/tester/test.txt"));
        Assert.assertNull(UrlUtils.getParentUrl(new URL("ftp://ftp.test.com/")));
    }

    @Test
    public void getLastPathPart() {
        Assert.assertEquals("test.txt", UrlUtils.getLastPathPart("file://home/tester/test.txt"));
    }

    @Test
    public void unregisterProtocolURLStreamHandlers() {
        String packageName = at.beris.virtualfile.protocol.Protocol.class.getPackage().getName();
        UrlUtils.registerProtocolURLStreamHandlers();
        Assert.assertTrue(System.getProperties().getProperty(PROPERTY_KEY_PROTOCOL_HANDLER_PKGS).contains(packageName));
        UrlUtils.unregisterProtocolURLStreamHandlers();
        Assert.assertFalse(System.getProperties().getProperty(PROPERTY_KEY_PROTOCOL_HANDLER_PKGS).contains(packageName));
    }

    @Test
    public void registerProtocolURLStreamHandlers() {
        String packageName = at.beris.virtualfile.protocol.Protocol.class.getPackage().getName();
        UrlUtils.unregisterProtocolURLStreamHandlers();
        Assert.assertFalse(System.getProperties().getProperty(PROPERTY_KEY_PROTOCOL_HANDLER_PKGS).contains(packageName));
        UrlUtils.registerProtocolURLStreamHandlers();
        Assert.assertTrue(System.getProperties().getProperty(PROPERTY_KEY_PROTOCOL_HANDLER_PKGS).contains(packageName));

    }
}