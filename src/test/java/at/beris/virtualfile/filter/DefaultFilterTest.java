/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.IFile;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class DefaultFilterTest {
    private static final String TEST_DIRECTORY = "testdir/";
    private static List<IFile> fileList;
    private static IFile testDirectory;

    @BeforeClass
    public static void setUp() throws Exception {
        fileList = TestFilterHelper.createFiles(TestFilterHelper.createFileNameList(TEST_DIRECTORY));
        testDirectory = FileManager.newLocalFile(TEST_DIRECTORY);
    }

    @AfterClass
    public static void tearDown() {
        testDirectory.delete();
    }

    @Test
    public void filterBetween() {
        List<IFile> filteredList = testDirectory.list(new FileSizeFilter().between(16 * 40L, 16 * 51L));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
    }

    @Test
    public void filterGreaterThan() {
        List<IFile> filteredList = testDirectory.find(new FileSizeFilter().greaterThan(16 * 40L));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }
}