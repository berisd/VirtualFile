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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.util.Date;

import static at.beris.virtualfile.provider.operation.CopyOperation.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;

public class FileManagerTest {
    public final static String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public final static long TEST_SOURCE_FILE_SIZE = COPY_BUFFER_SIZE + 10;
    public final static Date TEST_SOURCE_FILE_LAST_MODIFIED = new Date();

    @Test
    public void createLocalFile() throws Exception {
        java.io.File sourceFile = createFile();
        byte[] checkSumBytes = generate_checksum(sourceFile);


        Byte[] expectedChecksum = new Byte[checkSumBytes.length];
        for (int i = 0; i < checkSumBytes.length; i++)
            expectedChecksum[i] = checkSumBytes[i];

        File file = FileManager.newFile(new java.io.File(TEST_SOURCE_FILE_NAME).toURI().toURL());
        assertEquals(TEST_SOURCE_FILE_NAME, file.getName());
        assertEquals(TEST_SOURCE_FILE_SIZE, file.getSize());
        Assert.assertFalse(file.isDirectory());
        assertEquals(sourceFile.getAbsolutePath(), file.getPath());
        assertTrue(TestFileHelper.isDateClose(TEST_SOURCE_FILE_LAST_MODIFIED, new Date(file.getLastModifiedTime().toMillis()), 2));
        assertNotNull(file.getParent());
        Assert.assertArrayEquals(expectedChecksum, file.checksum());
        Assert.assertFalse(file.isArchive());
        Assert.assertFalse(file.isArchived());
        file.delete();
    }


    private java.io.File createFile() throws IOException {
        java.io.File file = new java.io.File(TEST_SOURCE_FILE_NAME);

        StringBuilder dataString = new StringBuilder("t");

        while (dataString.length() < TEST_SOURCE_FILE_SIZE)
            dataString.append("t");

        Files.write(file.toPath(), dataString.toString().getBytes());
        Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(TEST_SOURCE_FILE_LAST_MODIFIED.getTime()));
        return file;
    }

    private byte[] generate_checksum(java.io.File file) throws Exception {
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