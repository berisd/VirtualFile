/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.operation;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.UrlFile;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.provider.operation.CopyOperation;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;

public class CopyOperationTest {
    @Test
    public void copyFile() throws Exception {
        File sourceFile = createSourceFileMock(new URL("file:/source/foo"), false);
        File targetFile = createTargetFileMock(new URL("file:/target/foo"), false);

        CopyListener listener = Mockito.mock(CopyListener.class);
        FileContext fileContext = Mockito.mock(FileContext.class);
        FileOperationProvider fileOperationProvider = Mockito.mock(FileOperationProvider.class);

        new CopyOperation(fileContext, fileOperationProvider).execute(sourceFile, targetFile, listener);

        Mockito.verify(listener, times(1)).startFile(Matchers.any(File.class), Matchers.any(Long.class));
        Mockito.verify(listener, times(1)).afterBlockCopied(Matchers.any(Long.class), Matchers.eq(10L), Matchers.eq(10L));
    }

    @Test
    public void copyDirectory() throws Exception {
        File sourceFile = createSourceFileMock(new URL("file:/source/foo/"), true);
        File targetFile = createTargetFileMock(new URL("file:/target/foo/"), true);

        CopyListener listener = Mockito.mock(CopyListener.class);

        File sourceChildFile = createSourceFileMock(new URL("file:/source/foo/file.txt"), false);
        File sourceChildDirectory = createSourceFileMock(new URL("file:/source/foo/subdir/"), true);

        List<File> fileList = new ArrayList<>();
        fileList.add(sourceChildFile);
        fileList.add(sourceChildDirectory);
        Mockito.when(sourceFile.list()).thenReturn(fileList);

        File targetChildFile = createTargetFileMock(new URL("file:/target/foo/file.txt"), false);
        File targetChildDirectory = createTargetFileMock(new URL("file:/target/foo/subdir/"), true);

        FileContext fileContext = Mockito.mock(FileContext.class);
        Mockito.when(fileContext.newFile(Matchers.any(URL.class))).thenReturn(targetChildFile);
        Mockito.when(fileContext.newFile(Matchers.any(URL.class))).thenReturn(targetChildDirectory);

        FileOperationProvider fileOperationProvider = Mockito.mock(FileOperationProvider.class);

        new CopyOperation(fileContext, fileOperationProvider).execute(sourceFile, targetFile, listener);

        Mockito.verify(listener, times(1)).startFile(Matchers.any(File.class), Matchers.any(Long.class));
        Mockito.verify(listener, times(1)).afterBlockCopied(Matchers.any(Long.class), Matchers.eq(10L), Matchers.eq(10L));
    }

    private File createSourceFileMock(URL url, boolean isDirectory) throws Exception {
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.read(Mockito.any(byte[].class))).thenReturn(10).thenReturn(0);

        File sourceFile = createFileMock(url, isDirectory);
        Mockito.when(sourceFile.getInputStream()).thenReturn(inputStream);
        Mockito.when(sourceFile.exists()).thenReturn(true);
        return sourceFile;
    }

    private File createTargetFileMock(URL url, boolean isDirectory) throws Exception {
        OutputStream outputStream = Mockito.mock(OutputStream.class);

        File targetFile = createFileMock(url, isDirectory);
        Mockito.when(targetFile.getOutputStream()).thenReturn(outputStream);
        Mockito.when(targetFile.exists()).thenReturn(false);
        return targetFile;
    }

    private File createFileMock(URL url, boolean isDirectory) throws IOException {
        String[] pathParts = url.toString().split("/");
        File sourceFile = Mockito.mock(UrlFile.class);
        Mockito.when(sourceFile.getUrl()).thenReturn(url);
        Mockito.when(sourceFile.getName()).thenReturn(pathParts[pathParts.length - 1]);
        Mockito.when(sourceFile.isDirectory()).thenReturn(false);
        Mockito.when(sourceFile.isDirectory()).thenReturn(isDirectory);
        return sourceFile;
    }
}
