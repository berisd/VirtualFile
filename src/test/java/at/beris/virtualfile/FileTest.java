/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.provider.FileOperationProvider;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.net.URL;

public class FileTest {
    @Test
    public void create() throws Exception {
        FileOperationProvider provider = Mockito.mock(FileOperationProvider.class);

        URL fileUrl = new URL("file:/home/testdir/test.txt");
        FileContext context = Mockito.mock(FileContext.class);
        Mockito.when(context.getFileOperationProvider(fileUrl.toString())).thenReturn(provider);

        UrlFile file = new UrlFile(null, fileUrl, context);
        file.create();
        Mockito.verify(provider).create(Matchers.eq(file.getModel()));
    }

    @Test
    public void delete() throws Exception {
        FileOperationProvider provider = Mockito.mock(FileOperationProvider.class);

        URL fileUrl = new URL("file:/home/testdir/test.txt");
        FileContext context = Mockito.mock(FileContext.class);
        Mockito.when(context.getFileOperationProvider(fileUrl.toString())).thenReturn(provider);

        UrlFile file = new UrlFile(null, fileUrl, context);
        file.delete();

        Mockito.verify(provider).delete(Matchers.eq(file.getModel()));
    }

    @Test
    public void exists() throws Exception {
        FileOperationProvider provider = Mockito.mock(FileOperationProvider.class);

        URL fileUrl = new URL("file:/home/testdir/test.txt");
        FileContext context = Mockito.mock(FileContext.class);
        Mockito.when(context.getFileOperationProvider(fileUrl.toString())).thenReturn(provider);

        UrlFile file = new UrlFile(null, fileUrl, context);
        file.exists();

        Mockito.verify(provider).exists(Matchers.eq(file.getModel()));
    }
}