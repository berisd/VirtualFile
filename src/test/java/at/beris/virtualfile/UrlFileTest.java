/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.provider.FileOperationProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URL;

@RunWith(MockitoJUnitRunner.class)
public class UrlFileTest extends AbstractFileTest {
    @Mock
    private FileOperationProvider provider;

    private UrlFile file;

    @Before
    public void setUp() throws Exception {
        super.beforeTestCase();
        sourceFileUrl = new URL(String.format("file:/%s/%s", TEST_SOURCE_DIRECTORY_NAME, TEST_SOURCE_FILE_NAME));
        Mockito.when(getFileContext().getFileOperationProvider(sourceFileUrl.toString())).thenReturn(provider);
        file = new UrlFile(sourceFileUrl, getFileContext());
    }

    @Test
    public void create() throws Exception {
        file.create();
        Mockito.verify(provider).create(Matchers.eq(file.getModel()));
    }

    @Test
    public void delete() throws Exception {
        file.delete();
        Mockito.verify(provider).delete(Matchers.eq(file.getModel()));
    }

    @Test
    public void exists() throws Exception {
        file.exists();
        Mockito.verify(provider).exists(Matchers.eq(file.getModel()));
    }

    @Override
    protected FileContext createFileContext() {
        return Mockito.mock(FileContext.class);
    }
}