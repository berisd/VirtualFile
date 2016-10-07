/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.FileManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class DefaultFilterTest {
    private static final String TEST_DIRECTORY = "testdir/";
    private static VirtualFile testDirectory;

    @BeforeClass
    public static void setUp() throws Exception {
        TestFilterHelper.createFiles(TEST_DIRECTORY);
        testDirectory = FileManager.newLocalFile(TEST_DIRECTORY);
    }

    @AfterClass
    public static void tearDown() throws IOException {
        testDirectory.delete();
    }

    @Test
    public void filterBetween() throws IOException {
        List<VirtualFile> filteredList = testDirectory.find(new FileSizeFilter().between(640L, 816L));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
    }

    @Test
    public void filterGreaterThan() throws IOException {
        List<VirtualFile> filteredList = testDirectory.find(new FileSizeFilter().greaterThan(640L));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }

    @Test
    public void filterGreaterThanOrEqual() throws IOException {
        List<VirtualFile> filteredList = testDirectory.find(new FileSizeFilter().greaterThanOrEqualTo(640L));
        Assert.assertEquals(3, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }

    @Test
    public void filterIn() throws IOException {
        List<VirtualFile> filteredList = testDirectory.find(new FileSizeFilter().in(640L, 800L));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
    }

    @Test
    public void filterLessThan() throws IOException {
        List<VirtualFile> filteredList = testDirectory.find(new FileSizeFilter().lessThan(640L));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("subdir"));
    }

    @Test
    public void filterLessThanOrEqual() throws IOException {
        List<VirtualFile> filteredList = testDirectory.find(new FileSizeFilter().lessThanOrEqualTo(640L));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("subdir"));
    }
}