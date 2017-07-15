/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.util.UrlUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractClientConfigurationTest {

    @BeforeClass
    public static void beforeTest() {
        UrlUtils.registerProtocolURLStreamHandlers();
    }

    @AfterClass
    public static void afterTest() {
        UrlUtils.unregisterProtocolURLStreamHandlers();
    }

}
