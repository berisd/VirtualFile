/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.util.DateUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static at.beris.virtualfile.FileTestHelper.NUMBER_OF_ARCHIVE_ENTRIES;
import static at.beris.virtualfile.FileTestHelper.ZIP_FILENAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileArchiveTest {

    private UrlFileContext fileContext;

    private VirtualFile targetDirectory;

    @Before
    public void beforeTestCase() {
        fileContext = new UrlFileContext();
        targetDirectory = FileManager.newLocalDirectory("extracted");
    }

    @After
    public void afterTestCase() {
        if (targetDirectory.exists()) {
            targetDirectory.delete();
        }
    }

    @Test
    public void list() {
        VirtualArchive archive = fileContext.newFile(UrlUtils.getUrlForLocalPath(ZIP_FILENAME)).asArchive();
        assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, archive.list().size());
    }

    @Test
    public void archiveEntry() {
        VirtualArchive archive = fileContext.newFile(UrlUtils.getUrlForLocalPath(ZIP_FILENAME)).asArchive();
        List<VirtualArchiveEntry> archiveEntryList = archive.list();

        assertArchiveEntry(createArchiveEntry("", "TreeDb", 0, LocalDateTime.of(2015, 12, 24, 10, 48, 55), true), archiveEntryList.get(0));
        assertArchiveEntry(createArchiveEntry("TreeDb/.idea", "uiDesigner.xml", 8792, LocalDateTime.of(2015, 12, 23, 22, 45, 53), false), archiveEntryList.get(8));
    }

    @Test
    public void extract() {
        VirtualArchive archive = FileManager.newLocalFile(ZIP_FILENAME).asArchive();
        List<VirtualFile> extractedFiles = archive.extract(targetDirectory);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, extractedFiles.size());
    }

    private FileArchiveEntry createArchiveEntry(String path, String name, long size, LocalDateTime lastModified, boolean directory) {
        FileArchiveEntry archiveEntry = new FileArchiveEntry();
        archiveEntry.setPath(path);
        archiveEntry.setName(name);
        archiveEntry.setLastModified(DateUtils.getLocalDateTimeFromInstant(lastModified));
        archiveEntry.setDirectory(directory);
        archiveEntry.setSize(size);
        return archiveEntry;
    }

    private void assertArchiveEntry(VirtualArchiveEntry expected, VirtualArchiveEntry actual) {
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getSize(), actual.getSize());
        assertTrue(expected.getLastModified().equals(actual.getLastModified()));
        assertEquals(expected.isDirectory(), actual.isDirectory());
    }
}