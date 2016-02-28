/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.BasicFilePermission;
import at.beris.virtualfile.attribute.DosFileAttribute;
import at.beris.virtualfile.attribute.IAttribute;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.OsUtils;
import at.beris.virtualfile.util.VoidOperation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class LocalWindowsFileTest extends AbstractFileTest {

    @BeforeClass
    public static void setUp() throws Exception {
        org.junit.Assume.assumeTrue("Host operating system isn't Windows. Skipping test..", OsUtils.isWindows());

        sourceFileUrl = FileUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME);
        targetFileUrl = FileUtils.getUrlForLocalPath(TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = FileUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + "/");
        targetDirectoryUrl = FileUtils.getUrlForLocalPath(TEST_TARGET_DIRECTORY_NAME + "/");
    }

    @AfterClass
    public static void tearDown() {
        AbstractFileTest.tearDown();
    }

    @Test
    public void createFile() {
        super.createFile();
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
        super.getFileAttributes(new VoidOperation<IFile>() {
            @Override
            public void execute(IFile file) {
                assertTrue(file.getAttributes().contains(BasicFilePermission.READ));
                assertTrue(file.getAttributes().contains(BasicFilePermission.WRITE));
            }
        });
    }

    @Test
    public void setFileAttributes() {
        Set<IAttribute> attributes = new HashSet<>();
        attributes.add(BasicFilePermission.EXECUTE);
        attributes.add(DosFileAttribute.HIDDEN);
        super.setFileAttributes(attributes);
    }

    @Test
    public void setOwner() throws IOException {
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(System.getProperty("user.name"));
        super.setOwner(userPrincipal);
    }

    @Test
    public void setGroup() {
        super.setGroup();
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
}