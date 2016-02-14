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

public class BasicFilterTest {
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
    public void filterEqual() {
        List<IFile> filteredList = testDirectory.list(new FileNameFilter().equal("testfile1.txt"));
        Assert.assertEquals(1, filteredList.size());
        Assert.assertEquals("testfile1.txt", filteredList.get(0).getName());
    }
}