/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.config.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    public static final String TEST_CREDENTIALS_DIRECTORY = HOME_DIRECTORY + java.io.File.separator + "test" + java.io.File.separator;

    public static final String SSH_HOME_DIRECTORY = "/home/sshtest/";

    public static void initIntegrationTest() throws Exception {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new java.io.File(TestFileHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
    }

    public static String readSftpPassword() {
        List<String> stringList = null;
        try {
            stringList = Files.readAllLines(new java.io.File(TEST_CREDENTIALS_DIRECTORY + java.io.File.separator + "sshlogin.txt").toPath());
        } catch (IOException e) {
            return "";
        }
        return stringList.get(0);
    }

    public static SftpClient createSftpClient(URL url) throws IOException {
        SftpClient sftpClient = new SftpClient(url, new Configuration());
        sftpClient.connect();

        return sftpClient;
    }

    public static boolean isDateCloseToNow(FileTime fileTime, int seconds) {
        long nowMillis = System.currentTimeMillis();
        long dateMillis = fileTime.toMillis();

        return (dateMillis > (nowMillis - seconds * 1000)) && (dateMillis < nowMillis);
    }

    public static boolean isInstantClose(Instant instant, Instant otherInstant, int seconds) {
        Instant instantPlusSeconds = instant.plus(seconds, ChronoUnit.SECONDS);
        return otherInstant.compareTo(instantPlusSeconds) < 0;
    }

    public static File createLocalSourceFile(URL url) {
        try {
            java.io.File file = new java.io.File(url.toURI());

            StringBuilder dataString = new StringBuilder("t");

            while (dataString.length() < TEST_SOURCE_FILE_SIZE)
                dataString.append("t");

            Files.write(file.toPath(), dataString.toString().getBytes());
            Files.setLastModifiedTime(file.toPath(), FileTime.from(TEST_SOURCE_FILE_LAST_MODIFIED));

            return FileManager.newFile(file.toURI().toURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> createFilenamesTree(String rootUrl) {
        List<String> fileList = new ArrayList<>();
        fileList.add(rootUrl);
        fileList.add(rootUrl + "testfile1.txt");
        fileList.add(rootUrl + "testfile2.txt");
        fileList.add(rootUrl + "subdir/");
        fileList.add(rootUrl + "subdir/testfile3.txt");
        fileList.add(rootUrl + "subdir/testfile4.txt");

        return fileList;
    }

    public static List<File> createFileTreeData(List<String> fileUrlList) throws IOException {
        String testString = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttest";
        StringBuilder dataString = new StringBuilder(testString);

        int index = 0;
        List<File> fileList = new ArrayList<>();
        for (String fileUrl : fileUrlList) {
            java.io.File file = new java.io.File(new URL(fileUrl).getPath());
            if (fileUrl.indexOf('.') == -1) {
                // directory
                file.mkdirs();
            } else {
                // file
                if (file.getParentFile() != null)
                    file.getParentFile().mkdirs();

                index++;
                while (dataString.length() < COPY_BUFFER_SIZE * index + 10)
                    dataString.append(testString);

                Files.write(file.toPath(), dataString.toString().getBytes());
            }
        }
        return fileList;
    }
}
