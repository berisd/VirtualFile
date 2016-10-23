/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.BasicFilePermission;
import at.beris.virtualfile.attribute.DosFileAttribute;
import at.beris.virtualfile.os.OsFamily;
import at.beris.virtualfile.util.OsUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Arrays;
import java.util.HashSet;

import static at.beris.virtualfile.FileTestHelper.*;
import static org.junit.Assert.assertTrue;

public class LocalWindowsFileTest extends AbstractUrlFileTest {

    @BeforeClass
    public static void beforeTest() throws Exception {
        org.junit.Assume.assumeTrue("Host operating system isn't Windows. Skipping test..", OsUtils.detectOSFamily() == OsFamily.WINDOWS);
    }

    @Before
    @Override
    public void beforeTestCase() throws Exception {
        super.beforeTestCase();
        sourceFileUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME);
        targetFileUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + java.io.File.separator);
        targetDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_DIRECTORY_NAME + java.io.File.separator);
    }

    @After
    @Override
    public void afterTestCase() {
        super.afterTestCase();
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
        super.getFileAttributes(file -> {
            assertTrue(file.getAttributes().contains(BasicFilePermission.READ));
            assertTrue(file.getAttributes().contains(BasicFilePermission.WRITE));
        });
    }

    @Test
    public void setFileAttributes() {
        super.setFileAttributes(new HashSet(Arrays.asList(BasicFilePermission.EXECUTE, DosFileAttribute.HIDDEN)));
    }

    @Test
    public void setOwner() throws IOException {
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(System.getProperty("user.name"));
        super.setOwner(userPrincipal);
    }

    @Test
    public void setAcl() {
        super.setAcl();
    }

    @Test
    public void setCreationTime() {
        super.setCreationTime();
    }

    @Test
    public void setLastModifiedTime() {
        super.setLastModifiedTime();
    }

    @Test
    public void setLastAccessTime() {
        super.setLastAccessTime();
    }

    @Override
    protected UrlFileContext createFileContext() {
        return new UrlFileContext();
    }
}