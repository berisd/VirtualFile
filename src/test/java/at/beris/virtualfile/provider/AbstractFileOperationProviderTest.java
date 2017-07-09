/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.*;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.*;
import org.mockito.Mockito;

import java.net.URL;

public abstract class AbstractFileOperationProviderTest<P extends AbstractFileOperationProvider<C>, C extends Client> {

    protected UrlFileContext fileContext;
    protected P provider;
    protected C client;

    protected URL sourceFileUrl;
    protected URL targetFileUrl;
    protected URL sourceDirectoryUrl;
    protected URL targetDirectoryUrl;

    @BeforeClass
    public static void beforeTest() {
        UrlUtils.registerProtocolURLStreamHandlers();
    }

    @Before
    public void beforeTestCase() {
        fileContext = Mockito.mock(UrlFileContext.class);
    }

    @After
    public void afterTestCase() {
        cleanupFiles();
    }

    protected abstract void cleanupFiles();
}
