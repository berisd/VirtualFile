/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import at.beris.virtualfile.util.UrlUtils;
import at.beris.virtualfile.util.VoidOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashSet;
import java.util.Set;

import static at.beris.virtualfile.FileTestHelper.*;
import static org.junit.Assert.*;

public class SftpFileTest extends AbstractFileTest {

    @BeforeClass
    public static void beforeTest() throws Exception {
        initIntegrationTest();
    }

    @Before
    @Override
    public void beforeTestCase() throws Exception {
        super.beforeTestCase();
        URL siteUrl = UrlUtils.newUrl("sftp://sshtest:" + readSftpPassword() + "@www.beris.at:22" + FileTestHelper.SSH_HOME_DIRECTORY);
        sourceFileUrl = UrlUtils.newUrl(siteUrl, FileTestHelper.SSH_HOME_DIRECTORY + TEST_SOURCE_FILE_NAME);
        targetFileUrl = UrlUtils.newUrl(siteUrl, FileTestHelper.SSH_HOME_DIRECTORY + TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = UrlUtils.newUrl(siteUrl, FileTestHelper.SSH_HOME_DIRECTORY + TEST_SOURCE_DIRECTORY_NAME + "/");
        targetDirectoryUrl = UrlUtils.newUrl(siteUrl, FileTestHelper.SSH_HOME_DIRECTORY + TEST_TARGET_DIRECTORY_NAME + "/");
    }

    @After
    @Override
    public void afterTestCase() throws IOException {
        super.afterTestCase();
    }

    @Test
    public void createFile() throws IOException {
        super.createFile(new VoidOperation<VirtualFile>() {
            @Override
            public void execute(VirtualFile file) throws IOException {
                assertEquals(TEST_SOURCE_FILE_NAME, file.getName());
                assertTrue(FileTestHelper.isDateCloseToNow(file.getLastModifiedTime(), 10));
                assertTrue(FileTestHelper.isDateCloseToNow(file.getLastAccessTime(), 10));
                assertTrue(file.getOwner() instanceof UserPrincipal);
                assertTrue(file.getGroup() instanceof GroupPrincipal);
                assertTrue(file.getAttributes().size() > 0);
                assertEquals(0, file.getSize());
                assertFalse(file.isDirectory());
            }
        });
    }

    @Test
    public void createDirectory() throws IOException {
        super.createDirectory();
    }

    @Test
    public void copyFile() throws IOException {
        super.copyFile();
    }

    @Test
    public void copyDirectory() throws IOException {
        super.copyDirectory();
    }

    @Test
    public void deleteFile() throws IOException {
        super.deleteFile();
    }

    @Test
    public void deleteDirectory() throws IOException {
        super.deleteDirectory();
    }

    @Test
    public void getFileAttributes() throws IOException {
        super.getFileAttributes(new VoidOperation<VirtualFile>() {
            @Override
            public void execute(VirtualFile file) throws IOException {
                assertTrue(file.getAttributes().contains(PosixFilePermission.OWNER_READ));
                assertTrue(file.getAttributes().contains(PosixFilePermission.OWNER_WRITE));
                assertTrue(file.getAttributes().contains(PosixFilePermission.GROUP_READ));
                assertTrue(file.getAttributes().contains(PosixFilePermission.OTHERS_READ));
            }
        });
    }

    @Test
    public void setFileAttributes() throws IOException {
        Set<FileAttribute> attributes = new HashSet<>();
        attributes.add(PosixFilePermission.OTHERS_EXECUTE);
        attributes.add(PosixFilePermission.GROUP_EXECUTE);
        super.setFileAttributes(attributes);
    }

    @Test(expected = AccessDeniedException.class)
    public void setOwner() throws IOException {
        UnixUserPrincipal user = new UnixUserPrincipal(1002, 1002);
        super.setOwner(user);
    }

    @Test(expected = AccessDeniedException.class)
    public void setGroup() throws IOException {
        UnixGroupPrincipal group = new UnixGroupPrincipal(1002);
        super.setGroup(group);
    }

    @Override
    protected FileContext createFileContext() {
        return new FileContext();
    }
}