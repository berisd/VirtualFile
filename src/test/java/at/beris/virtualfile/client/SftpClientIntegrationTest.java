/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.FileTestHelper;
import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.client.sftp.SftpFile;
import at.beris.virtualfile.client.sftp.SftpFileTranslator;
import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import static org.junit.Assert.*;

public class SftpClientIntegrationTest {
    private static final String TEST_FILE = FileTestHelper.SSH_HOME_DIRECTORY + FileTestHelper.TEST_SOURCE_FILE_NAME;
    private static final String TEST_DIRECTORY = FileTestHelper.SSH_HOME_DIRECTORY + FileTestHelper.TEST_SOURCE_DIRECTORY_NAME;
    private static final String TEST_STRING = "This is a test string";

    private static SftpClient sftpClient;

    @BeforeClass
    public static void setUp() throws Exception {
        FileTestHelper.initIntegrationTest();
        UrlUtils.registerProtocolURLStreamHandlers();
        sftpClient = createSftpClient();
        sftpClient.connect();
    }

    @AfterClass
    public static void tearDown() {
        cleanUp();
        if (sftpClient != null)
            sftpClient.disconnect();
    }

    @Test
    public void createFile() throws Exception {
        sftpClient.createFile(TEST_FILE);
        assertTrue(sftpClient.exists(TEST_FILE));
        sftpClient.deleteFile(TEST_FILE);
        assertFalse(sftpClient.exists(TEST_FILE));
    }

    @Test
    public void makeDirectory() {
        sftpClient.createDirectory(TEST_DIRECTORY);
        assertTrue(sftpClient.exists(TEST_DIRECTORY));
        sftpClient.deleteDirectory(TEST_DIRECTORY);
        assertFalse(sftpClient.exists(TEST_DIRECTORY));
    }

    @Test
    public void writeToFile() throws Exception {
        sftpClient.createFile(TEST_FILE);
        try (OutputStream outputstream = sftpClient.getOutputStream(TEST_FILE)) {
            outputstream.write(TEST_STRING.getBytes());

        }

        SftpFile fileInfo = sftpClient.getFileInfo(TEST_FILE);
        FileModel model = new FileModel();
        SftpFileTranslator.fillModel(model, fileInfo);

        assertNotNull(model.getLastModifiedTime());
        assertEquals(TEST_STRING.length(), model.getSize());
        sftpClient.deleteFile(TEST_FILE);
    }

    @Test
    public void readFromFile() throws Exception {
        sftpClient.createFile(TEST_FILE);
        try (OutputStream outputstream = sftpClient.getOutputStream(TEST_FILE)) {
            outputstream.write(TEST_STRING.getBytes());

        }

        byte[] bytesReadArray = new byte[TEST_STRING.length()];
        int bytesRead = 0;
        try (InputStream inputstream = sftpClient.getInputStream(TEST_FILE)) {
            bytesRead = inputstream.read(bytesReadArray);
        }

        assertEquals(TEST_STRING.length(), bytesRead);
        assertArrayEquals(TEST_STRING.getBytes(), bytesReadArray);
        sftpClient.deleteFile(TEST_FILE);
    }

    @Test
    public void fileNotExists() {
        assertFalse(sftpClient.exists(FileTestHelper.SSH_HOME_DIRECTORY + "abcdef.txt"));
    }

    @Test(expected = VirtualFileException.class)
    public void permissionDenied() throws Exception {
        sftpClient.createFile("../abc.txt");
    }

    private static SftpClient createSftpClient() throws Exception {
        Configuration configuration = new Configuration();
        configuration.initValues();
        char[] password = FileTestHelper.readSftpPassword().toCharArray();
        URL url = new URL("sftp://sshtest:" + String.valueOf(password) + "@www.beris.at:22");
        return new SftpClient(url, configuration);
    }

    private static void cleanUp() {
        if (sftpClient != null && sftpClient.exists(TEST_FILE))
            sftpClient.deleteFile(TEST_FILE);
        if (sftpClient != null && sftpClient.exists(TEST_DIRECTORY))
            sftpClient.deleteFile(TEST_DIRECTORY);
    }
}