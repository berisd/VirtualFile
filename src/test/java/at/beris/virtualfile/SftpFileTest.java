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
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static at.beris.virtualfile.FileTestHelper.*;
import static org.junit.Assert.*;

public class SftpFileTest extends AbstractUrlFileTest {

    @BeforeClass
    public static void beforeTest() throws Exception {
        initIntegrationTest();
    }

    @Before
    @Override
    public void beforeTestCase() {
        super.beforeTestCase();
        URL siteUrl = UrlUtils.newUrl("sftp://sshtest:" + readSftpPassword() + "@www.beris.at:22" + FileTestHelper.SSH_HOME_DIRECTORY);
        sourceFileUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME);
        targetFileUrl = UrlUtils.newUrl(siteUrl, FileTestHelper.SSH_HOME_DIRECTORY + TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + "/");
        targetDirectoryUrl = UrlUtils.newUrl(siteUrl, FileTestHelper.SSH_HOME_DIRECTORY + TEST_TARGET_DIRECTORY_NAME + "/");
    }

    @After
    @Override
    public void afterTestCase() {
        super.afterTestCase();
    }

    @Test
    @Override
    public void createFile() {
        super.createFile(Optional.of(file -> {
            assertEquals(TEST_SOURCE_FILE_NAME, file.getName());
            assertTrue(FileTestHelper.isDateCloseToNow(file.getLastModifiedTime(), 10));
            assertTrue(FileTestHelper.isDateCloseToNow(file.getLastAccessTime(), 10));
            assertTrue(file.getOwner() instanceof UserPrincipal);
            assertTrue(file.getGroup() instanceof GroupPrincipal);
            assertTrue(file.getAttributes().size() > 0);
            assertEquals(0, file.getSize());
            assertFalse(file.isDirectory());
        }));
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
    public void copyDirectory() throws IOException, URISyntaxException {
        super.copyDirectory();
    }

    @Test
    public void deleteFile() {
        super.deleteFile();
    }

    @Test
    public void deleteDirectory() throws IOException, URISyntaxException {
        super.deleteDirectory();
    }

    @Test
    public void getFileAttributes() {
        super.getFileAttributes(new Consumer<VirtualFile>() {
            @Override
            public void accept(VirtualFile file) {
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

    @Test
    public void setOwner() {
        UnixUserPrincipal user = new UnixUserPrincipal(1002, 1002);
        super.setOwner(user);
    }

    @Test
    public void setGroup() {
        UnixGroupPrincipal group = new UnixGroupPrincipal(1002);
        super.setGroup(group);
    }

    @Override
    protected FileManager createFileManager() {
        return new FileManager();
    }
}