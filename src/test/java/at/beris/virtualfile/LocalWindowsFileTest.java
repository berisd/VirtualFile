/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.BasicFilePermission;
import at.beris.virtualfile.attribute.DosFileAttribute;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.os.OsFamily;
import at.beris.virtualfile.util.OsUtils;
import at.beris.virtualfile.util.UrlUtils;
import at.beris.virtualfile.util.VoidOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocalWindowsFileTest extends AbstractFileTest {

    @BeforeClass
    public static void setUp() throws Exception {
        org.junit.Assume.assumeTrue("Host operating system isn't Windows. Skipping test..", OsUtils.detectOSFamily() == OsFamily.WINDOWS);

        sourceFileUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME);
        targetFileUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + java.io.File.separator);
        targetDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_DIRECTORY_NAME + java.io.File.separator);
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
                assertTrue(file.getAttributes().contains(BasicFilePermission.READ));
                assertTrue(file.getAttributes().contains(BasicFilePermission.WRITE));
            }
        });
    }

    @Test
    public void setFileAttributes() throws IOException {
        Set<FileAttribute> attributes = new HashSet<>();
        attributes.add(BasicFilePermission.EXECUTE);
        attributes.add(DosFileAttribute.HIDDEN);
        File file = fileContext.newFile(sourceFileUrl);
        file.create();
        file.setAttributes(attributes.toArray(new FileAttribute[0]));
        fileContext.dispose(file);

        file = fileContext.newFile(sourceFileUrl);
        Set<FileAttribute> actualAttributes = file.getAttributes();
        assertTrue(actualAttributes.containsAll(attributes));
        file.delete();
        fileContext.dispose(file);
    }

    @Test
    public void setOwner() throws IOException {
        UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
        UserPrincipal userPrincipal = lookupService.lookupPrincipalByName(System.getProperty("user.name"));
        super.setOwner(userPrincipal);
    }

    @Test
    public void setAcl() throws IOException {
        File file = fileContext.newFile(sourceFileUrl);
        file.create();
        List<AclEntry> acl = file.getAcl();
        List<AclEntry> newAcl = new ArrayList<>(acl);
        newAcl.remove(0);
        file.setAcl(newAcl);
        fileContext.dispose(file);

        file = fileContext.newFile(sourceFileUrl);
        assertEquals(newAcl.size(), file.getAcl().size());
        file.delete();
    }

    @Test
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