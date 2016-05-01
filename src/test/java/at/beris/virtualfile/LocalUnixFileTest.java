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
import at.beris.virtualfile.util.OsUtils;
import at.beris.virtualfile.util.UrlUtils;
import at.beris.virtualfile.util.VoidOperation;
import org.junit.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class LocalUnixFileTest extends AbstractFileTest {

    @BeforeClass
    public static void setUp() throws Exception {
        org.junit.Assume.assumeTrue("Host operating system isn't Unix. Skipping test..", !OsUtils.isWindows());

        sourceFileUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME);
        targetFileUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + "/");
        targetDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_DIRECTORY_NAME + "/");
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
    }

    @After
    public void afterTest() throws IOException {
        super.afterTest();
    }

    @Test
    public void createFile() throws IOException {
        super.createFile();
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
        super.getFileAttributes(new VoidOperation<File>() {
            @Override
            public void execute(File file) throws IOException {
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

    @Test
    public void setOwner() throws IOException {
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(System.getProperty("user.name"));
        super.setOwner(userPrincipal);
    }

    @Test
    public void setGroup() throws IOException {
        super.setGroup();
    }

    @Test
    @Ignore("IgnoreTest: On Linux creationTime doesn't seem to be set")
    public void setCreationTime() throws IOException {
        super.setCreationTime();
    }

    @Test
    public void setLastModifiedTime() throws IOException {
        super.setLastModifiedTime();
    }

    @Test
    public void setLastAccessTime() throws IOException {
        super.setLastAccessTime();
    }
}