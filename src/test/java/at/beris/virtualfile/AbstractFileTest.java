/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.SingleValueOperationHook;
import at.beris.virtualfile.util.VoidOperationHook;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static at.beris.virtualfile.TestFileHelper.createFilenamesTree;
import static at.beris.virtualfile.operation.CopyOperation.COPY_BUFFER_SIZE;
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

    protected void createFile() {
        createFile(null);
    }

    protected void createFile(VoidOperationHook<IFile> assertHook) {
        IFile file = FileManager.newFile(sourceFileUrl);
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

    protected void createDirectory() {
        IFile file = FileManager.newFile(sourceDirectoryUrl);
        file.create();

        assertEquals(TEST_SOURCE_DIRECTORY_NAME, file.getName());
        assertTrue(TestFileHelper.isDateCloseToNow(file.getLastModifiedTime(), 10));
        assertTrue(file.isDirectory());
    }

    protected void copyFile() {
        IFile sourceFile = TestFileHelper.createLocalSourceFile(FileUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME));
        IFile targetFile = FileManager.newFile(targetFileUrl);
        CopyListener copyListenerMock = Mockito.mock(CopyListener.class);
        sourceFile.copy(targetFile, copyListenerMock);
        assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
        assertCopyListener(copyListenerMock);
        sourceFile.delete();
        targetFile.delete();
    }

    protected void copyDirectory() {
        try {
            List<String> sourceFileUrlList = createFilenamesTree(new File(TEST_SOURCE_DIRECTORY_NAME).toURI().toURL().toString() + "/");
            List<String> targetFileUrlList = createFilenamesTree(targetDirectoryUrl.toString());

            TestFileHelper.createFileTreeData(sourceFileUrlList);

            IFile sourceDirectory = FileManager.newLocalFile(TEST_SOURCE_DIRECTORY_NAME);
            IFile targetDirectory = FileManager.newFile(targetDirectoryUrl);

            CopyListener copyListener = Mockito.mock(CopyListener.class);
            Mockito.when(copyListener.interrupt()).thenReturn(false);

            sourceDirectory.copy(targetDirectory, copyListener);
            assertDirectory(sourceFileUrlList, targetFileUrlList);

            sourceDirectory.delete();
            targetDirectory.delete();
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    protected void deleteFile() {
        IFile sourceFile = FileManager.newFile(sourceFileUrl);
        sourceFile.create();
        assertTrue(sourceFile.exists());
        sourceFile.delete();
        assertFalse(sourceFile.exists());
    }

    protected void deleteDirectory() {
        try {
            List<String> sourceFileUrlList = createFilenamesTree(new File(TEST_SOURCE_DIRECTORY_NAME).toURI().toURL().toString() + "/");
            TestFileHelper.createFileTreeData(sourceFileUrlList);

            IFile sourceDirectory = FileManager.newLocalFile(TEST_SOURCE_DIRECTORY_NAME);
            IFile targetDirectory = FileManager.newFile(targetDirectoryUrl);

            sourceDirectory.copy(targetDirectory, Mockito.mock(CopyListener.class));

            assertTrue(targetDirectory.exists());
            targetDirectory.delete();
            assertFalse(targetDirectory.exists());

            sourceDirectory.delete();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void getFileAttributes(VoidOperationHook assertHook) {
        IFile file = FileManager.newFile(sourceFileUrl);
        file.create();
        assertHook.execute(file);
        file.delete();
    }

    protected void setOwner(UserPrincipal owner) {
        IFile file = FileManager.newFile(sourceFileUrl);
        file.create();
        file.setOwner(owner);

        FileManager.dispose(file);

        file = FileManager.newFile(sourceFileUrl);
        assertEquals(owner.getName(), file.getOwner().getName());
        file.delete();
    }

    protected void setGroup() {
        IFile file = FileManager.newFile(sourceFileUrl);
        file.create();
        GroupPrincipal group = file.getGroup();
        file.setGroup(group);

        FileManager.dispose(file);

        file = FileManager.newFile(sourceFileUrl);
        assertEquals(group.getName(), file.getGroup().getName());
        file.delete();
    }

    protected void setCreationTime() {
        setTime(new SingleValueOperationHook<IFile, FileTime>() {
            @Override
            public void setValue(IFile object, FileTime value) {
                object.setCreationTime(value);
            }

            @Override
            public FileTime getValue(IFile object) {
                return object.getCreationTime();
            }
        });
    }

    protected void setLastModifiedTime() {
        setTime(new SingleValueOperationHook<IFile, FileTime>() {
            @Override
            public void setValue(IFile object, FileTime value) {
                object.setLastModifiedTime(value);
            }

            @Override
            public FileTime getValue(IFile object) {
                return object.getLastModifiedTime();
            }
        });
    }

    protected void setLastAccessTime() {
        setTime(new SingleValueOperationHook<IFile, FileTime>() {
            @Override
            public void setValue(IFile object, FileTime value) {
                object.setLastAccessTime(value);
            }

            @Override
            public FileTime getValue(IFile object) {
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

    protected void assertDirectory(List<String> sourceFileUrlList, List<String> targetFileUrlList) {
        for (int i = 0; i < sourceFileUrlList.size(); i++) {
            IFile sourceFile = FileManager.newFile(sourceFileUrlList.get(i));
            IFile targetFile = FileManager.newFile(targetFileUrlList.get(i));

            if (!sourceFile.isDirectory())
                assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
        }
    }

    protected static void tearDown() {
        for (URL url : new URL[]{sourceFileUrl, targetFileUrl, sourceDirectoryUrl, targetDirectoryUrl}) {
            if (url != null) {
                IFile file = FileManager.newFile(url);
                if (file.exists())
                    file.delete();
            }
        }
    }

    private void setTime(SingleValueOperationHook<IFile, FileTime> operation) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.roll(Calendar.YEAR, false);

        FileTime fileTime = FileTime.fromMillis(calendar.getTimeInMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        IFile file = FileManager.newFile(sourceFileUrl);
        file.create();
        operation.setValue(file, fileTime);

        FileManager.dispose(file);

        file = FileManager.newFile(sourceFileUrl);
        assertEquals(dateFormat.format(new Date(fileTime.toMillis())), dateFormat.format(new Date(operation.getValue(file).toMillis())));
        file.delete();
    }
}
