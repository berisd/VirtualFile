/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class LocalArchiveIntegrationTest extends AbstractFileTest {
    public static final String ZIP_FILENAME = "test.zip";

    @BeforeClass
    public static void setUp() throws Exception {
//        TestFileHelper.initIntegrationTest();
    }

    @Test
    public void createArchive() throws IOException {

        IFile archive = fileManager.newLocalFile(ZIP_FILENAME);

        archive.create();

        assertTrue(new File(archive.getPath()).exists());

//
//        IFile archivedFile1 = fileManager.newFile(archive, "hallo.txt");
//        IFile archivedFile2 = fileManager.newFile(archive, "hallo2.txt");
//        IFile directory3 = fileManager.newDirectory("dir1");
//        fileManager.newFile(directory3, "hallo3.txt");
//        fileManager.newFile(directory3, "hallo4.txt");
//        fileManager.newFile(directory3, "hallo5.txt");
//        IFile directory34 = fileManager.newDirectory(directory3, "dir2");
//        fileManager.newFile(directory34, "hallo6.txt");
//
//        archive.add(archivedFile1);
//        archive.add(archivedFile2);
//        archive.add(directory3);
    }
}
