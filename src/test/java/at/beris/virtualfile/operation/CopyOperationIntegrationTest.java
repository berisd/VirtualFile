/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.operation;

import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.IFile;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static at.beris.virtualfile.TestFileHelper.*;
import static at.beris.virtualfile.operation.CopyOperation.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;

@RunWith(Parameterized.class)
public class CopyOperationIntegrationTest {
    private static IFile sourceFile;
    private static IFile targetFile;
    private static String targetSite;

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        return Arrays.asList(new Object[][]{
                        {new File(".").toURI().toURL().toString()},
                {"sftp://sshtest:" + readSftpPassword() + "@www.beris.at:22/home/sshtest/"},
                }
        );
    }

    public CopyOperationIntegrationTest(String targetSite) {
        this.targetSite = targetSite;
    }

    @BeforeClass
    public static void setUp() throws Exception {
        initIntegrationTest();
    }

    @After
    public void afterTest() throws Exception {
        if (sourceFile != null && sourceFile.exists())
            sourceFile.delete();
        if (targetFile != null && targetFile.exists())
            targetFile.delete();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (sourceFile != null && sourceFile.getClient() != null)
            sourceFile.getClient().disconnect();
        if (targetFile != null && targetFile.getClient() != null)
            targetFile.getClient().disconnect();
    }

    @Test
    public void copyFileFromSourceToTargetSuccessFully() throws Exception {
        sourceFile = createLocalSourceFile(TEST_SOURCE_FILE_NAME);
        CopyListener copyListener = Mockito.mock(CopyListener.class);
        targetFile = FileManager.newFile(targetSite + TEST_TARGET_FILE_NAME);
        sourceFile.copy(targetFile, copyListener);
        assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
        assertEquals(TEST_SOURCE_FILE_SIZE, targetFile.getSize());
        Mockito.verify(copyListener).startCopyFile(sourceFile.getPath(), 1);
        Mockito.verify(copyListener, times((TEST_SOURCE_FILE_SIZE / COPY_BUFFER_SIZE) + 1)).afterBlockCopied(eq(targetFile.getSize()), anyLong(), anyLong());
    }

    @Test
    public void copyFromTargetToSourceSuccessFully() throws Exception {
        sourceFile = createLocalSourceFile(TEST_SOURCE_FILE_NAME);
        targetFile = FileManager.newFile(targetSite + TEST_TARGET_FILE_NAME);
        sourceFile.copy(targetFile, Mockito.mock(CopyListener.class));
        sourceFile.delete();
        CopyListener copyListener = Mockito.mock(CopyListener.class);
        targetFile.copy(sourceFile, copyListener);
        assertArrayEquals(targetFile.checksum(), sourceFile.checksum());
        assertEquals(TEST_SOURCE_FILE_SIZE, sourceFile.getSize());
        Mockito.verify(copyListener).startCopyFile(targetFile.getPath(), 1);
        Mockito.verify(copyListener, times((TEST_SOURCE_FILE_SIZE / COPY_BUFFER_SIZE) + 1)).afterBlockCopied(eq(targetFile.getSize()), anyLong(), anyLong());
    }

    @Test
    public void copyFilesRecursivelyToTargetSuccessFully() throws Exception {
        List<String> sourceFileUrlList = createFilenamesTree(new File(TEST_SOURCE_DIRECTORY_NAME).toURI().toURL().toString() + "/");
        List<String> targetFileUrlList = createFilenamesTree(targetSite + TEST_TARGET_DIRECTORY_NAME);

        createFileTreeData(sourceFileUrlList);

        IFile sourceDirectory = FileManager.newLocalFile(TEST_SOURCE_DIRECTORY_NAME);

        CopyListener copyListener = Mockito.mock(CopyListener.class);
        Mockito.when(copyListener.interrupt()).thenReturn(false);

        IFile targetDirectory = FileManager.newFile(targetSite + TEST_TARGET_DIRECTORY_NAME);
        sourceDirectory.copy(targetDirectory, copyListener);

//        List<IFile> targetFileList = targetFileUrlList.stream().map(f -> fileManager.newInstance(new File(f))).collect(Collectors.toList());
//        List<IFile> sourceFileList = sourceFileUrlList.stream().map(f -> fileManager.newInstance(new File(f))).collect(Collectors.toList());
//
//        assertFileTree(sourceFileList, targetFileList);
//
//        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<Long> fileIndexCaptor = ArgumentCaptor.forClass(Long.class);
//        Mockito.verify(copyListener, times(4)).startCopyFile(fileNameCaptor.capture(), fileIndexCaptor.capture());
//
//        List<Long> capturedIndexes = fileIndexCaptor.getAllValues();
//
//        for (long i = 1; i <= capturedIndexes.size(); i++)
//            assertEquals(i, capturedIndexes.get((int) i - 1).longValue());
//
        sourceDirectory.delete();
        targetDirectory.delete();
    }

    private static IFile createLocalSourceFile(String pathName) throws IOException {
        File file = new File(pathName);
        StringBuilder dataString = new StringBuilder("t");

        while (dataString.length() < TEST_SOURCE_FILE_SIZE)
            dataString.append("t");

        Files.write(file.toPath(), dataString.toString().getBytes());
        return FileManager.newLocalFile(pathName);
    }

    private void assertFileTree(List<IFile> sourceFileList, List<IFile> targetFileList) throws IOException {
        for (int i = 0; i < sourceFileList.size(); i++) {
            if (sourceFileList.get(i).isDirectory())
                continue;

            IFile sourceFile = sourceFileList.get(i);
            IFile targetFile = targetFileList.get(i);

            if (targetFile.exists()) {
                assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
            } else {
                fail("targetFile doesn't exist.");
            }
        }
    }

