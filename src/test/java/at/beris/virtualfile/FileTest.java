/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import at.beris.virtualfile.operation.Listener;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FileTest {
    @Test
    public void create() throws Exception {
        Map<FileOperationEnum, FileOperation> fileOperationMap = new HashMap<>();
        FileOperation fileOperation = Mockito.mock(FileOperation.class);
        fileOperationMap.put(FileOperationEnum.CREATE, fileOperation);
        fileOperationMap.put(FileOperationEnum.UPDATE_MODEL, Mockito.mock(FileOperation.class));

        URL fileUrl = new URL("file:/home/testdir/test.txt");
        FileContext context = Mockito.mock(FileContext.class);
        Mockito.when(context.getFileOperationMap(fileUrl)).thenReturn(fileOperationMap);

        UrlFile file = new UrlFile(null, fileUrl, context);
        file.create();
        Mockito.verify(fileOperation).execute(Matchers.eq(file), Matchers.any(File.class), Matchers.any(Listener.class), Matchers.isNull());
    }

    @Test
    public void delete() throws Exception {
        Map<FileOperationEnum, FileOperation> fileOperationMap = new HashMap<>();
        FileOperation fileOperation = Mockito.mock(FileOperation.class);
        fileOperationMap.put(FileOperationEnum.DELETE, fileOperation);
        fileOperationMap.put(FileOperationEnum.UPDATE_MODEL, Mockito.mock(FileOperation.class));

        URL fileUrl = new URL("file:/home/testdir/test.txt");
        FileContext context = Mockito.mock(FileContext.class);
        Mockito.when(context.getFileOperationMap(fileUrl)).thenReturn(fileOperationMap);

        UrlFile file = new UrlFile(null, fileUrl, context);
        file.delete();

        Mockito.verify(fileOperation).execute(Matchers.eq(file), Matchers.any(File.class), Matchers.any(Listener.class), Matchers.isNull());
    }

    @Test
    public void exists() throws Exception {
        Map<FileOperationEnum, FileOperation> fileOperationMap = new HashMap<>();
        FileOperation fileOperation = Mockito.mock(FileOperation.class);
        fileOperationMap.put(FileOperationEnum.EXISTS, fileOperation);
        fileOperationMap.put(FileOperationEnum.UPDATE_MODEL, Mockito.mock(FileOperation.class));

        URL fileUrl = new URL("file:/home/testdir/test.txt");
        FileContext context = Mockito.mock(FileContext.class);
        Mockito.when(context.getFileOperationMap(fileUrl)).thenReturn(fileOperationMap);

        UrlFile file = new UrlFile(null, fileUrl, context);
        file.exists();

        Mockito.verify(fileOperation).execute(Matchers.eq(file), Matchers.any(File.class), Matchers.any(Listener.class), Matchers.isNull());
    }
}