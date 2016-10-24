/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.util.UrlUtils;
import org.junit.BeforeClass;

import static at.beris.virtualfile.FileTestHelper.SEVEN_ZIP_FILENAME;

public class SevenZipFileArchiveTest extends AbstractFileArchiveTest {

    @BeforeClass
    public static void beforeTest() {
        sourceArchiveUrl = UrlUtils.getUrlForLocalPath(SEVEN_ZIP_FILENAME);
    }

}
