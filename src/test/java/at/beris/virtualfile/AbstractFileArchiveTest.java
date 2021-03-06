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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static at.beris.virtualfile.TestHelper.NUMBER_OF_ARCHIVE_ENTRIES;
import static org.junit.Assert.assertEquals;

public abstract class AbstractFileArchiveTest {

    private UrlFileManager fileManager;

    private VirtualArchive sourceArchive;

    private VirtualFile targetDirectory;

    protected static URL sourceArchiveUrl;

    @Before
    public void beforeTestCase() {
        fileManager = TestHelper.createFileManager();
        sourceArchive = fileManager.resolveFile(sourceArchiveUrl).asArchive();
        targetDirectory = fileManager.resolveLocalDirectory("extracted");
    }

    @After
    public void afterTestCase() {
        if (targetDirectory.exists()) {
            targetDirectory.delete();
        }
    }

    @Test
    public void list() {
        assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, sourceArchive.list().size());
    }

    @Test
    public void archiveEntry() {
        List<VirtualArchiveEntry> findEntryList = new ArrayList<>();
        findEntryList.add(createArchiveEntry("", "MyProject", 0, LocalDateTime.of(2016, 10, 25, 17, 42, 28), true));
        findEntryList.add(createArchiveEntry("MyProject/target/classes", "App.class", 3879, LocalDateTime.of(2015, 12, 24, 10, 25, 24), false));

        for (VirtualArchiveEntry archiveEntry : sourceArchive.list()) {
            for (VirtualArchiveEntry matchEntry : findEntryList) {
                if (isEqualArchiveEntry(archiveEntry, matchEntry)) {
                    findEntryList.remove(matchEntry);
                    break;
                }
            }
            if (findEntryList.size() == 0)
                break;
        }

        assertEquals(0, findEntryList.size());
    }

    @Test
    public void extract() {
        List<VirtualFile> extractedFiles = sourceArchive.extract(targetDirectory);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, extractedFiles.size());
    }

    private VirtualArchiveEntry createArchiveEntry(String path, String name, long size, LocalDateTime lastModified, boolean directory) {
        VirtualArchiveEntry archiveEntry = new FileArchiveEntry();
        archiveEntry.setPath(path);
        archiveEntry.setName(name);
        archiveEntry.setLastModified(DateUtils.getLocalDateTimeFromInstant(lastModified));
        archiveEntry.setDirectory(directory);
        archiveEntry.setSize(size);
        return archiveEntry;
    }

    private boolean isEqualArchiveEntry(VirtualArchiveEntry archiveEntry, VirtualArchiveEntry otherArchiveEntry) {
        String fullPath = archiveEntry.getPath() +  archiveEntry.getName();
        String fullPathOther = otherArchiveEntry.getPath() +  otherArchiveEntry.getName();

        if (!fullPath.equals(fullPathOther))
            return false;
        if (archiveEntry.getSize() != otherArchiveEntry.getSize())
            return false;
        if (!archiveEntry.getLastModified().equals(otherArchiveEntry.getLastModified()))
            return false;
        if (archiveEntry.isDirectory() != otherArchiveEntry.isDirectory())
            return false;
        return true;
    }
}