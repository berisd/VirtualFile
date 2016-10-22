/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.protocol.Protocol;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static at.beris.virtualfile.provider.operation.CopyOperation.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;

public class FileContextTest {
    public final static String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public final static long TEST_SOURCE_FILE_SIZE = COPY_BUFFER_SIZE + 10;

    @Test
    public void enabledProtocols() {
        Set<Protocol> expectedProtocols = new HashSet<>();
        expectedProtocols.add(Protocol.FILE);
        expectedProtocols.add(Protocol.FTP);
        expectedProtocols.add(Protocol.SFTP);

        FileContext context = new FileContext();
        Set<Protocol> actualProtocols = context.enabledProtocols();

        Assert.assertTrue(actualProtocols.containsAll(expectedProtocols));
        Assert.assertEquals(expectedProtocols.size(), actualProtocols.size());
    }

    @Test
    public void createLocalFile() throws Exception {
        File sourceFile = createFile();
        byte[] checkSumBytes = generate_checksum(sourceFile);

        Byte[] expectedChecksum = new Byte[checkSumBytes.length];
        for (int i = 0; i < checkSumBytes.length; i++)
            expectedChecksum[i] = checkSumBytes[i];

        VirtualFile file = FileManager.newFile(new File(TEST_SOURCE_FILE_NAME).toURI().toURL());
        assertEquals(TEST_SOURCE_FILE_NAME, file.getName());
        assertEquals(TEST_SOURCE_FILE_SIZE, file.getSize());
        Assert.assertFalse(file.isDirectory());
        assertEquals(sourceFile.getAbsolutePath(), file.getPath());
        assertTrue(FileTestHelper.isDateClose(new Date(file.getLastModifiedTime().toMillis()), new Date(), 60));
        assertNotNull(file.getParent());
        Assert.assertArrayEquals(expectedChecksum, file.checksum());
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
//        Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(new Date().getTime()));
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