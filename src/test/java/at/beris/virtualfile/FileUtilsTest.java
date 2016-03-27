/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FileUtilsTest {
    @BeforeClass
    public static void setUp() {
        FileManager.registerProtocolURLStreamHandlers();
    }

    @Test
    public void maskLocalUnixFileString() throws Exception {
        String urlString = "file:/home/bernd/IdeaProjects/VirtualFile/testfile1.txt";
        File file = createFile(urlString);
        assertEquals(urlString, UrlUtils.maskedUrlString(file.getUrl()));
    }

    @Test
    @Ignore
    public void maskLocalWindowsFileString() throws Exception {
        //TODO urls with Windows style Filename not working
        String urlString = "file:///C:/Documents%20and%20Settings/davris/FileSchemeURIs.doc";
        File file = createFile(urlString);
        assertEquals(urlString, UrlUtils.maskedUrlString(file.getUrl()));
    }

    @Test
    public void maskSftpFileString() throws Exception {
        String urlString = "sftp://sshtest:mypassword@www.example.com:22/home/sshtest/targetfile1.txt";
        String expectedString = "sftp://sshtest:***@www.example.com:22/home/sshtest/targetfile1.txt";
        File file = createFile(urlString);
        assertEquals(expectedString, UrlUtils.maskedUrlString(file.getUrl()));
    }

    private File createFile(String urlString) throws MalformedURLException {
        Map<FileOperationEnum, FileOperation> fileOperationMap = new HashMap<>();
        fileOperationMap.put(FileOperationEnum.UPDATE_MODEL, Mockito.mock(FileOperation.class));

        URL url = new URL(urlString);
        FileContext context = Mockito.mock(FileContext.class);
        Mockito.when(context.getFileOperationMap(url)).thenReturn(fileOperationMap);

        return new UrlFile(null, url, context);
    }
}
