/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.provider.IFileOperationProvider;
import at.beris.virtualfile.util.FileUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {
    @BeforeClass
    public static void setUp() {
        FileManager.registerProtocolURLStreamHandlers();
    }

    @Test
    public void maskLocalUnixFileString() throws Exception {
        String urlString = "file:/home/bernd/IdeaProjects/VirtualFile/testfile1.txt";
        IFile file = createFile(urlString);
        assertEquals(urlString, FileUtils.maskedUrlString(file.getUrl()));
    }

    @Test
    @Ignore
    public void maskLocalWindowsFileString() throws Exception {
        //TODO urls with Windows style Filename not working
        String urlString = "file:///C:/Documents%20and%20Settings/davris/FileSchemeURIs.doc";
        IFile file = createFile(urlString);
        assertEquals(urlString, FileUtils.maskedUrlString(file.getUrl()));
    }

    @Test
    public void maskSftpFileString() throws Exception {
        String urlString = "sftp://sshtest:mypassword@www.example.com:22/home/sshtest/targetfile1.txt";
        String expectedString = "sftp://sshtest:***@www.example.com:22/home/sshtest/targetfile1.txt";
        IFile file = createFile(urlString);
        assertEquals(expectedString, FileUtils.maskedUrlString(file.getUrl()));
    }

    private IFile createFile(String urlString) throws MalformedURLException {
        IFileOperationProvider fileOperationProvider = Mockito.mock(IFileOperationProvider.class);
        IClient client = Mockito.mock(IClient.class);
        FileModel model = new FileModel();
        return new File(new URL(urlString), model, Collections.singletonMap(FileType.DEFAULT, fileOperationProvider), client);
    }
}
