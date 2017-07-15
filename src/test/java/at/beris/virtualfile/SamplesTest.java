/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.sftp.AuthenticationType;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static at.beris.virtualfile.TestHelper.*;
import static org.junit.Assert.assertTrue;

/**
 * This class contains real world code samples
 */
public class SamplesTest {

    @BeforeClass
    public static void beforeTestCase() {
        TestHelper.initIntegrationTest();
    }

    @After
    public void afterTestCase() {
        VirtualFileManager fileManager = TestHelper.createFileManager();
        VirtualFile[] resources = {fileManager.resolveLocalDirectory("extracted"), fileManager.resolveLocalFile("file.xml")};
        for (VirtualFile resource : resources) {
            if (resource.exists())
                resource.delete();
        }
    }

    @Test
    public void extractZipArchive() {
        VirtualFileManager fileManager = TestHelper.createFileManager();
        VirtualArchive archive = fileManager.resolveLocalFile(ZIP_FILENAME).asArchive();
        VirtualFile directory = fileManager.resolveLocalDirectory("extracted");
        List<VirtualFile> extractedFiles = archive.extract(directory);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, extractedFiles.size());
    }

    @Test
    public void extractSevenZipArchive() {
        VirtualFileManager fileManager = TestHelper.createFileManager();
        VirtualArchive archive = fileManager.resolveLocalArchive(SEVEN_ZIP_FILENAME);
        VirtualFile directory = fileManager.resolveLocalDirectory("extracted");
        List<VirtualFile> extractedFiles = archive.extract(directory);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, extractedFiles.size());
    }

    @Test
    @Ignore
    public void extractFile() {
        VirtualFileManager fileManager = TestHelper.createFileManager();
        VirtualArchive archive = fileManager.resolveLocalFile(ZIP_FILENAME + "/TreeDb/file.xml").asArchive();
        VirtualFile targetFile = fileManager.resolveLocalFile("file.xml");
        List<VirtualFile> extractedFiles = archive.extract(targetFile);
        Assert.assertEquals(920, extractedFiles.get(0).getSize());
    }

    @Test
    public void listArchive() {
        VirtualFileManager fileManager = TestHelper.createFileManager();
        VirtualArchive archive = fileManager.resolveLocalArchive(ZIP_FILENAME);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, archive.list().size());
    }

    @Test
    public void copyFileToDirectory() {
        VirtualFileManager fileManager = TestHelper.createFileManager();
        VirtualFile file = fileManager.resolveFile("sftp://sshtest:" + TestHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/dokuwiki-stable.tgz");
        Integer filesCopied = file.copy(fileManager.resolveLocalFile("."));
        Assert.assertEquals(Integer.valueOf(1), filesCopied);
        VirtualFile copiedFile = fileManager.resolveLocalFile("dokuwiki-stable.tgz");
        Assert.assertArrayEquals(file.checksum(), copiedFile.checksum());
        copiedFile.delete();
    }

    @Test
    public void AuthWithPublicKey() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(TestHelper.TEST_HOME_DIRECTORY).toPath()));

        VirtualFileManager fileManager = VirtualFileManager.createManager(Configuration.create(TEST_HOME_DIRECTORY)
                .setAuthenticationType(AuthenticationType.PUBLIC_KEY)
                .setPrivateKeyFile(TestHelper.TEST_HOME_DIRECTORY + File.separator + "id_dsa")
        );


        VirtualFile file = fileManager.resolveFile("sftp://sshtest:" + TestHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/.ssh");
        assertTrue(file.isDirectory());
    }

    @Test
    public void AuthWithPasswordNoStrictHost() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(TestHelper.TEST_HOME_DIRECTORY).toPath()));

        VirtualFileManager fileManager = VirtualFileManager.createManager();
        fileManager.getClientDefaultConfigurationSftp().setStrictHostKeyChecking(false).setPassword(TestHelper.readSftpPassword().toCharArray());
        VirtualFile file = fileManager.resolveFile("sftp://sshtest:@www.beris.at:22/home/sshtest/.ssh");

        assertTrue(file.isDirectory());
    }

    @Test
    @Ignore
    public void ftpListFiles() {
        VirtualFileManager fileManager = VirtualFileManager.createManager();
        VirtualFile file = fileManager.resolveFile("ftp://gd.tuwien.ac.at/");
        assertTrue(file.list().size() > 0);
    }

    @Test
    public void getContentAndEncoding() {
        VirtualFileManager fileManager = VirtualFileManager.createManager();
        VirtualFile file = fileManager.resolveLocalFile(ZIP_FILENAME);
        Assert.assertEquals("windows-1251", file.getContentEncoding());
        Assert.assertEquals("application/zip", file.getContentType().toString());
    }
}
