/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static at.beris.virtualfile.provider.operation.CopyOperation.COPY_BUFFER_SIZE;

public class TestFileHelper {
    public static final String HOME_DIRECTORY = System.getProperty("user.home") + File.separator + ".VirtualFile";
    public static final String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public static final String TEST_TARGET_FILE_NAME = "targetfile1.txt";
    public static final Date TEST_SOURCE_FILE_LAST_MODIFIED = new Date();
    public static final int TEST_SOURCE_FILE_SIZE = COPY_BUFFER_SIZE + 10;

    public static final String TEST_SOURCE_DIRECTORY_NAME = "testdirectory/";
    public static final String TEST_TARGET_DIRECTORY_NAME = "targettestdirectory/";

    public static final String TEST_CREDENTIALS_DIRECTORY = HOME_DIRECTORY + File.separator + "test" + File.separator;

    public static final String SSH_HOME_DIRECTORY = "/home/sshtest/";

    public static void initIntegrationTest() throws Exception {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(TestFileHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
    }

    public static String readSftpPassword() {
        List<String> stringList = null;
        try {
            stringList = Files.readAllLines(new File(TEST_CREDENTIALS_DIRECTORY + File.separator + "sshlogin.txt").toPath(), Charset.defaultCharset());
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
        return isDateClose(new Date(fileTime.toMillis()), new Date(System.currentTimeMillis()), seconds);
    }

    public static boolean isDateClose(Date date, Date otherDate, int seconds) {
        long dateMillis = date.getTime();
        long otherMillis = otherDate.getTime();

        return (dateMillis > (otherMillis - seconds * 1000)) && (dateMillis < otherMillis);
    }

    public static VirtualFile createLocalSourceFile(URL url) {
        try {
            File file = new File(url.toURI());

            StringBuilder dataString = new StringBuilder("t");

            while (dataString.length() < TEST_SOURCE_FILE_SIZE)
                dataString.append("t");

            Files.write(file.toPath(), dataString.toString().getBytes());
            Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(TEST_SOURCE_FILE_LAST_MODIFIED.getTime()));

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

    public static List<VirtualFile> createFileTreeData(List<String> fileUrlList) throws IOException {
        String testString = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttest";
        StringBuilder dataString = new StringBuilder(testString);

        int index = 0;
        List<VirtualFile> fileList = new ArrayList<>();
        for (String fileUrl : fileUrlList) {
            File file = null;
            try {
                file = new File(new URL(fileUrl).toURI());
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
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
