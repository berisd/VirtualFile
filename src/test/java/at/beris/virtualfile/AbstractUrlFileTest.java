/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.os.OsFamily;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.util.OsUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static at.beris.virtualfile.FileTestHelper.*;
import static at.beris.virtualfile.provider.operation.CopyOperation.COPY_BUFFER_SIZE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

public abstract class AbstractUrlFileTest {

    private UrlFileContext fileContext;

    protected URL sourceFileUrl;
    protected URL targetFileUrl;
    protected URL sourceDirectoryUrl;
    protected URL targetDirectoryUrl;

    @Test
    public void createFile() {
        createFile(Optional.empty());
    }

    protected void createFile(Optional<Consumer<VirtualFile>> assertHook) {
        VirtualFile file = fileContext.newFile(sourceFileUrl);
        file.create();

        if (assertHook.isPresent()) {
            assertHook.get().accept(file);
        } else {
            assertEquals(TEST_SOURCE_FILE_NAME, file.getName());
            // FileStore.readAttributes for Windows might return old value, so don't check
            if (OsUtils.detectOSFamily() != OsFamily.WINDOWS)
                assertTrue(FileTestHelper.isDateCloseToNow(file.getCreationTime(), 10));
            assertTrue(FileTestHelper.isDateCloseToNow(file.getLastModifiedTime(), 10));
            assertTrue(FileTestHelper.isDateCloseToNow(file.getLastAccessTime(), 10));
            assertTrue(file.getOwner() != null);
            assertEquals(0, file.getSize());
            assertFalse(file.isDirectory());
        }
    }

    protected void createDirectory() {
        VirtualFile file = fileContext.newFile(sourceDirectoryUrl);
        file.create();

        assertEquals(TEST_SOURCE_DIRECTORY_NAME, file.getName());
        assertTrue(FileTestHelper.isDateCloseToNow(file.getLastModifiedTime(), 10));
        assertTrue(file.isDirectory());
    }

    protected void createSymbolicLink() throws IOException {
        String symLinkName = TEST_SOURCE_DIRECTORY_NAME + "Link";
        Path dir = new File(TEST_SOURCE_DIRECTORY_NAME).toPath();
        Path symLink = new File(symLinkName).toPath();

        Files.createDirectory(dir);
        Files.createSymbolicLink(symLink, dir);

        URL symLinkUrl = UrlUtils.getUrlForLocalPath(symLinkName);
        VirtualFile file = fileContext.newFile(symLinkUrl);

        assertTrue(file.isSymbolicLink());
        assertEquals(dir.toUri().toURL().toString(), file.getLinkTarget().toString());

        Files.delete(symLink);
        Files.delete(dir);
    }

