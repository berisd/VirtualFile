/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;

import static at.beris.virtualfile.TestFileHelper.initIntegrationTest;
import static at.beris.virtualfile.TestFileHelper.readSftpPassword;

public class SftpFileIntegrationTest extends AbstractFileTest {

    @BeforeClass
    public static void setUp() throws Exception {
        initIntegrationTest();
        FileManager.registerProtocolURLStreamHandlers();

        URL siteUrl = FileUtils.newUrl("sftp://sshtest:" + readSftpPassword() + "@www.beris.at:22" + TestFileHelper.SSH_HOME_DIRECTORY);
        sourceFileUrl = FileUtils.newUrl(siteUrl, TEST_SOURCE_FILE_NAME);
        targetFileUrl = FileUtils.newUrl(siteUrl, TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = FileUtils.newUrl(siteUrl, TEST_SOURCE_DIRECTORY_NAME + "/");
        targetDirectoryUrl = FileUtils.newUrl(siteUrl, TEST_TARGET_DIRECTORY_NAME + "/");
    }

    @AfterClass
    public static void tearDown() {
        AbstractFileTest.tearDown();
    }

    @Test
    public void createFile() {
        super.createFile();
    }

    @Test
    public void createDirectory() {
        super.createDirectory();
    }

    @Test
    public void copyFile() {
        super.copyFile();
    }

    @Test
    public void copyDirectory() {
        super.copyDirectory();
    }

    @Test
    public void deleteFile() {
        super.deleteFile();
    }

    @Test
    public void deleteDirectory() {
        super.deleteDirectory();
    }

    @Test
    public void getFileAttributes() {
        super.getFileAttributes();
    }
}