/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.util.DateUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;

import static at.beris.virtualfile.FileTestHelper.initIntegrationTest;

public class HttpFileTest extends AbstractUrlFileTest {

    @BeforeClass
    public static void beforeTest() throws Exception {
        initIntegrationTest();
    }

    @Before
    public void beforeTestCase() {
        super.beforeTestCase();
        targetFileUrl = UrlUtils.getUrlForLocalPath("testimage.jpg");
    }


    @Test(expected = OperationNotSupportedException.class)
    public void createFile() {
        throw new OperationNotSupportedException();
    }

    @Test
    public void copyFile() {
        try {
            VirtualFile sourceFile = getFileContext().newFile(new URL("https://images-3.gog.com/d6fe0ebe40ef6117c1c8979c00777ce64f8a521302fd95ec13afdb8ebfa4349a_196.jpg"));
            VirtualFile targetFile = getFileContext().newFile(targetFileUrl);
            CopyListener copyListenerMock = Mockito.mock(CopyListener.class);
            sourceFile.copy(targetFile, copyListenerMock);
            Assert.assertTrue(targetFile.getSize() > 0);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getFile() {
        VirtualFile file = FileManager.newFile("http://www.beris.at/images/d6fe0ebe40ef6117c1c8979c00777ce64f8a521302fd95ec13afdb8ebfa4349a_196.jpg");
        Assert.assertEquals(11069, file.getSize());
        Assert.assertEquals(file.getLastModifiedTime().toInstant(), DateUtils.getLocalDateTimeFromInstant(LocalDateTime.of(2016, 02, 21, 22, 53, 20)));
        Assert.assertEquals("image/jpeg", file.getContentType().toString());
    }

    @Override
    protected UrlFileContext createFileContext() {
        return new UrlFileContext();
    }
}
