/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.provider.IFileOperationProvider;
import at.beris.virtualfile.FileModel;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;

public class FileTest {
    @Test
    public void create() throws Exception {
        IFileOperationProvider fileOperationProvider = Mockito.mock(IFileOperationProvider.class);
        IClient client = Mockito.mock(IClient.class);
        FileModel model = Mockito.mock(FileModel.class);
        File file = new File(new URL("file:/home/testdir/test.txt"), model, fileOperationProvider, client);
        file.create();

        Mockito.verify(fileOperationProvider).create(client, model);
    }

    @Test
    public void delete() throws Exception {
        IFileOperationProvider fileOperationProvider = Mockito.mock(IFileOperationProvider.class);
        IClient client = Mockito.mock(IClient.class);
        FileModel model = Mockito.mock(FileModel.class);
        File file = new File(new URL("file:/home/testdir/test.txt"), model, fileOperationProvider, client);
        file.delete();

        Mockito.verify(fileOperationProvider).delete(client, model);
    }

    @Test
    public void exists() throws Exception {
        IFileOperationProvider fileOperationProvider = Mockito.mock(IFileOperationProvider.class);
        IClient client = Mockito.mock(IClient.class);
        FileModel model = Mockito.mock(FileModel.class);
        File file = new File(new URL("file:/home/testdir/test.txt"), model, fileOperationProvider, client);
        file.exists();

        Mockito.verify(fileOperationProvider).exists(client, model);
    }
}