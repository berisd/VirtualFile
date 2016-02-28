/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * This class contains real world code samples
 */
public class SamplesTest {
    @Test
    @Ignore
    public void addFilesToDirectory() {
        IDirectory dir = FileManager.newLocalDirectory("testdir");
        dir.create();
        IFile file = FileManager.newLocalFile("abc.txt");
        dir.add(file);
        file.create();
        dir.delete();
    }

    @Test
    public void extractArchive() {
        IArchive archive = FileManager.newLocalFile("src" + java.io.File.separator + "test" + File.separator +
                "resources" + File.separator + "testarchive.zip").asArchive();
        IDirectory directory = FileManager.newLocalDirectory("extracted");
        List<IFile> extractedFiles = archive.extract(directory);
        Assert.assertEquals(33, extractedFiles.size());
        directory.delete();
    }

    @Test
    public void listArchive() {
        IArchive archive = FileManager.newLocalArchive("src" + java.io.File.separator + "test" + File.separator +
                "resources" + File.separator + "testarchive.zip");
        Assert.assertEquals(33, archive.list().size());
    }
}