//    @Test
//    public void copyFileCancelled() throws IOException {
//        LocalFile sourceFile = createLocalSourceFile();
//        super.copyFileToLocalHostCancelled(sourceFile, fileManager.newInstance(new File(TEST_TARGET_FILE_NAME)));
//    }


//    @Test
//    public void copyFilesTargetExists() throws IOException {
//        List<String> sourceFileNameList = createFilenamesTree(TEST_SOURCE_DIRECTORY_NAME);
//        List<String> targetFileNameList = createFilenamesTree(TEST_TARGET_DIRECTORY_NAME);
//
//        createFileTreeData(sourceFileNameList);
//        createFileTreeData(targetFileNameList);
//
//        IFile sourceDirectory = fileManager.newInstance(new File(TEST_SOURCE_DIRECTORY_NAME));
//        IFile targetDirectory = fileManager.newInstance(new File(TEST_TARGET_DIRECTORY_NAME));
//
//        CopyListener copyListener = Mockito.mock(CopyListener.class);
//        Mockito.when(copyListener.interrupt()).thenReturn(false);
//
//        sourceDirectory.copy(targetDirectory, copyListener);
//
//        ArgumentCaptor<IFile> fileArgumentCaptor = ArgumentCaptor.forClass(IFile.class);
//        Mockito.verify(copyListener, times(sourceFileNameList.size())).fileExists(fileArgumentCaptor.capture());
//
//        List<IFile> capturedFiles = fileArgumentCaptor.getAllValues();
//        List<String> capturedFileNameList = new ArrayList<>();
//
//        String absoluteBasePath = capturedFiles.get(0).getFile();
//        String relativeBasePath = targetFileNameList.get(0);
//
//        for (int i = 0; i < capturedFiles.size(); i++) {
//            String capturedFileName = capturedFiles.get(i).getFile();
//            capturedFileNameList.add(capturedFileName.replace(absoluteBasePath, relativeBasePath));
//        }
//
//        assertEquals(targetFileNameList.size(), capturedFileNameList.size());
//        assertTrue(targetFileNameList.containsAll(capturedFileNameList));
//
//        sourceDirectory.deleteFile();
//        targetDirectory.deleteFile();
//    }
}