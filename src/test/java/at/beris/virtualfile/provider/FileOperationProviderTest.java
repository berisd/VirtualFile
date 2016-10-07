/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import static at.beris.virtualfile.TestFileHelper.initIntegrationTest;

@RunWith(Parameterized.class)
public class FileOperationProviderTest {

    private FileOperationProvider provider;
    private static Client client;
    private URL testUrl;

    @Parameters
    public static Collection<Object[]> data() throws Exception {
        UrlUtils.registerProtocolURLStreamHandlers();

        //TODO Make test with mocks
        return Arrays.asList(new Object[][]{
//                        {new File(TEST_SOURCE_FILE_NAME).toURI().toURL(), new LocalFileOperationProvider(), null},
//                        {new URL("sftp://sshtest:password@www.beris.at:22/home/sshtest/" + TEST_SOURCE_FILE_NAME), new SftpClientFileOperationProvider(), createSftpClient(new URL("sftp://sshtest:password@www.beris.at:22/home/sshtest/" + TEST_SOURCE_FILE_NAME))}
                }
        );
    }

    public FileOperationProviderTest(URL url, FileOperationProvider fileOperationProvider, Client client) {
        this.provider = fileOperationProvider;
        this.client = client;
        this.testUrl = url;
    }

    @BeforeClass
    public static void setUp() throws Exception {
        initIntegrationTest();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (client != null)
            client.disconnect();
    }

    @Test
    public void testCreate() throws Exception {
        FileModel model = createModelFromUrl(testUrl);
        provider.create(model);
        Assert.assertTrue(provider.exists(model));
        provider.delete(model);
    }

    @Test
    public void testExists() throws Exception {
        FileModel model = createModelFromUrl(testUrl);
//        provider.create(client, model);
//        Assert.assertTrue(provider.exists(client, model));
//        provider.delete(client, model);
    }

    @Test
    public void testDelete() throws Exception {
        FileModel model = createModelFromUrl(testUrl);
//        provider.create(client, model);
//        Assert.assertTrue(provider.exists(client, model));
//        provider.delete(client, model);
//        Assert.assertFalse(provider.exists(client, model));
    }

    @Test
    public void testList() throws Exception {

    }

    @Test
    public void testCopy() throws Exception {

    }

    @Test
    public void testAdd() throws Exception {

    }

    @Test
    public void testUpdateModel() throws Exception {

    }

    @Test
    public void testSave() throws Exception {

    }

    private FileModel createModelFromUrl(URL url) {
        FileModel model = new FileModel();
        model.setUrl(url);
        return model;
    }
}