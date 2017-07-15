/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.client.sftp.SftpClientConfiguration;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlFileContextTest {

    private UrlFileContext fileContext;

    @Before
    public void beforeTestCase() {
        UrlUtils.registerProtocolURLStreamHandlers();
        fileContext = new UrlFileContext(Configuration.create().setFileCacheSize(10), SiteManager.create());
    }

    @Test
    public void getParentFile() throws MalformedURLException {
        fileContext.resolveFile(new URL("file:/this/is/a/file/test"));
        VirtualFile parentFile1 = fileContext.resolveFile(new URL("file:/this/"));
        VirtualFile parentFile2 = fileContext.resolveFile(new URL("file:/this/is/")).getParent();
        Assert.assertSame(parentFile1, parentFile2);
        fileContext.resolveFile(new URL("file:/this/here/is/another/file/test"));
        VirtualFile parentFile3 = fileContext.resolveFile(new URL("file:/this/"));
        Assert.assertNotSame(parentFile1, parentFile3);
    }

    @Test
    public void createClientInstance() throws MalformedURLException {
        Client client = fileContext.createClientInstance(new URL("sftp://www.example.com/test.file"));
        Assert.assertTrue(client instanceof SftpClient);
        Assert.assertTrue(client.getConfiguration() instanceof SftpClientConfiguration);
    }

    @Test
    public void createClientConfiguration() throws MalformedURLException {
        fileContext.createClientConfiguration(new URL("sftp://www.example.com/test.file"), SftpClientConfiguration.class);
    }

    @Test
    public void newRootFile() throws MalformedURLException {
        VirtualFile virtualFile = fileContext.resolveFile(new URL("file:/"));
        Assert.assertNotNull(virtualFile);
    }
}