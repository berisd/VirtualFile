/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.os.OsFamily;
import org.junit.Assert;
import org.junit.Test;

public class OsUtilsTest {

    @Test
    public void detectOSFamily() {
        String oldValue = System.getProperty("os.name");
        System.setProperty("os.name", "Linux");
        Assert.assertEquals(OsFamily.UNIX, OsUtils.detectOSFamily());
        System.setProperty("os.name", oldValue);
    }
}