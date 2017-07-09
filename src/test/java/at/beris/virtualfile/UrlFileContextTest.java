/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.config.Configurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlFileContextTest {

    private UrlFileContext fileContext;

    @Before
    public void beforeTestCase() {
        Configurator configurator = new Configurator();
        configurator.setFileCacheSize(10);
        fileContext = new UrlFileContext(configurator);
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
    public void newRootFile() throws MalformedURLException {
        VirtualFile virtualFile = fileContext.resolveFile(new URL("file:/"));
        Assert.assertNotNull(virtualFile);
    }
}