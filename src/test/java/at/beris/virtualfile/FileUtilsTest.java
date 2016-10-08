/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.util.UrlUtils;
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
