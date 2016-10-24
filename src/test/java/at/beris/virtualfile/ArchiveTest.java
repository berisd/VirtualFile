/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.util.UrlUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static at.beris.virtualfile.FileTestHelper.NUMBER_OF_ARCHIVE_ENTRIES;
import static at.beris.virtualfile.FileTestHelper.ZIP_FILENAME;
import static org.junit.Assert.assertEquals;

public class ArchiveTest {

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
        Archive archive = fileContext.newFile(UrlUtils.getUrlForLocalPath(ZIP_FILENAME)).asArchive();
        assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, archive.list().size());
    }

    @Test
    public void extract() {
        Archive archive = FileManager.newLocalFile(ZIP_FILENAME).asArchive();

        List<VirtualFile> extractedFiles = archive.extract(targetDirectory);
        Assert.assertEquals(NUMBER_OF_ARCHIVE_ENTRIES, extractedFiles.size());
    }
}