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

public class StringFilterTest {
    private static final String TEST_DIRECTORY = "testdir/";
    private static IFile testDirectory;

    @BeforeClass
    public static void setUp() throws Exception {
        TestFilterHelper.createFiles(TEST_DIRECTORY);
        testDirectory = FileManager.newLocalFile(TEST_DIRECTORY);
    }

    @AfterClass
    public static void tearDown() {
        testDirectory.delete();
    }

    @Test
    public void filterContains() {
        List<IFile> filteredList = testDirectory.find(new FileNameFilter().contains("good"));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }

    @Test
    public void filterStartsWith() {
        List<IFile> filteredList = testDirectory.find(new FileNameFilter().startsWith("test"));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
    }

    @Test
    public void filterEndsWith() {
        List<IFile> filteredList = testDirectory.find(new FileNameFilter().endsWith(".txt"));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
    }

    @Test
    public void filterMatches() {
        List<IFile> filteredList = testDirectory.find(new FileNameFilter().matches(".*odm.*$"));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }
}