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
import at.beris.virtualfile.filter.TestFilterHelper;
import at.beris.virtualfile.os.OsFamily;
import at.beris.virtualfile.util.OsUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static at.beris.virtualfile.FileTestHelper.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocalUnixFileTest extends AbstractUrlFileTest {

    @BeforeClass
    public static void beforeTest() throws Exception {
        org.junit.Assume.assumeTrue("Host operating system isn't Unix. Skipping test..", OsUtils.detectOSFamily() != OsFamily.WINDOWS);
    }

    @Before
    @Override
    public void beforeTestCase() {
        super.beforeTestCase();
        sourceFileUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME);
        targetFileUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + "/");
        targetDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_DIRECTORY_NAME + "/");
    }

    @Test
    public void createDirectory() {
        super.createDirectory();
    }

    @Test
    public void createSymbolicLink() throws IOException {
        super.createSymbolicLink();
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
    public void compareFileEqual() throws IOException {
        String msg = "jhdfsjhusdfuijfdsuidfsuidfsuifsduijsdfujifsdunfdsunsdfnufsdnusdfundsfnusdfnufnudf";
        Files.write(Paths.get(TEST_SOURCE_FILE_NAME), msg.getBytes());
        Files.write(Paths.get(TEST_TARGET_FILE_NAME), msg.getBytes());
        VirtualFile sourceFile = fileManager.resolveFile(sourceFileUrl);
        VirtualFile targetFile = fileManager.resolveFile(targetFileUrl);
        assertTrue(sourceFile.compare(targetFile));
    }

    @Test
    public void compareFileNotEqual() throws IOException {
        String msg = "jhdfsjhusdfuijfdsuidfsuidfsuifsduijsdfujifsdunfdsunsdfnufsdnusdfundsfnusdfnufnudf";
        Files.write(Paths.get(TEST_SOURCE_FILE_NAME), msg.getBytes());
        msg = "xyzfsjhusdfuijfdsuidfsuidfsuifxyzijsdfujifsdunfdsunsdfnufsdnusdfundsfnusdfnufnudf";
        Files.write(Paths.get(TEST_TARGET_FILE_NAME), msg.getBytes());
        VirtualFile sourceFile = fileManager.resolveFile(sourceFileUrl);
        VirtualFile targetFile = fileManager.resolveFile(targetFileUrl);
        assertFalse(sourceFile.compare(targetFile));
    }

    @Test
    public void compareDirectoryEqual() throws IOException {
        List<VirtualFile> sourceFileList = null;
        List<VirtualFile> targetFileList = null;
        try {
            sourceFileList = TestFilterHelper.createFiles(fileManager, TEST_SOURCE_DIRECTORY_NAME + "/");
            targetFileList = TestFilterHelper.createFiles(fileManager, TEST_TARGET_DIRECTORY_NAME + "/");

            String msg = "jhdfsjhusdfuijfdsuidfsuidfsuifsduijsdfujifsdunfdsunsdfnufsdnusdfundsfnusdfnufnudf";
            Files.write(sourceFileList.get(1).asFile().toPath(), msg.getBytes());
            Files.write(targetFileList.get(1).asFile().toPath(), msg.getBytes());
            Files.write(sourceFileList.get(2).asFile().toPath(), msg.getBytes());
            Files.write(targetFileList.get(2).asFile().toPath(), msg.getBytes());
            assertTrue(sourceFileList.get(0).compare(targetFileList.get(0)));
        } finally {
            createFileManager().resolveFile(UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + "/")).delete();
            createFileManager().resolveFile(UrlUtils.getUrlForLocalPath(TEST_TARGET_DIRECTORY_NAME + "/")).delete();
        }
    }

    @Test
    public void compareDirectoryNotEqual() throws IOException {
        List<VirtualFile> sourceFileList = null;
        List<VirtualFile> targetFileList = null;
        try {
            sourceFileList = TestFilterHelper.createFiles(fileManager, TEST_SOURCE_DIRECTORY_NAME + "/");
            targetFileList = TestFilterHelper.createFiles(fileManager, TEST_TARGET_DIRECTORY_NAME + "/");

            String msg = "jhdfsjhusdfuijfdsuidfsuidfsuifsduijsdfujifsdunfdsunsdfnufsdnusdfundsfnusdfnufnudf";
            Files.write(sourceFileList.get(1).asFile().toPath(), msg.getBytes());
            Files.write(targetFileList.get(1).asFile().toPath(), msg.getBytes());
            Files.write(sourceFileList.get(2).asFile().toPath(), msg.getBytes());
            msg = "xyzfsjhusdfuijfdsuidfsuidfsuifsduijsdfujifsdunfdsunsdfnufsdnusdfundsfnusdfnufnudf";
            Files.write(targetFileList.get(2).asFile().toPath(), msg.getBytes());
            assertFalse(sourceFileList.get(0).compare(targetFileList.get(0)));
        } finally {
            createFileManager().resolveFile(UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + "/")).delete();
            createFileManager().resolveFile(UrlUtils.getUrlForLocalPath(TEST_TARGET_DIRECTORY_NAME + "/")).delete();
        }
    }

    @Test
    public void rename() {
        VirtualFile sourceFile = fileManager.resolveFile(UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME));
        VirtualFile targetFile = fileManager.resolveFile(UrlUtils.getUrlForLocalPath(TEST_TARGET_FILE_NAME));
        sourceFile.create();
        assertTrue(sourceFile.exists());
        assertFalse(targetFile.exists());
        sourceFile.rename(TEST_TARGET_FILE_NAME);
        assertFalse(sourceFile.exists());
        assertTrue(targetFile.exists());
    }

    @Test
    public void move() {
        VirtualFile sourceFile = fileManager.resolveFile(UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME));
        VirtualFile targetFile = fileManager.resolveFile(UrlUtils.getUrlForLocalPath(TEST_TARGET_FILE_NAME));
        sourceFile.create();
        assertTrue(sourceFile.exists());
        assertFalse(targetFile.exists());
        sourceFile.move(targetFile);
        assertFalse(sourceFile.exists());
        assertTrue(targetFile.exists());
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
            assertTrue(file.getAttributes().contains(PosixFilePermission.OWNER_READ));
            assertTrue(file.getAttributes().contains(PosixFilePermission.OWNER_WRITE));
            assertTrue(file.getAttributes().contains(PosixFilePermission.GROUP_READ));
            assertTrue(file.getAttributes().contains(PosixFilePermission.OTHERS_READ));
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
    @Ignore("IgnoreTest: On Linux creationTime doesn't seem to be set")
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
    protected VirtualFileManager createFileManager() {
        return new VirtualFileManager();
    }
}