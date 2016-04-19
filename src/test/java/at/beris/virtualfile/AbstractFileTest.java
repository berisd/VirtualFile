/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.util.SingleValueOperation;
import at.beris.virtualfile.util.UrlUtils;
import at.beris.virtualfile.util.VoidOperation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.*;

import static at.beris.virtualfile.TestFileHelper.createFilenamesTree;
import static at.beris.virtualfile.provider.operation.CopyOperation.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

/**
 * Abstract class to test file operations for a protocol
 * An abstract class was chosen because not every protocol supports all operations
 */
public abstract class AbstractFileTest {
    public static String TEST_SOURCE_FILE_NAME = "testfile1.txt";
    public static String TEST_TARGET_FILE_NAME = "targetfile1.txt";
    public static int TEST_SOURCE_FILE_SIZE = COPY_BUFFER_SIZE + 10;

    public static String TEST_SOURCE_DIRECTORY_NAME = "testdirectory";
    public static String TEST_TARGET_DIRECTORY_NAME = "targettestdirectory";

    protected static FileManager fileManager;

    protected static URL sourceFileUrl;
    protected static URL targetFileUrl;
    protected static URL sourceDirectoryUrl;
    protected static URL targetDirectoryUrl;

    protected void createFile() throws IOException {
        createFile(null);
    }

    protected void createFile(VoidOperation<File> assertHook) throws IOException {
        File file = FileManager.newFile(sourceFileUrl);
        file.create();

        if (assertHook != null) {
            assertHook.execute(file);
        } else {
            assertEquals(TEST_SOURCE_FILE_NAME, file.getName());
            assertTrue(TestFileHelper.isDateCloseToNow(file.getCreationTime(), 10));
            assertTrue(TestFileHelper.isDateCloseToNow(file.getLastModifiedTime(), 10));
            assertTrue(TestFileHelper.isDateCloseToNow(file.getLastAccessTime(), 10));
            assertTrue(file.getOwner() instanceof UserPrincipal);
            assertEquals(0, file.getSize());
            assertFalse(file.isDirectory());
        }
        file.delete();
    }

    protected void createDirectory() throws IOException {
        File file = FileManager.newFile(sourceDirectoryUrl);
        file.create();

        assertEquals(TEST_SOURCE_DIRECTORY_NAME, file.getName());
        assertTrue(TestFileHelper.isDateCloseToNow(file.getLastModifiedTime(), 10));
        assertTrue(file.isDirectory());
    }

