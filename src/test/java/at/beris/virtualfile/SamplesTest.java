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
import at.beris.virtualfile.exception.OperationNotSupportedException;
import org.junit.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static at.beris.virtualfile.FileTestHelper.NUMBER_OF_ARCHIVE_ENTRIES;
import static at.beris.virtualfile.FileTestHelper.ZIP_FILENAME;
import static org.junit.Assert.assertTrue;

/**
 * This class contains real world code samples
 */
public class SamplesTest {
    @BeforeClass
    public static void beforeTest() {
        FileTestHelper.initIntegrationTest();
    }

    @After
    public void afterTestCase() {
        VirtualFile[] resources = {FileManager.newLocalDirectory("extracted"), FileManager.newLocalFile("file.xml")};
        for (VirtualFile resource : resources) {
            if (resource.exists())
                resource.delete();
        }
    }

    @Test
    public void extractArchive() {
        Archive archive = FileManager.newLocalFile(ZIP_FILENAME).asArchive();
        VirtualFile directory = FileManager.newLocalDirectory("extracted");
        List<VirtualFile> extractedFiles = archive.extract(directory);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, extractedFiles.size());
    }

    @Test
    @Ignore
    public void extractFile() {
        Archive archive = FileManager.newLocalFile(ZIP_FILENAME + "/TreeDb/file.xml").asArchive();
        VirtualFile targetFile = FileManager.newLocalFile("file.xml");
        List<VirtualFile> extractedFiles = archive.extract(targetFile);
        Assert.assertEquals(920, extractedFiles.get(0).getSize());
    }

    @Test
    public void listArchive() {
        //TODO Must use VirtualArchive
        VirtualFile archive = FileManager.newLocalFile(ZIP_FILENAME);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, archive.list().size());
    }

    @Test
    public void copyFileToDirectory() {
        VirtualFile file = FileManager.newFile("sftp://sshtest:" + FileTestHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/dokuwiki-stable.tgz");
        file.copy(FileManager.newLocalFile("."));
        VirtualFile copiedFile = FileManager.newLocalFile("dokuwiki-stable.tgz");
        Assert.assertArrayEquals(file.checksum(), copiedFile.checksum());
        copiedFile.delete();
    }

    @Test
    public void AuthWithPublicKey() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(FileTestHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
        FileManager.getConfiguration().setAuthenticationType(AuthenticationType.PUBLIC_KEY)
                .setPrivateKeyFile(FileTestHelper.TEST_CREDENTIALS_DIRECTORY + File.separator + "id_dsa");
        VirtualFile file = FileManager.newFile("sftp://sshtest:" + FileTestHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/.ssh");
        assertTrue(file.isDirectory());
    }

    @Test
    public void AuthWithPasswordNoStrictHost() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(FileTestHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
        VirtualFile file = FileManager.newFile("sftp://sshtest:@www.beris.at:22/home/sshtest/.ssh");
        FileManager.getConfiguration(file).setStrictHostKeyChecking(false).setPassword(FileTestHelper.readSftpPassword());
        assertTrue(file.isDirectory());
    }

    @Test
    @Ignore
    public void ftpListFiles() {
        VirtualFile file = FileManager.newFile("ftp://gd.tuwien.ac.at/");
        assertTrue(file.list().size() > 0);
    }
}
