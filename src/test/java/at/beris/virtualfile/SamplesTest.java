/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.value.AuthenticationType;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static at.beris.virtualfile.FileTestHelper.*;
import static org.junit.Assert.assertTrue;

/**
 * This class contains real world code samples
 */
public class SamplesTest {
    private FileManager fileManager;

    @BeforeClass
    public static void beforeTestCase() {
        FileTestHelper.initIntegrationTest();
    }

    @Before
    public void beforeTest() {
        fileManager = new FileManager();
    }

    @After
    public void afterTestCase() {
        fileManager.dispose();
        VirtualFile[] resources = {fileManager.newLocalDirectory("extracted"), fileManager.newLocalFile("file.xml")};
        for (VirtualFile resource : resources) {
            if (resource.exists())
                resource.delete();
        }
    }

    @Test
    public void extractZipArchive() {
        VirtualArchive archive = fileManager.newLocalFile(ZIP_FILENAME).asArchive();
        VirtualFile directory = fileManager.newLocalDirectory("extracted");
        List<VirtualFile> extractedFiles = archive.extract(directory);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, extractedFiles.size());
    }

    @Test
    public void extractSevenZipArchive() {
        VirtualArchive archive = fileManager.newLocalArchive(SEVEN_ZIP_FILENAME);
        VirtualFile directory = fileManager.newLocalDirectory("extracted");
        List<VirtualFile> extractedFiles = archive.extract(directory);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, extractedFiles.size());
    }

    @Test
    @Ignore
    public void extractFile() {
        VirtualArchive archive = fileManager.newLocalFile(ZIP_FILENAME + "/TreeDb/file.xml").asArchive();
        VirtualFile targetFile = fileManager.newLocalFile("file.xml");
        List<VirtualFile> extractedFiles = archive.extract(targetFile);
        Assert.assertEquals(920, extractedFiles.get(0).getSize());
    }

    @Test
    public void listArchive() {
        VirtualArchive archive = fileManager.newLocalArchive(ZIP_FILENAME);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, archive.list().size());
    }

    @Test
    public void copyFileToDirectory() {
        VirtualFile file = fileManager.newFile("sftp://sshtest:" + FileTestHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/dokuwiki-stable.tgz");
        Integer filesCopied = file.copy(fileManager.newLocalFile("."));
        Assert.assertEquals(Integer.valueOf(1), filesCopied);
        VirtualFile copiedFile = fileManager.newLocalFile("dokuwiki-stable.tgz");
        Assert.assertArrayEquals(file.checksum(), copiedFile.checksum());
        copiedFile.delete();
    }

    @Test
    public void AuthWithPublicKey() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(FileTestHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
        fileManager.getConfiguration().setAuthenticationType(AuthenticationType.PUBLIC_KEY)
                .setPrivateKeyFile(FileTestHelper.TEST_CREDENTIALS_DIRECTORY + File.separator + "id_dsa");
        VirtualFile file = fileManager.newFile("sftp://sshtest:" + FileTestHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/.ssh");
        assertTrue(file.isDirectory());
    }

    @Test
    public void AuthWithPasswordNoStrictHost() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(FileTestHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
        VirtualFile file = fileManager.newFile("sftp://sshtest:@www.beris.at:22/home/sshtest/.ssh");
        fileManager.getConfiguration(file).setStrictHostKeyChecking(false).setPassword(FileTestHelper.readSftpPassword());
        assertTrue(file.isDirectory());
    }

    @Test
    @Ignore
    public void ftpListFiles() {
        VirtualFile file = fileManager.newFile("ftp://gd.tuwien.ac.at/");
        assertTrue(file.list().size() > 0);
    }
}
