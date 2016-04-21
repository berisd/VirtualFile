/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.TestFileHelper;
import at.beris.virtualfile.client.ftp.FtpClient;
import at.beris.virtualfile.config.Configuration;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class FtpClientIntegrationTest {
    private static final int FTP_PORT = 2221;
    private static final String TEST_FILE = "test.txt";
    private static final String TEST_DIRECTORY = "testdirectory";
    private static final String TEST_STRING = "This is a test string";

    private static FtpServer ftpServer;
    private static FtpClient ftpClient;

    @BeforeClass
    public static void setUp() throws Exception {
        TestFileHelper.initIntegrationTest();

        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        UserManager userManager = userManagerFactory.createUserManager();
        BaseUser user = new BaseUser();
        user.setName("ftptest");
        user.setPassword("test123");
        user.setHomeDirectory("/tmp");

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());

        user.setAuthorities(authorities);
        userManager.save(user);

        FtpServerFactory serverFactory = new FtpServerFactory();
        serverFactory.setUserManager(userManager);
        ListenerFactory factory = new ListenerFactory();
// set the port of the listener
        factory.setPort(FTP_PORT);
        serverFactory.addListener("default", factory.createListener());
// start the server
        ftpServer = serverFactory.createServer();

        ftpServer.start();

        ftpClient = createFtpClient();
        ftpClient.connect();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        if (ftpClient.exists(TEST_FILE))
            ftpClient.deleteFile(TEST_FILE);
        if (ftpClient != null)
            ftpClient.disconnect();
        ftpServer.stop();
    }

    @Test
    public void createFile() throws IOException {
        ftpClient.createFile(TEST_FILE);
        Assert.assertTrue(ftpClient.exists(TEST_FILE));
        ftpClient.deleteFile(TEST_FILE);
        Assert.assertFalse(ftpClient.exists(TEST_FILE));
    }

    @Test
    public void makeDirectory() throws IOException {
        ftpClient.createDirectory(TEST_DIRECTORY);
        assertTrue(ftpClient.exists(TEST_DIRECTORY));
        ftpClient.deleteDirectory(TEST_DIRECTORY);
        assertFalse(ftpClient.exists(TEST_DIRECTORY));
    }

    @Test
    public void writeToFile() throws IOException {
        ftpClient.createFile(TEST_FILE);
        try (OutputStream outputstream = ftpClient.getOutputStream(TEST_FILE)) {
            outputstream.write(TEST_STRING.getBytes());
        }
        ftpClient.completePendingCommand();

        FileInfo fileInfo = ftpClient.getFileInfo(TEST_FILE);
        FileModel model = new FileModel();
        fileInfo.fillModel(model);

        assertNotNull(model.getLastModifiedTime());
        assertEquals(TEST_STRING.length(), model.getSize());
        ftpClient.deleteFile(TEST_FILE);
    }

    @Test
    public void readFromFile() throws IOException {
        ftpClient.createFile(TEST_FILE);
        try (OutputStream outputstream = ftpClient.getOutputStream(TEST_FILE)) {
            outputstream.write(TEST_STRING.getBytes());
        }
        ftpClient.completePendingCommand();

        byte[] bytesReadArray = new byte[TEST_STRING.length()];
        int bytesRead = 0;
        try (InputStream inputstream = ftpClient.getInputStream(TEST_FILE)) {
            bytesRead = inputstream.read(bytesReadArray);
        }
        ftpClient.completePendingCommand();

        assertEquals(TEST_STRING.length(), bytesRead);
        assertArrayEquals(TEST_STRING.getBytes(), bytesReadArray);
        ftpClient.deleteFile(TEST_FILE);
    }

    @Test
    public void list() throws IOException {
        List<FileInfo> fileInfoList = ftpClient.list("/");
        Assert.assertTrue(fileInfoList.size() > 0);
    }

    private static FtpClient createFtpClient() throws Exception {
        Configuration configuration = new Configuration();
        configuration.initValues();
//        URL url = new URL("ftp://gd.tuwien.ac.at");
        URL url = new URL("ftp://ftptest:test123@localhost:" + FTP_PORT);
        return new FtpClient(url, configuration);
    }


}