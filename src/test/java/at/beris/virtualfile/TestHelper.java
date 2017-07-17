/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

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

import static at.beris.virtualfile.provider.operation.CopyFileOperation.STREAM_BUFFER_SIZE;

public class TestHelper {
    public static final char[] TEST_MASTER_PASSWORD = new char[]{'t', 'e', 's', 't', 'p', 'w', 'd'};
    public static final String TEST_HOME_DIRECTORY = System.getProperty("user.home") + File.separator + ".VirtualFile" + File.separator + ".test";
    public static final String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public static final String TEST_TARGET_FILE_NAME = "targetfile1.txt";
    public static final Date TEST_SOURCE_FILE_LAST_MODIFIED = new Date();
    public static final int TEST_SOURCE_FILE_SIZE = STREAM_BUFFER_SIZE + 10;

    public static final String TEST_SOURCE_DIRECTORY_NAME = "testdirectory";
    public static final String TEST_TARGET_DIRECTORY_NAME = "targettestdirectory";

    public static final String SSH_HOME_DIRECTORY = "/home/sshtest/";

    public static final int NUMBER_OF_ARCHIVE_ENTRIES = 20;
    public static final String SEVEN_ZIP_FILENAME = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testarchive.7z";
    public static final String ZIP_FILENAME = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testarchive.zip";
    public static final String TAR_GZIP_FILENAME = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testarchive.tar.gz";

    public static void initIntegrationTest() {
        org.junit.Assume.assumeTrue("Ignore Integration Tests.", Boolean.parseBoolean(System.getProperty("runintegrationtests")));
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(TestHelper.TEST_HOME_DIRECTORY).toPath()));
    }

    public static String readSftpPassword() {
        List<String> stringList = null;
        try {
            stringList = Files.readAllLines(new File(TEST_HOME_DIRECTORY + File.separator + "sshlogin.txt").toPath(), Charset.defaultCharset());
        } catch (IOException e) {
            return "";
        }
        return stringList.get(0);
    }

    public static boolean isDateCloseToNow(FileTime fileTime, int seconds) {
        return isDateClose(new Date(fileTime.toMillis()), new Date(System.currentTimeMillis()), seconds);
    }

    public static boolean isDateClose(Date date, Date otherDate, int seconds) {
        long dateMillis = date.getTime();
        long otherMillis = otherDate.getTime();

        return (dateMillis > (otherMillis - seconds * 1000)) && (dateMillis < otherMillis);
    }

    public static VirtualFile createLocalSourceFile(UrlFileManager fileManager, URL url) {
        try {
            File file = new File(url.toURI());

            StringBuilder dataString = new StringBuilder("t");

            while (dataString.length() < TEST_SOURCE_FILE_SIZE)
                dataString.append("t");

            Files.write(file.toPath(), dataString.toString().getBytes());
            Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(TEST_SOURCE_FILE_LAST_MODIFIED.getTime()));

            return fileManager.resolveFile(file.toURI().toURL());
        } catch (IOException | URISyntaxException e) {
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

    public static List<VirtualFile> createFileTreeData(List<String> fileUrlList) throws IOException, URISyntaxException {
        String testString = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttest";
        StringBuilder dataString = new StringBuilder(testString);

        int index = 0;
        List<VirtualFile> fileList = new ArrayList<>();
        for (String fileUrl : fileUrlList) {
            File file = null;
            file = new File(new URL(fileUrl).toURI());
            if (fileUrl.indexOf('.') == -1) {
                // directory
                file.mkdirs();
            } else {
                // file
                if (file.getParentFile() != null)
                    file.getParentFile().mkdirs();

                index++;
                while (dataString.length() < STREAM_BUFFER_SIZE * index + 10)
                    dataString.append(testString);

                Files.write(file.toPath(), dataString.toString().getBytes());
            }
        }
        return fileList;
    }

    public static UrlFileManager createFileManager() {
        Configuration configuration = createConfiguration();
        KeyStoreManager keyStoreManager = KeyStoreManager.create(configuration);
        return new UrlFileManager(new UrlFileContext(configuration, SiteManager.create(configuration, keyStoreManager), keyStoreManager));
    }

    public static Configuration createConfiguration() {
        return Configuration.create(TEST_HOME_DIRECTORY).setMasterPassword(TEST_MASTER_PASSWORD);
    }

    public static SiteManager createSiteManager() {
        Configuration configuration = createConfiguration();
        return SiteManager.create(configuration, KeyStoreManager.create(configuration));
    }
}
