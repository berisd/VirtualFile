/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.AuthenticationType;
import at.beris.virtualfile.config.FileConfig;
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
        Directory dir = FileManager.newLocalDirectory("testdir");
        dir.create();
        File file = FileManager.newLocalFile("abc.txt");
        dir.add(file);
        file.create();
        dir.delete();
    }

    @Test
    public void extractArchive() {
        Archive archive = FileManager.newLocalFile("src" + java.io.File.separator + "test" + java.io.File.separator +
                "resources" + java.io.File.separator + "testarchive.zip").asArchive();
        Directory directory = FileManager.newLocalDirectory("extracted");
        List<File> extractedFiles = archive.extract(directory);
        Assert.assertEquals(33, extractedFiles.size());
        directory.delete();
    }

    @Test
    public void listArchive() {
        Archive archive = FileManager.newLocalArchive("src" + java.io.File.separator + "test" + java.io.File.separator +
                "resources" + java.io.File.separator + "testarchive.zip");
        Assert.assertEquals(33, archive.list().size());
    }

    @Test
    public void AuthWithPublicKey() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new java.io.File(TestFileHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
        FileConfig config = new FileConfig().setClientAuthenticationType(AuthenticationType.PUBLIC_KEY)
                .setPrivateKeyFile(TestFileHelper.TEST_CREDENTIALS_DIRECTORY + java.io.File.separator + "id_dsa");
        File file = FileManager.newFile("sftp://sshtest:" + TestFileHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/.ssh", config);
        assertTrue(file.isDirectory());
    }

    @Test
    public void AuthWithPasswordNoStrictHost() {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new java.io.File(TestFileHelper.TEST_CREDENTIALS_DIRECTORY).toPath()));
        FileConfig config = new FileConfig().setClientStrictHostKeyChecking(false);
        File file = FileManager.newFile("sftp://sshtest:" + TestFileHelper.readSftpPassword() + "@www.beris.at:22/home/sshtest/.ssh", config);
        assertTrue(file.isDirectory());
    }
}
