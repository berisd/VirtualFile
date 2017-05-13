/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.UrlFileManager;
import at.beris.virtualfile.VirtualFile;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class StringFilterTest {
    private static final String TEST_DIRECTORY = "testdir/";
    private static VirtualFile testDirectory;
    private static UrlFileManager fileManager;

    @BeforeClass
    public static void setUp() throws Exception {
        fileManager = new UrlFileManager();
        TestFilterHelper.createFiles(fileManager, TEST_DIRECTORY);
        testDirectory = fileManager.resolveLocalFile(TEST_DIRECTORY);
    }

    @AfterClass
    public static void tearDown() {
        testDirectory.delete();
    }

    @Test
    public void filterContains() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().contains("good"));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }

    @Test
    public void filterStartsWith() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().startsWith("test"));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
    }

    @Test
    public void filterEndsWith() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().endsWith(".txt"));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
    }

    @Test
    public void filterMatches() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().matches(".*odm.*$"));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }
}