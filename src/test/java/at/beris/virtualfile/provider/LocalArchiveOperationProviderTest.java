/*
 * This file is part of JarCommander.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import org.junit.Test;

public class LocalArchiveOperationProviderTest {
    @Test
    public void testList() throws Exception {
        java.io.File file = new java.io.File("src/test/resources/testarchive.zip");

        FileModel fileModel = new FileModel();
        fileModel.setUrl(file.toURI().toURL());
        //TODO Rework
//        LocalArchiveOperationProvider provider = new LocalArchiveOperationProvider();

//        List<File> fileList = provider.list(null, fileModel, null);
//        Assert.assertTrue(fileList.size()>0);
    }
}