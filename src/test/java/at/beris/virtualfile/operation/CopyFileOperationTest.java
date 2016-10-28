/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.operation;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.VirtualFileContext;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.provider.operation.CopyFileOperation;
import at.beris.virtualfile.provider.operation.FileOperationListener;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

public class CopyFileOperationTest {
    @Test
    public void copyFile() throws Exception {
        VirtualFile sourceFile = createSourceFileMock(new URL("file:/source/foo"), false);
        VirtualFile targetFile = createTargetFileMock(new URL("file:/target/foo"), false);

        FileOperationListener listener = Mockito.mock(FileOperationListener.class);
        VirtualFileContext fileContext = Mockito.mock(VirtualFileContext.class);
        FileOperationProvider fileOperationProvider = Mockito.mock(FileOperationProvider.class);

        new CopyFileOperation(fileContext, fileOperationProvider).execute(sourceFile, targetFile, listener);

        Mockito.verify(listener, times(1)).startProcessingFile(Matchers.any(VirtualFile.class), Matchers.any(Long.class));
        Mockito.verify(listener, times(1)).afterStreamBufferProcessed(Matchers.any(Long.class), Matchers.eq(10L), Matchers.eq(10L));
    }

    @Test
    public void copyDirectory() throws Exception {
        VirtualFile sourceFile = createSourceFileMock(new URL("file:/source/foo/"), true);
        VirtualFile targetFile = createTargetFileMock(new URL("file:/target/foo/"), true);

        FileOperationListener listener = Mockito.mock(FileOperationListener.class);

        VirtualFile sourceChildFile = createSourceFileMock(new URL("file:/source/foo/file.txt"), false);
        VirtualFile sourceChildDirectory = createSourceFileMock(new URL("file:/source/foo/subdir/"), true);

        List<VirtualFile> fileList = new ArrayList<>();
        fileList.add(sourceChildFile);
        fileList.add(sourceChildDirectory);
        Mockito.when(sourceFile.list()).thenReturn(fileList);

        VirtualFile targetChildFile = createTargetFileMock(new URL("file:/target/foo/file.txt"), false);
        VirtualFile targetChildDirectory = createTargetFileMock(new URL("file:/target/foo/subdir/"), true);

        VirtualFileContext fileContext = Mockito.mock(VirtualFileContext.class);
        Mockito.when(fileContext.newFile(Matchers.any(URL.class))).thenReturn(targetChildFile);
        Mockito.when(fileContext.newFile(Matchers.any(URL.class))).thenReturn(targetChildDirectory);

        FileOperationProvider fileOperationProvider = Mockito.mock(FileOperationProvider.class);

        new CopyFileOperation(fileContext, fileOperationProvider).execute(sourceFile, targetFile, listener);

        Mockito.verify(listener, times(3)).startProcessingFile(Matchers.any(VirtualFile.class), Matchers.any(Long.class));
        Mockito.verify(listener, times(1)).afterStreamBufferProcessed(Matchers.any(Long.class), Matchers.eq(10L), Matchers.eq(10L));
    }

    private VirtualFile createSourceFileMock(URL url, boolean isDirectory) throws Exception {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.read(Mockito.any(byte[].class))).thenReturn(10).thenReturn(0);

        VirtualFile sourceFile = createFileMock(url, isDirectory);
        Mockito.when(sourceFile.getInputStream()).thenReturn(inputStream);
        Mockito.when(sourceFile.exists()).thenReturn(true);
        return sourceFile;
    }

    private VirtualFile createTargetFileMock(URL url, boolean isDirectory) throws Exception {
        OutputStream outputStream = Mockito.mock(OutputStream.class);

        VirtualFile targetFile = createFileMock(url, isDirectory);
        Mockito.when(targetFile.getOutputStream()).thenReturn(outputStream);
        Mockito.when(targetFile.exists()).thenReturn(false);
        return targetFile;
    }

    private VirtualFile createFileMock(URL url, boolean isDirectory) {
        String[] pathParts = url.toString().split("/");
        VirtualFile sourceFile = Mockito.mock(VirtualFile.class);
        Mockito.when(sourceFile.getUrl()).thenReturn(url);
        Mockito.when(sourceFile.getName()).thenReturn(pathParts[pathParts.length - 1]);
        Mockito.when(sourceFile.isDirectory()).thenReturn(false);
        Mockito.when(sourceFile.isDirectory()).thenReturn(isDirectory);
        return sourceFile;
    }
}
