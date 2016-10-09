/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.UrlFile;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {
    @BeforeClass
    public static void setUp() {
        UrlUtils.registerProtocolURLStreamHandlers();
    }

    @Test
    public void isArchive() {
        Assert.assertTrue(FileUtils.isArchive("test.zip"));
    }

    @Test
    public void isArchived() {
        Assert.assertTrue(FileUtils.isArchived("test.zip/testfile.txt"));
    }

    @Test
    public void isDirectory() {
        Assert.assertTrue(FileUtils.isDirectory("test/"));
        Assert.assertFalse(FileUtils.isDirectory("test"));
        Assert.assertFalse(FileUtils.isDirectory("test.txt"));
    }

    @Test
    public void getArchiveExtensions() {
        Assert.assertTrue(FileUtils.getArchiveExtensions().contains("7Z"));
    }

    @Test
    public void getAttributesString() {
        Assert.assertEquals("OWNER_READ, GROUP_READ", FileUtils.getAttributesString(new FileAttribute[]{PosixFilePermission.OWNER_READ, PosixFilePermission.GROUP_READ}));
        Assert.assertEquals("<none>", FileUtils.getAttributesString(new FileAttribute[]{}));
    }

    @Test
    public void getName() {
        Assert.assertEquals("file.txt", FileUtils.getName("/home/tester/file.txt"));
        Assert.assertEquals("tester", FileUtils.getName("/home/tester"));
        Assert.assertEquals("tester", FileUtils.getName("/home/tester/"));
    }

    @Test
    public void maskLocalUnixFileString() throws Exception {
        String urlString = "file:/home/bernd/IdeaProjects/VirtualFile/testfile1.txt";
        VirtualFile file = createFile(urlString);
        assertEquals(urlString, UrlUtils.maskedUrlString(file.getUrl()));
    }

    @Test
    @Ignore
    public void maskLocalWindowsFileString() throws Exception {
        //TODO urls with Windows style Filename not working
        String urlString = "file:///C:/Documents%20and%20Settings/davris/FileSchemeURIs.doc";
        VirtualFile file = createFile(urlString);
        assertEquals(urlString, UrlUtils.maskedUrlString(file.getUrl()));
    }

    @Test
    public void maskSftpFileString() throws Exception {
        String urlString = "sftp://sshtest:mypassword@www.example.com:22/home/sshtest/targetfile1.txt";
        String expectedString = "sftp://sshtest:***@www.example.com:22/home/sshtest/targetfile1.txt";
        VirtualFile file = createFile(urlString);
        assertEquals(expectedString, UrlUtils.maskedUrlString(file.getUrl()));
    }

    private VirtualFile createFile(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        FileContext context = Mockito.mock(FileContext.class);

        return new UrlFile(url, context);
    }
}
