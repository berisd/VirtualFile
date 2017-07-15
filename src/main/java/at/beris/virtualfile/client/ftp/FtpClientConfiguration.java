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

public class FtpClientConfiguration extends ClientConfiguration<FtpClientConfiguration> {

    public static final String DEFAULT_USERNAME_FTP = "anonymous";

    public FtpClientConfiguration() {
        super();
        setPort(Protocol.FTP.getDefaultPort());
        setUsername(DEFAULT_USERNAME_FTP);
    }
}
