/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CollectionFilterTest {
    private static final String TEST_DIRECTORY = "testdir/";
    private static VirtualFile testDirectory;
    private static FileManager fileManager;

    @BeforeClass
    public static void setUp() throws Exception {
        fileManager = new FileManager();
        TestFilterHelper.createFiles(fileManager, TEST_DIRECTORY);
        testDirectory = fileManager.newLocalFile(TEST_DIRECTORY);
    }

    @AfterClass
    public static void tearDown() {
        testDirectory.delete();
    }

    @Test
    public void filterEqual() {
        List<VirtualFile> filteredList = testDirectory.find(new FileAttributesFilter().equalTo(
                new HashSet<FileAttribute>(Arrays.asList(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE))));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("subdir"));
    }

    @Test
    public void filterContains() {
        List<VirtualFile> filteredList = testDirectory.find(new FileAttributesFilter().contains(PosixFilePermission.GROUP_READ));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }

    @Test
    public void filterContainsAll() {
        List<VirtualFile> filteredList = testDirectory.find(new FileAttributesFilter().containsAll(
                new HashSet<FileAttribute>(Arrays.asList(PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ))));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }
}