/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientConfigTest {

    public static final String KNOWN_HOSTS_FILE = "knownhosts";
    public static final String PRIVATE_KEY_FILE = "privkey";
    private ClientConfig config;

    @Before
    public void setUp() {
        config = new ClientConfig();
    }

    @Test
    public void setValues() {
        _setValues();
        assertEquals(AuthenticationType.PASSWORD, config.getAuthenticationType());
        assertTrue(config.isStrictHostKeyChecking());
        assertEquals(60, (int) config.getTimeOut());
        assertEquals(KNOWN_HOSTS_FILE, config.getKnownHostsFile());
        assertEquals(PRIVATE_KEY_FILE, config.getPrivateKeyFile());
    }

    @Test
    public void removeValues() {
        _setValues();
        config.remove(ClientConfigOption.AUTHENTICATION_TYPE);
        assertNull(config.getAuthenticationType());
        config.remove(ClientConfigOption.STRICT_HOSTKEY_CHECKING);
        assertNull(config.isStrictHostKeyChecking());
        config.remove(ClientConfigOption.TIMEOUT);
        assertNull(config.getTimeOut());
        config.remove(ClientConfigOption.KNOWN_HOSTS_FILE);
        assertNull(config.getKnownHostsFile());
        config.remove(ClientConfigOption.PRIVATE_KEY_FILE);
        assertNull(config.getPrivateKeyFile());
    }

    void _setValues() {
        config.setAuthenticationType(AuthenticationType.PASSWORD);
        config.setStrictHostKeyChecking(true);
        config.setTimeOut(60);
        config.setKnownHostsFile(KNOWN_HOSTS_FILE);
        config.setPrivateKeyFile(PRIVATE_KEY_FILE);
    }
}