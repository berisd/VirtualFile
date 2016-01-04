/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.SftpClient;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;

import static at.beris.virtualfile.TestFileHelper.HOME_DIRECTORY;
import static at.beris.virtualfile.operation.CopyOperation.COPY_BUFFER_SIZE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;

public abstract class AbstractFileTest {
    public static String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public static String TEST_TARGET_FILE_NAME = "targetfile1.txt";
    public static int TEST_SOURCE_FILE_SIZE = COPY_BUFFER_SIZE + 10;
    public static Instant TEST_SOURCE_FILE_LAST_MODIFIED = Instant.now();

    public static String TEST_SOURCE_DIRECTORY_NAME = "testdirectory";
    public static String TEST_TARGET_DIRECTORY_NAME = "targettestdirectory";

    public static final String TEST_SITES_FILENAME = HOME_DIRECTORY + File.separator + "test" + File.separator + "sites.xml";

    public static final String SSH_HOME_DIRECTORY = "/home/sshtest";

    protected static FileManager fileManager;

    public static void initTest() throws Exception {
        org.junit.Assume.assumeTrue("Integration Test Data directory could not be found.", Files.exists(new File(TEST_SITES_FILENAME).toPath()));
    }

    public static IFile createLocalSourceFile() throws IOException {
        File file = new File(TEST_SOURCE_FILE_NAME);

        StringBuilder dataString = new StringBuilder("t");

        while (dataString.length() < TEST_SOURCE_FILE_SIZE)
            dataString.append("t");

        Files.write(file.toPath(), dataString.toString().getBytes());
        Files.setLastModifiedTime(file.toPath(), FileTime.from(TEST_SOURCE_FILE_LAST_MODIFIED));

        return FileManager.newFile(file.toURI().toURL());
    }

    public static IFile createSshFile(SftpClient sftpClient, String path) throws IOException {
//        LocalFile localFile = createLocalSourceFile();
//        URL url = new URL("file", "1.1.1.1", 80, path);
//        SshFile sshFile = new SshFile(url, sftpClient);
//        CopyListener copyListener = Mockito.mock(CopyListener.class);
//
//        localFile.copy(sshFile, copyListener);
//        localFile.deleteFile();
//
//        return new SshFile(url, sftpClient);
        return null;
    }

    public void assertCopyListener(CopyListener copyListener) {
        ArgumentCaptor<Long> bytesCopiedBlockArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> bytesCopiedTotalArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> fileSizeArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(copyListener, times(2)).afterBlockCopied(fileSizeArgumentCaptor.capture(), bytesCopiedBlockArgumentCaptor.capture(), bytesCopiedTotalArgumentCaptor.capture());

        List<Long> bytesCopiedBlockList = bytesCopiedBlockArgumentCaptor.getAllValues();
        List<Long> bytesCopiedTotalList = bytesCopiedTotalArgumentCaptor.getAllValues();

        assertEquals(2, bytesCopiedBlockList.size());
        assertEquals(COPY_BUFFER_SIZE, bytesCopiedBlockList.get(0).intValue());
        assertEquals(COPY_BUFFER_SIZE, bytesCopiedTotalList.get(0).intValue());

        assertEquals(TEST_SOURCE_FILE_SIZE % COPY_BUFFER_SIZE, bytesCopiedBlockList.get(1).intValue());
        assertEquals(TEST_SOURCE_FILE_SIZE, bytesCopiedTotalList.get(1).intValue());
    }

}
