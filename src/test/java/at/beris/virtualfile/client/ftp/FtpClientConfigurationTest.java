/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.ftp;

import at.beris.virtualfile.client.ClientConfiguration;
import at.beris.virtualfile.protocol.Protocol;
import org.junit.Assert;
import org.junit.Test;

import static at.beris.virtualfile.client.ftp.FtpClientConfiguration.DEFAULT_USERNAME_FTP;

public class FtpClientConfigurationTest {
    @Test
    public void create() {
        FtpClientConfiguration ftpConfiguration = ClientConfiguration.createFtpConfiguration();
        Assert.assertEquals(DEFAULT_USERNAME_FTP, ftpConfiguration.getUsername());
        Assert.assertEquals(Protocol.FTP.getDefaultPort(), ftpConfiguration.getPort());
    }

}