/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.protocol.Protocol;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class UrlFileManagerTest {
    @Test
    public void enabledProtocols() {
        Set<Protocol> expectedProtocols = new HashSet<>();
        expectedProtocols.add(Protocol.FILE);
        expectedProtocols.add(Protocol.FTP);
        expectedProtocols.add(Protocol.SFTP);

        UrlFileManager manager = new UrlFileManager();
        Set<Protocol> actualProtocols = manager.enabledProtocols();

        Assert.assertTrue(actualProtocols.containsAll(expectedProtocols));
        Assert.assertEquals(expectedProtocols.size(), actualProtocols.size());
    }
}
