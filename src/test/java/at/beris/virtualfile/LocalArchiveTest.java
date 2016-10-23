/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocalArchiveTest extends AbstractUrlFileTest {
    private static final String ZIP_FILENAME = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testarchive.zip";
    private static final int NUMBER_OF_ARCHIVE_ENTRIES = 33;

    @Before
    @Override
    public void beforeTestCase() throws Exception {
        super.beforeTestCase();
    }

    @After
    @Override
    public void afterTestCase() {
        super.afterTestCase();
    }

    @Test(expected = NotImplementedException.class)
    public void createFile() {
        throw new NotImplementedException();
    }

    @Test
    @Ignore
    public void createArchive() {
        VirtualFile archiveFile = getFileContext().newFile(UrlUtils.getUrlForLocalPath(ZIP_FILENAME));
        archiveFile.create();

        assertTrue(new File(archiveFile.getPath()).exists());
        assertTrue(archiveFile.isArchive());
//        assertFalse(archiveFile.isArchived());

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
    public void listArchive() {
        VirtualFile file = getFileContext().newFile(UrlUtils.getUrlForLocalPath(ZIP_FILENAME));
        assertTrue(file.getSize() > 0);
        List<VirtualFile> list = file.list();
        assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, list.size());
    }

    @Override
    protected UrlFileContext createFileContext() {
        return new UrlFileContext();
    }
}