    protected void copyFile() {
        VirtualFile sourceFile = FileTestHelper.createLocalSourceFile(UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME));
        VirtualFile targetFile = fileContext.newFile(targetFileUrl);
        CopyListener copyListenerMock = Mockito.mock(CopyListener.class);
        sourceFile.copy(targetFile, copyListenerMock);
        assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
        assertCopyListener(copyListenerMock);
    }

    protected void copyDirectory() throws IOException, URISyntaxException {
        List<String> sourceFileUrlList = createFilenamesTree(new File(TEST_SOURCE_DIRECTORY_NAME).toURI().toURL().toString() + "/");
        List<String> targetFileUrlList = createFilenamesTree(targetDirectoryUrl.toString());

        FileTestHelper.createFileTreeData(sourceFileUrlList);


        VirtualFile sourceDirectory = fileContext.newFile(UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME));
        VirtualFile targetDirectory = fileContext.newFile(targetDirectoryUrl);

        CopyListener copyListener = Mockito.mock(CopyListener.class);
        Mockito.when(copyListener.interrupt()).thenReturn(false);

        sourceDirectory.copy(targetDirectory, copyListener);
        assertDirectory(sourceFileUrlList, targetFileUrlList);
    }

    protected void deleteFile() {
        VirtualFile sourceFile = fileContext.newFile(sourceFileUrl);
        sourceFile.create();
        assertTrue(sourceFile.exists());
        sourceFile.delete();
        assertFalse(sourceFile.exists());
    }

    protected void deleteDirectory() throws IOException, URISyntaxException {
        List<String> sourceFileUrlList = createFilenamesTree(new File(TEST_SOURCE_DIRECTORY_NAME).toURI().toURL().toString() + "/");
        FileTestHelper.createFileTreeData(sourceFileUrlList);

        VirtualFile sourceDirectory = fileContext.newFile(UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME));
        VirtualFile targetDirectory = fileContext.newFile(targetDirectoryUrl);

        sourceDirectory.copy(targetDirectory, Mockito.mock(CopyListener.class));

        assertTrue(targetDirectory.exists());
        targetDirectory.delete();
        assertFalse(targetDirectory.exists());
    }

    protected void getFileAttributes(Consumer<VirtualFile> assertHook) {
        VirtualFile file = fileContext.newFile(sourceFileUrl);
        file.create();
        assertHook.accept(file);
    }

    protected void setFileAttributes(Set<FileAttribute> attributes) {
        VirtualFile file = fileContext.newFile(sourceFileUrl);
        file.create();
        file.setAttributes(attributes.toArray(new FileAttribute[0]));
        fileContext.dispose(file);

        file = fileContext.newFile(sourceFileUrl);
        Set<FileAttribute> actualAttributes = file.getAttributes();

        assertTrue(attributes.containsAll(actualAttributes));
        assertEquals(attributes.size(), actualAttributes.size());
    }

    protected void setOwner(UserPrincipal owner) {
        VirtualFile file = fileContext.newFile(sourceFileUrl);
        file.create();
        file.setOwner(owner);

        fileContext.dispose(file);

        file = fileContext.newFile(sourceFileUrl);
        assertEquals(owner.getName(), file.getOwner().getName());
    }

    protected void setGroup() {
        setGroup(null);
    }

    protected void setGroup(GroupPrincipal group) {
        VirtualFile file = fileContext.newFile(sourceFileUrl);
        file.create();
        if (group == null)
            group = file.getGroup();
        file.setGroup(group);

        fileContext.dispose(file);

        file = fileContext.newFile(sourceFileUrl);
        assertEquals(group.getName(), file.getGroup().getName());
    }

    protected void setAcl() {
        VirtualFile file = fileContext.newFile(sourceFileUrl);
        file.create();
        List<AclEntry> acl = file.getAcl();
        List<AclEntry> newAcl = new ArrayList<>(acl);
        newAcl.remove(0);
        file.setAcl(newAcl);
        getFileContext().dispose(file);

        file = getFileContext().newFile(sourceFileUrl);
        assertEquals(newAcl.size(), file.getAcl().size());
        file.delete();
    }

    protected void setCreationTime() {
        setTime((object, value) -> object.setCreationTime(value),
                virtualFile -> virtualFile.getCreationTime());
    }

    protected void setLastModifiedTime() {
        setTime((object, value) -> object.setLastModifiedTime(value),
                virtualFile -> virtualFile.getLastModifiedTime());
    }

    protected void setLastAccessTime() {
        setTime((object, value) -> object.setLastAccessTime(value),
                virtualFile -> virtualFile.getLastAccessTime());
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

            VirtualFile sourceFile = fileContext.newFile(UrlUtils.newUrl(sourceFileUrlList.get(i)));
            VirtualFile targetFile = fileContext.newFile(UrlUtils.newUrl(targetFileUrlList.get(i)));

            if (!sourceFile.isDirectory())
                assertArrayEquals(sourceFile.checksum(), targetFile.checksum());
        }
    }

    protected abstract UrlFileContext createFileContext();

    public UrlFileContext getFileContext() {
        return fileContext;
    }

    @Before
    public void beforeTestCase() {
        fileContext = createFileContext();
    }

    @After
    public void afterTestCase() {
        cleanupFiles();
        fileContext.dispose();
    }

    private void cleanupFiles() {
        for (URL url : new URL[]{sourceFileUrl, targetFileUrl, sourceDirectoryUrl, targetDirectoryUrl}) {
            if (url != null) {
                VirtualFile file = fileContext.newFile(url);
                if (file.exists())
                    file.delete();
            }
        }
    }

    private void setTime(BiConsumer<VirtualFile, FileTime> biconsumer, Function<VirtualFile, FileTime> function) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.roll(Calendar.YEAR, false);

        FileTime fileTime = FileTime.fromMillis(calendar.getTimeInMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        VirtualFile file = fileContext.newFile(sourceFileUrl);
        file.create();
        biconsumer.accept(file, fileTime);
        fileContext.dispose(file);

        file = fileContext.newFile(sourceFileUrl);
        assertEquals(dateFormat.format(new Date(fileTime.toMillis())), dateFormat.format(new Date(function.apply(file).toMillis())));
        file.delete();
        fileContext.dispose(file);
    }
}
