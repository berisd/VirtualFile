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
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.os.OsFamily;
import at.beris.virtualfile.provider.operation.FileOperationListener;
import at.beris.virtualfile.util.OsUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static at.beris.virtualfile.FileTestHelper.*;
import static at.beris.virtualfile.provider.operation.CopyFileOperation.STREAM_BUFFER_SIZE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.times;

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
