/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.mock.FileOperationProviderMock;
import at.beris.virtualfile.provider.FileOperationProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.util.Date;

import static at.beris.virtualfile.FileTestHelper.TEST_SOURCE_DIRECTORY_NAME;
import static at.beris.virtualfile.FileTestHelper.TEST_SOURCE_FILE_NAME;

public class UrlFileTest extends AbstractFileTest {
    private FileOperationProvider provider;

    private UrlFile file;

    @Before
    @Override
    public void beforeTestCase() throws Exception {
        super.beforeTestCase();
        provider = new FileOperationProviderMock();
        sourceFileUrl = new URL(String.format("file:/%s/%s", TEST_SOURCE_DIRECTORY_NAME, TEST_SOURCE_FILE_NAME));
        Mockito.when(getFileContext().getFileOperationProvider(sourceFileUrl.toString())).thenReturn(provider);
        file = new UrlFile(sourceFileUrl, getFileContext());
        Mockito.when(getFileContext().newFile(Matchers.eq(sourceFileUrl))).thenReturn(file);
        FileModel fileModel = createFileModel();
        Mockito.when(getFileContext().createFileModel()).thenReturn(fileModel);
    }

    @Test
    public void createFile() throws IOException {
        super.createFile();
    }

    @Test
    public void deleteFile() throws IOException {
        super.deleteFile();
    }


    @Override
    protected FileContext createFileContext() {
        return Mockito.mock(FileContext.class);
    }

    private FileModel createFileModel() {
        FileModel fileModel = new FileModel();
        fileModel.setOwner(Mockito.mock(UserPrincipal.class));
        fileModel.setCreationTime(FileTime.fromMillis(new Date().getTime()));
        fileModel.setLastModifiedTime(FileTime.fromMillis(new Date().getTime()));
        fileModel.setLastAccessTime(FileTime.fromMillis(new Date().getTime()));
        return fileModel;
    }

}