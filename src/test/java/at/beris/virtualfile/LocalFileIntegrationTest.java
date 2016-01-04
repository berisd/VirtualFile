/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class LocalFileIntegrationTest extends AbstractFileTest {

    @BeforeClass
    public static void setUp() throws Exception {
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
}