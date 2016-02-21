/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.Attribute;
import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.IFile;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CollectionFilterTest {
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
    public void filterEqual() {
        List<IFile> filteredList = testDirectory.find(new FileAttributesFilter().equalTo(
                new HashSet<>(Arrays.asList(Attribute.OWNER_READ, Attribute.OWNER_WRITE, Attribute.OWNER_EXECUTE))));
        Assert.assertEquals(1, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("subdir"));
    }

    @Test
    public void filterContains() {
        List<IFile> filteredList = testDirectory.find(new FileAttributesFilter().contains(Attribute.GROUP_READ));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }

    @Test
    public void filterContainsAll() {
        List<IFile> filteredList = testDirectory.find(new FileAttributesFilter().containsAll(
                new HashSet<>(Arrays.asList(Attribute.OWNER_WRITE, Attribute.OWNER_EXECUTE, Attribute.GROUP_READ))));
        Assert.assertEquals(2, filteredList.size());
        List<String> filteredFileNameList = TestFilterHelper.getNameListFromFileList(filteredList);
        Assert.assertTrue(filteredFileNameList.contains("testfile2.txt"));
        Assert.assertTrue(filteredFileNameList.contains("goodmovie.avi"));
    }
}