    protected void copyFile() throws IOException {
        File sourceFile = TestFileHelper.createLocalSourceFile(UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME));
        File targetFile = FileManager.newFile(targetFileUrl);
        CopyListener copyListenerMock = Mockito.mock(CopyListener.class);
        sourceFile.copy(targetFile, copyListenerMock);
        assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
        assertCopyListener(copyListenerMock);
        sourceFile.delete();
        targetFile.delete();
    }

    protected void copyDirectory() throws IOException {
        List<String> sourceFileUrlList = createFilenamesTree(new java.io.File(TEST_SOURCE_DIRECTORY_NAME).toURI().toURL().toString() + "/");
        List<String> targetFileUrlList = createFilenamesTree(targetDirectoryUrl.toString());

        TestFileHelper.createFileTreeData(sourceFileUrlList);

        File sourceDirectory = FileManager.newLocalFile(TEST_SOURCE_DIRECTORY_NAME);
        File targetDirectory = FileManager.newFile(targetDirectoryUrl);

        CopyListener copyListener = Mockito.mock(CopyListener.class);
        Mockito.when(copyListener.interrupt()).thenReturn(false);

        sourceDirectory.copy(targetDirectory, copyListener);
        assertDirectory(sourceFileUrlList, targetFileUrlList);

        sourceDirectory.delete();
        targetDirectory.delete();
    }

    protected void deleteFile() throws IOException {
        File sourceFile = FileManager.newFile(sourceFileUrl);
        sourceFile.create();
        assertTrue(sourceFile.exists());
        sourceFile.delete();
        assertFalse(sourceFile.exists());
    }

    protected void deleteDirectory() throws IOException {
        List<String> sourceFileUrlList = createFilenamesTree(new java.io.File(TEST_SOURCE_DIRECTORY_NAME).toURI().toURL().toString() + "/");
        TestFileHelper.createFileTreeData(sourceFileUrlList);

        File sourceDirectory = FileManager.newLocalFile(TEST_SOURCE_DIRECTORY_NAME);
        File targetDirectory = FileManager.newFile(targetDirectoryUrl);

        sourceDirectory.copy(targetDirectory, Mockito.mock(CopyListener.class));

        assertTrue(targetDirectory.exists());
        targetDirectory.delete();
        assertFalse(targetDirectory.exists());

        sourceDirectory.delete();
    }

    protected void getFileAttributes(VoidOperation assertHook) throws IOException {
        File file = FileManager.newFile(sourceFileUrl);
        file.create();
        assertHook.execute(file);
        file.delete();
    }

    protected void setFileAttributes(Set<FileAttribute> attributes) throws IOException {
        File file = FileManager.newFile(sourceFileUrl);
        file.create();
        file.setAttributes(attributes.toArray(new FileAttribute[0]));
        FileManager.dispose(file);

        file = FileManager.newFile(sourceFileUrl);
        Set<FileAttribute> actualAttributes = file.getAttributes();

        assertTrue(attributes.containsAll(actualAttributes));
        assertEquals(attributes.size(), actualAttributes.size());

        file.delete();
    }

    protected void setOwner(UserPrincipal owner) throws IOException {
        File file = FileManager.newFile(sourceFileUrl);
        file.create();
        file.setOwner(owner);

        FileManager.dispose(file);

        file = FileManager.newFile(sourceFileUrl);
        assertEquals(owner.getName(), file.getOwner().getName());
        file.delete();
    }

    protected void setGroup() throws IOException {
        setGroup(null);
    }

    protected void setGroup(GroupPrincipal group) throws IOException {
        File file = FileManager.newFile(sourceFileUrl);
        file.create();
        if (group == null)
            group = file.getGroup();
        file.setGroup(group);

        FileManager.dispose(file);

        file = FileManager.newFile(sourceFileUrl);
        assertEquals(group.getName(), file.getGroup().getName());
        file.delete();
    }

    protected void setCreationTime() throws IOException {
        setTime(new SingleValueOperation<File, FileTime>() {
            @Override
            public void setValue(File object, FileTime value) throws IOException {
                object.setCreationTime(value);
            }

            @Override
            public FileTime getValue(File object) throws IOException {
                return object.getCreationTime();
            }
        });
    }

    protected void setLastModifiedTime() throws IOException {
        setTime(new SingleValueOperation<File, FileTime>() {
            @Override
            public void setValue(File object, FileTime value) throws IOException {
                object.setLastModifiedTime(value);
            }

            @Override
            public FileTime getValue(File object) throws IOException {
                return object.getLastModifiedTime();
            }
        });
    }

    protected void setLastAccessTime() throws IOException {
        setTime(new SingleValueOperation<File, FileTime>() {
            @Override
            public void setValue(File object, FileTime value) throws IOException {
                object.setLastAccessTime(value);
            }

            @Override
            public FileTime getValue(File object) throws IOException {
                return object.getLastAccessTime();
            }
        });
    }

    protected void assertCopyListener(CopyListener copyListener) {
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

    protected void assertDirectory(List<String> sourceFileUrlList, List<String> targetFileUrlList) throws IOException {
        for (int i = 0; i < sourceFileUrlList.size(); i++) {
            File sourceFile = FileManager.newFile(sourceFileUrlList.get(i));
            File targetFile = FileManager.newFile(targetFileUrlList.get(i));

            if (!sourceFile.isDirectory())
                assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
        }
    }

    protected static void tearDown() throws IOException {
        for (URL url : new URL[]{sourceFileUrl, targetFileUrl, sourceDirectoryUrl, targetDirectoryUrl}) {
            if (url != null) {
                File file = FileManager.newFile(url);
                if (file.exists())
                    file.delete();
            }
        }
    }

    private void setTime(SingleValueOperation<File, FileTime> operation) throws IOException {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.roll(Calendar.YEAR, false);

        FileTime fileTime = FileTime.fromMillis(calendar.getTimeInMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        File file = FileManager.newFile(sourceFileUrl);
        file.create();
        operation.setValue(file, fileTime);

        FileManager.dispose(file);

        file = FileManager.newFile(sourceFileUrl);
        assertEquals(dateFormat.format(new Date(fileTime.toMillis())), dateFormat.format(new Date(operation.getValue(file).toMillis())));
        file.delete();
    }
}
