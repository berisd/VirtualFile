/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.VoidOperation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashSet;
import java.util.Set;

import static at.beris.virtualfile.TestFileHelper.initIntegrationTest;
import static at.beris.virtualfile.TestFileHelper.readSftpPassword;
import static org.junit.Assert.*;

public class SftpFileTest extends AbstractFileTest {

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
        super.createFile(new VoidOperation<File>() {
            @Override
            public void execute(File file) {
                assertEquals(TEST_SOURCE_FILE_NAME, file.getName());
                assertTrue(TestFileHelper.isDateCloseToNow(file.getLastModifiedTime(), 10));
                assertTrue(TestFileHelper.isDateCloseToNow(file.getLastAccessTime(), 10));
                assertTrue(file.getOwner() instanceof UserPrincipal);
                assertTrue(file.getGroup() instanceof GroupPrincipal);
                assertTrue(file.getAttributes().size() > 0);
                assertEquals(0, file.getSize());
                assertFalse(file.isDirectory());
            }
        });
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
        super.getFileAttributes(new VoidOperation<File>() {
            @Override
            public void execute(File file) {
                assertTrue(file.getAttributes().contains(PosixFilePermission.OWNER_READ));
                assertTrue(file.getAttributes().contains(PosixFilePermission.OWNER_WRITE));
                assertTrue(file.getAttributes().contains(PosixFilePermission.GROUP_READ));
                assertTrue(file.getAttributes().contains(PosixFilePermission.OTHERS_READ));
            }
        });
    }

    @Test
    public void setFileAttributes() {
        Set<FileAttribute> attributes = new HashSet<>();
        attributes.add(PosixFilePermission.OTHERS_EXECUTE);
        attributes.add(PosixFilePermission.GROUP_EXECUTE);
        super.setFileAttributes(attributes);
    }

    @Test(expected = at.beris.virtualfile.exception.AccessDeniedException.class)
    public void setOwner() {
        UnixUserPrincipal user = new UnixUserPrincipal(1002, 1002);
        super.setOwner(user);
    }

    @Test(expected = at.beris.virtualfile.exception.AccessDeniedException.class)
    public void setGroup() {
        UnixGroupPrincipal group = new UnixGroupPrincipal(1002);
        super.setGroup(group);
    }
}