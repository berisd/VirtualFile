/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import org.junit.*;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class LocalArchiveTest extends AbstractFileTest {
    private static final String ZIP_FILENAME = "src" + java.io.File.separator + "test" + java.io.File.separator + "resources" + java.io.File.separator + "testarchive.zip";
    private static final int NUMBER_OF_ARCHIVE_ENTRIES = 33;

    @BeforeClass
    public static void setUp() throws Exception {
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
    }

    @After
    public void afterTest() throws IOException {
        super.afterTest();
    }

    @Test
    @Ignore
    public void createArchive() throws IOException {

        File archiveFile = fileContext.newLocalFile(ZIP_FILENAME);

        archiveFile.create();

        assertTrue(new java.io.File(archiveFile.getPath()).exists());
        assertTrue(archiveFile.isArchive());
        assertFalse(archiveFile.isArchived());

        archiveFile.delete();

//
//        File archivedFile1 = fileManager.newFile(archive, "hallo.txt");
//        File archivedFile2 = fileManager.newFile(archive, "hallo2.txt");
//        File directory3 = fileManager.newDirectory("dir1");
//        fileManager.newFile(directory3, "hallo3.txt");
//        fileManager.newFile(directory3, "hallo4.txt");
//        fileManager.newFile(directory3, "hallo5.txt");
//        File directory34 = fileManager.newDirectory(directory3, "dir2");
//        fileManager.newFile(directory34, "hallo6.txt");
//
//        archive.add(archivedFile1);
//        archive.add(archivedFile2);
//        archive.add(directory3);
    }

    @Test
    public void listArchive() throws IOException {
        File file = fileContext.newLocalFile(ZIP_FILENAME);
        assertTrue(file.getSize() > 0);
        List<File> list = file.list();
        assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, list.size());
    }
}
