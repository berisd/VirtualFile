/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.jarcommander.filesystem.file;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static at.beris.jarcommander.filesystem.file.operation.CopyOperation.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;

public class LocalFileTest extends AbstractFileTest {

    @BeforeClass
    public static void setUp() throws Exception {
        initTest();
    }

    @AfterClass
    public static void tearDown() {
        deleteTestData();
    }

    @Test
    public void createFile() throws Exception {
        IFile sourceFile = createLocalSourceFile();
        assertEquals(TEST_SOURCE_FILE_NAME, sourceFile.getName());
        assertEquals(Date.from(TEST_SOURCE_FILE_LAST_MODIFIED).toString(), sourceFile.getLastModified().toString());
        assertEquals(TEST_SOURCE_FILE_SIZE, sourceFile.getSize());
        assertFalse(sourceFile.isDirectory());
    }

//    @Test
//    public void createDirectory() throws IOException {
//        LocalFile testDirectory = (LocalFile) fileManager.newInstance(Files.createDirectory(new File(TEST_SOURCE_DIRECTORY_NAME).toPath()).toFile());
//        assertEquals(TEST_SOURCE_DIRECTORY_NAME, testDirectory.getName());
//        assertTrue(testDirectory.isDirectory());
//        testDirectory.deleteFile();
//    }
//
//    @Test
//    public void copyFileSuccessfully() throws IOException {
//        LocalFile sourceFile = createLocalSourceFile();
//        super.copyFileToLocalHostSuccessfully(sourceFile, fileManager.newInstance(new File(TEST_TARGET_FILE_NAME)));
//    }
//
//    @Test
//    public void copyFileCancelled() throws IOException {
//        LocalFile sourceFile = createLocalSourceFile();
//        super.copyFileToLocalHostCancelled(sourceFile, fileManager.newInstance(new File(TEST_TARGET_FILE_NAME)));
//    }
//
//    @Test
//    public void copyFilesRecursively() throws IOException {
//        List<String> sourceFileNameList = createFilenamesTree(TEST_SOURCE_DIRECTORY_NAME);
//        List<String> targetFileNameList = createFilenamesTree(TEST_TARGET_DIRECTORY_NAME);
//
//        createFileTreeData(sourceFileNameList);
//
//        LocalFile sourceDirectory = (LocalFile) fileManager.newInstance(new File(TEST_SOURCE_DIRECTORY_NAME));
//
//        CopyListener copyListener = Mockito.mock(CopyListener.class);
//        Mockito.when(copyListener.interrupt()).thenReturn(false);
//
//        IFile targetDirectory = fileManager.newInstance(new File(TEST_TARGET_DIRECTORY_NAME));
//        sourceDirectory.copy(targetDirectory, copyListener);
//
//        List<IFile> targetFileList = targetFileNameList.stream().map(f -> fileManager.newInstance(new File(f))).collect(Collectors.toList());
//        List<IFile> sourceFileList = sourceFileNameList.stream().map(f -> fileManager.newInstance(new File(f))).collect(Collectors.toList());
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
//        sourceDirectory.deleteFile();
//        targetDirectory.deleteFile();
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

//    @Test
//    public void copyFileToRemoteSiteWithSftpSuccessFully() throws IOException {
//        SftpClient sshContext = createClient();
//        LocalFile sourceFile = createLocalSourceFile();
//        SshFile targetFile = new SshFile(sshContext, SSH_HOME_DIRECTORY + "/" + TEST_TARGET_FILE_NAME, fileManager);
//        CopyListener copyListener = Mockito.mock(CopyListener.class);
//
//        assertFalse(targetFile.exists());
//        sourceFile.copy(targetFile, copyListener);
//        //TODO Sync problem in sshfile.refresh, deshalb size=0 ?
//        assertEquals(sourceFile.getSize(), targetFile.getSize());
//        assertTrue(targetFile.exists());
//
//        sourceFile.deleteFile();
//        targetFile.deleteFile();
//    }

    @Test
    public void deleteFile() throws IOException {
        IFile sourceFile = createLocalSourceFile();
        assertTrue(sourceFile.exists());
        sourceFile.delete();
        assertFalse(sourceFile.exists());
    }

    @Test
    public void deleteFileRecursively() throws IOException {
        List<String> sourceFileNameList = createFilenamesTree(TEST_SOURCE_DIRECTORY_NAME);
        List<IFile> sourceFileList = createFileTreeData(sourceFileNameList);
        IFile sourceDirectory = sourceFileList.get(0);

        sourceFileList.stream().forEach(file -> assertTrue(file.exists()));
        sourceDirectory.delete();
        sourceFileList.stream().forEach(file -> assertFalse(file.exists()));
    }

    private List<String> createFilenamesTree(String rootDirectory) {
        List<String> fileList = new ArrayList<>();
        fileList.add(rootDirectory);
        fileList.add(rootDirectory + File.separator + "testfile1.txt");
        fileList.add(rootDirectory + File.separator + "testfile2.txt");
        fileList.add(rootDirectory + File.separator + "subdir");
        fileList.add(rootDirectory + File.separator + "subdir" + File.separator + "testfile3.txt");
        fileList.add(rootDirectory + File.separator + "subdir" + File.separator + "testfile4.txt");

        return fileList;
    }

    private List<IFile> createFileTreeData(List<String> fileNameList) throws IOException {
        String testString = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttest";
        StringBuilder dataString = new StringBuilder(testString);

        int index = 0;
        List<at.beris.jarcommander.filesystem.file.IFile> fileList = new ArrayList<>();
        for (String fileName : fileNameList) {
            File file = new File(fileName);
            if (fileName.indexOf('.') == -1) {
                // directory
                file.mkdirs();
            } else {
                // file
                if (file.getParentFile() != null)
                    file.getParentFile().mkdirs();

                index++;
                while (dataString.length() < COPY_BUFFER_SIZE * index + 10)
                    dataString.append(testString);

                Files.write(file.toPath(), dataString.toString().getBytes());
            }
//            fileList.add(fileManager.newInstance(new File(fileName)));
        }
        return fileList;
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

    private static void deleteTestData() {
        List<IFile> fileList = new ArrayList<>();

//        fileList.add(fileManager.newInstance(new File(TEST_SOURCE_FILE_NAME)));
//        fileList.add(fileManager.newInstance(new File(TEST_TARGET_FILE_NAME)));
//        fileList.add(fileManager.newInstance(new File(TEST_SOURCE_DIRECTORY_NAME)));
//        fileList.add(fileManager.newInstance(new File(TEST_TARGET_DIRECTORY_NAME)));

        fileList.stream().filter(file -> file.exists()).forEach(IFile::delete);
    }
}