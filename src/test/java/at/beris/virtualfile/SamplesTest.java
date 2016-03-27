/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.value.AuthenticationType;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * This class contains real world code samples
 */
public class SamplesTest {
    @Test
    @Ignore
    public void addFilesToDirectory() {
        File dir = FileManager.newLocalDirectory("testdir");
        dir.create();
        File file = FileManager.newLocalFile("abc.txt");
        dir.add(file);
        file.create();
        dir.delete();
    }

    @Test
    public void extractArchive() {
        File archive = FileManager.newLocalFile("src" + java.io.File.separator + "test" + java.io.File.separator +
                "resources" + java.io.File.separator + "testarchive.zip");
        File directory = FileManager.newLocalDirectory("extracted");
        List<File> extractedFiles = archive.extract(directory);
        Assert.assertEquals(33, extractedFiles.size());
        directory.delete();
    }

    @Test
    public void listArchive() {
        File archive = FileManager.newLocalFile("src" + java.io.File.separator + "test" + java.io.File.separator +
                "resources" + java.io.File.separator + "testarchive.zip");
        Assert.assertEquals(33, archive.list().size());
    }

    @Test
    public void copyFileToDirectory() {
        File file = FileManager.newFile("sftp://sshtest:" + TestFileHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/dokuwiki-stable.tgz");
        file.copy(FileManager.newLocalFile("."));
        File copiedFile = FileManager.newLocalFile("dokuwiki-stable.tgz");
        Assert.assertArrayEquals(file.checksum(), copiedFile.checksum());
        copiedFile.delete();
    }

    @Test
    public void AuthWithPublicKey() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new java.io.File(TestFileHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
        FileManager.getConfig().setAuthenticationType(AuthenticationType.PUBLIC_KEY)
                .setPrivateKeyFile(TestFileHelper.TEST_CREDENTIALS_DIRECTORY + java.io.File.separator + "id_dsa");
        File file = FileManager.newFile("sftp://sshtest:" + TestFileHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/.ssh");
        assertTrue(file.isDirectory());
    }

    @Test
    public void AuthWithPasswordNoStrictHost() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new java.io.File(TestFileHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
        FileManager.getConfig().setStrictHostKeyChecking(false).setPassword(TestFileHelper.readSftpPassword());
        File file = FileManager.newFile("sftp://sshtest:@www.beris.at:22/home/sshtest/.ssh");
        assertTrue(file.isDirectory());
    }
}
