/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import at.beris.virtualfile.provider.FileOperationProvider;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.util.Collections;

public class FileTest {
    @Test
    public void create() throws Exception {
        FileOperationProvider fileOperationProvider = Mockito.mock(FileOperationProvider.class);
        FileOperation fileOperation = Mockito.mock(FileOperation.class);
        Client client = Mockito.mock(Client.class);
        FileModel model = new FileModel();
        UrlFile file = new UrlFile(new URL("file:/home/testdir/test.txt"), model, Collections.singletonMap(FileType.DEFAULT, fileOperationProvider),
                client, Collections.singletonMap(FileOperationEnum.COPY, fileOperation));
        file.create();
        Mockito.verify(fileOperationProvider).create(client, model);
    }

    @Test
    public void delete() throws Exception {
        FileOperationProvider fileOperationProvider = Mockito.mock(FileOperationProvider.class);
        FileOperation fileOperation = Mockito.mock(FileOperation.class);
        Client client = Mockito.mock(Client.class);
        FileModel model = new FileModel();
        UrlFile file = new UrlFile(new URL("file:/home/testdir/test.txt"), model, Collections.singletonMap(FileType.DEFAULT, fileOperationProvider),
                client, Collections.singletonMap(FileOperationEnum.COPY, fileOperation));
        file.delete();

        Mockito.verify(fileOperationProvider).delete(client, model);
    }

    @Test
    public void exists() throws Exception {
        FileOperationProvider fileOperationProvider = Mockito.mock(FileOperationProvider.class);
        FileOperation fileOperation = Mockito.mock(FileOperation.class);
        Client client = Mockito.mock(Client.class);
        FileModel model = new FileModel();
        UrlFile file = new UrlFile(new URL("file:/home/testdir/test.txt"), model, Collections.singletonMap(FileType.DEFAULT, fileOperationProvider),
                client, Collections.singletonMap(FileOperationEnum.COPY, fileOperation));
        file.exists();

        Mockito.verify(fileOperationProvider).exists(client, model);
    }
}