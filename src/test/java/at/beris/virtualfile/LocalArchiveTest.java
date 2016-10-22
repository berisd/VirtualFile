/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class LocalArchiveTest extends AbstractFileTest {
    private static final String ZIP_FILENAME = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testarchive.zip";
    private static final int NUMBER_OF_ARCHIVE_ENTRIES = 33;

    @BeforeClass
    public static void setUp() throws Exception {
    }

    @Before
    public void beforeTestCase() throws Exception {
        super.beforeTestCase();
    }

    @After
    public void afterTestCase() throws IOException {
        super.afterTestCase();
    }

    @Test
    @Ignore
    public void createArchive() throws IOException {

        VirtualFile archiveFile = getFileContext().newLocalFile(ZIP_FILENAME);

        archiveFile.create();

        assertTrue(new File(archiveFile.getPath()).exists());
        assertTrue(archiveFile.isArchive());
        assertFalse(archiveFile.isArchived());

        archiveFile.delete();

//
//        VirtualFile archivedFile1 = fileManager.newFile(archive, "hallo.txt");
//        VirtualFile archivedFile2 = fileManager.newFile(archive, "hallo2.txt");
//        VirtualFile directory3 = fileManager.newDirectory("dir1");
//        fileManager.newFile(directory3, "hallo3.txt");
//        fileManager.newFile(directory3, "hallo4.txt");
//        fileManager.newFile(directory3, "hallo5.txt");
//        VirtualFile directory34 = fileManager.newDirectory(directory3, "dir2");
//        fileManager.newFile(directory34, "hallo6.txt");
//
//        archive.add(archivedFile1);
//        archive.add(archivedFile2);
//        archive.add(directory3);
    }

    @Test
    public void listArchive() throws IOException {
        VirtualFile file = getFileContext().newLocalFile(ZIP_FILENAME);
        assertTrue(file.getSize() > 0);
        List<VirtualFile> list = file.list();
        assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, list.size());
    }

    @Override
    protected FileContext createFileContext() {
        return new FileContext();
    }
}
