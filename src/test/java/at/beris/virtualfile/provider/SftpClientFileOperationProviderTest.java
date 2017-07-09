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
import at.beris.virtualfile.TestHelper;
import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URISyntaxException;
import java.net.URL;

import static at.beris.virtualfile.TestHelper.*;
import static org.mockito.Mockito.never;

@RunWith(MockitoJUnitRunner.class)
public class SftpClientFileOperationProviderTest extends AbstractFileOperationProviderTest<SftpClientFileOperationProvider, SftpClient> {

    @Before
    @Override
    public void beforeTestCase() {
        super.beforeTestCase();

        client = Mockito.mock(SftpClient.class);
        provider = new SftpClientFileOperationProvider(fileContext, client);

        URL siteUrl = UrlUtils.newUrl("sftp://sshtest:" + readSftpPassword() + "@www.beris.at:22" + TestHelper.SSH_HOME_DIRECTORY);
        sourceFileUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_FILE_NAME);
        targetFileUrl = UrlUtils.newUrl(siteUrl, TestHelper.SSH_HOME_DIRECTORY + TEST_TARGET_FILE_NAME);
        sourceDirectoryUrl = UrlUtils.getUrlForLocalPath(TEST_SOURCE_DIRECTORY_NAME + "/");
        targetDirectoryUrl = UrlUtils.newUrl(siteUrl, TestHelper.SSH_HOME_DIRECTORY + TEST_TARGET_DIRECTORY_NAME + "/");
    }


    @Test
    public void createFile() throws URISyntaxException {
        FileModel fileModel = new FileModel();
        fileModel.setUrl(sourceFileUrl);
        provider.create(fileModel);

        Mockito.verify(client).createFile(Matchers.eq(sourceFileUrl.getPath()));
        Mockito.verify(client, never()).createDirectory(Matchers.anyString());
    }

    @Override
    protected void cleanupFiles() {

    }
}