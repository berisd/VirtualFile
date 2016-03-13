/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.protocol.Protocol;

public interface RemoteSite extends Site {
    Client getClient();

    void setClient(Client client);

    String getHost();

    void setHost(String host);

    String getUsername();

    void setUsername(String username);

    char[] getPassword();

    void setPassword(char[] password);

    int getPort();

    void setPort(int port);

    Protocol getProtocol();

    void setProtocol(Protocol protocol);
}
