/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static at.beris.virtualfile.FileTestHelper.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalFileOperationProviderTest extends AbstractFileOperationProviderTest<LocalFileOperationProvider, Client> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileOperationProviderTest.class);

    @Before
    @Override
    public void beforeTestCase() {
        super.beforeTestCase();

        client = Mockito.mock(Client.class);
        provider = new LocalFileOperationProvider(fileContext, client);

        sourceFileUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME);
        targetFileUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + "/");
        targetDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_TARGET_DIRECTORY_NAME + "/");
    }

    @Test
    public void createFile() throws URISyntaxException {
        FileModel fileModel = new FileModel();
        fileModel.setUrl(sourceFileUrl);
        provider.create(fileModel);
        Assert.assertTrue(new File(sourceFileUrl.toURI()).exists());
    }

    @Test
    public void createDirectory() throws URISyntaxException {
        FileModel fileModel = new FileModel();
        fileModel.setUrl(sourceDirectoryUrl);
        provider.create(fileModel);
        Assert.assertTrue(new File(sourceDirectoryUrl.toURI()).exists());
    }

    protected void cleanupFiles() {
        for (URL url : new URL[]{sourceFileUrl, targetFileUrl, sourceDirectoryUrl, targetDirectoryUrl}) {
            if (url != null) {
                try {
                    File file = null;
                    try {
                        file = new File(url.toURI());
                    } catch (URISyntaxException e) {
                        LOGGER.error("Exception", e);
                    }
                    if (file.exists())
                        file.delete();
                } catch (RuntimeException e) {
                    LOGGER.error("Exception occured", e);
                }
            }
        }
    }

}