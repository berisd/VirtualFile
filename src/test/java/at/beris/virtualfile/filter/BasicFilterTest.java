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
import at.beris.virtualfile.attribute.PosixFilePermission;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class BasicFilterTest {
    private static final String TEST_DIRECTORY = "testdir/";
    private static VirtualFile testDirectory;

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
    public void filterEqual() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().equalTo("testfile1.txt"));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
    }

    @Test
    public void filterAnd() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().equalTo("testfile1.txt").and(new IsDirectoryFilter().equalTo(false)));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
    }

    @Test
    public void filterAndNot() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().endsWith(".txt").andNot(new FileAttributesFilter().contains(PosixFilePermission.OTHERS_READ)));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
    }

    @Test
    public void filterOr() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().equalTo("testfile1.txt").or(new FileNameFilter().equalTo("subdir")));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("subdir"));
    }

    @Test
    public void filterOrNot() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().endsWith(".txt").orNot(new FileSizeFilter().lessThan(800L)));
        Assert.assertEquals(3, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("testfile1.txt"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }

    @Test
    public void filterNot() {
        List<VirtualFile> filteredList = testDirectory.find(new FileNameFilter().not().equalTo("testfile1.txt"));
        Assert.assertEquals(3, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("subdir"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }
}