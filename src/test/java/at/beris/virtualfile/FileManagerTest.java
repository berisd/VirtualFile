/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;

import static at.beris.virtualfile.operation.CopyOperation.COPY_BUFFER_SIZE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileManagerTest {
    public final static String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public final static long TEST_SOURCE_FILE_SIZE = COPY_BUFFER_SIZE + 10;
    public final static Instant TEST_SOURCE_FILE_LAST_MODIFIED = Instant.now();

    @Test
    public void createLocalFile() throws Exception {
        File sourceFile = createFile();
        byte[] expectedCheckSum = generate_checksum(sourceFile);

        IFile file = FileManager.newFile(new File(TEST_SOURCE_FILE_NAME).toURI().toURL());
        assertEquals(TEST_SOURCE_FILE_NAME, file.getName());
        assertEquals(TEST_SOURCE_FILE_SIZE, file.getSize());
        Assert.assertFalse(file.isDirectory());
        assertEquals(sourceFile.getAbsolutePath(), file.getPath());
        assertEquals(Date.from(TEST_SOURCE_FILE_LAST_MODIFIED).toString(), file.getLastModified().toString());
        assertNotNull(file.getParent());
        Assert.assertArrayEquals(expectedCheckSum, file.checksum());
        Assert.assertFalse(file.isArchive());
        Assert.assertFalse(file.isArchived());
        file.delete();
    }


    private File createFile() throws IOException {
        File file = new File(TEST_SOURCE_FILE_NAME);

        StringBuilder dataString = new StringBuilder("t");

        while (dataString.length() < TEST_SOURCE_FILE_SIZE)
            dataString.append("t");

        Files.write(file.toPath(), dataString.toString().getBytes());
        Files.setLastModifiedTime(file.toPath(), FileTime.from(TEST_SOURCE_FILE_LAST_MODIFIED));
        return file;
    }

    private byte[] generate_checksum(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(file.getPath());
        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        return md.digest();
    }
}