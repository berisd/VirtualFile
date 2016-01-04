/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.SftpClient;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;

import static at.beris.virtualfile.operation.CopyOperation.COPY_BUFFER_SIZE;

public class TestFileHelper {
    public static final String HOME_DIRECTORY = System.getProperty("user.home") + java.io.File.separator + ".VirtualFile";
    public static final String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public static final String TEST_TARGET_FILE_NAME = "targetfile1.txt";
    public static final Instant TEST_SOURCE_FILE_LAST_MODIFIED = Instant.now();
    public static final int TEST_SOURCE_FILE_SIZE = COPY_BUFFER_SIZE + 10;

    public static final String TEST_SOURCE_DIRECTORY_NAME = "testdirectory/";
    public static final String TEST_TARGET_DIRECTORY_NAME = "targettestdirectory/";

    public static final String TEST_CREDENTIALS_DIRECTORY = HOME_DIRECTORY + java.io.File.separator + "test";

    public static final String SSH_HOME_DIRECTORY = "/home/sshtest/";

    public static void initTest() throws Exception {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new java.io.File(TEST_CREDENTIALS_DIRECTORY).toPath()));
    }

    public static String readSftpPassword() throws IOException {
        List<String> stringList = Files.readAllLines(new java.io.File(TEST_CREDENTIALS_DIRECTORY + java.io.File.separator + "sshlogin.txt").toPath());
        return stringList.get(0);
    }

    public static SftpClient createSftpClient(URL url) throws IOException {
        SftpClient sftpClient = new SftpClient();
        sftpClient.setHost(url.getHost());
        sftpClient.setPort(url.getPort());
        sftpClient.setUsername(url.getUserInfo().split(":")[0]);
        sftpClient.setPassword(readSftpPassword());

        sftpClient.init();
        sftpClient.connect();

        return sftpClient;
    }
}
