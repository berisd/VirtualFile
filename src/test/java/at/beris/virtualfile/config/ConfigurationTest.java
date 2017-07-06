/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.config.value.AuthenticationType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationTest {

    private static final String KNOWN_HOSTS_FILE = "knownhosts";
    private static final String PRIVATE_KEY_FILE = "privkey";
    private static final String USERNAME = "user1";
    private static final String PASSWORD = "password1";

    private UrlFileConfiguration config;

    @Before
    public void setUp() {
        config = new UrlFileConfiguration();
    }

    @Test
    public void setValues() {
        _setValues();
        assertEquals(AuthenticationType.PASSWORD, config.getAuthenticationType());
        assertTrue(config.isStrictHostKeyChecking());
        assertEquals(60, (int) config.getTimeOut());
        assertEquals(KNOWN_HOSTS_FILE, config.getKnownHostsFile());
        assertEquals(PRIVATE_KEY_FILE, config.getPrivateKeyFile());
        assertEquals(PRIVATE_KEY_FILE, config.getPrivateKeyFile());
        assertEquals(USERNAME, config.getUsername());
        assertEquals(PASSWORD, String.valueOf(config.getPassword()));
    }

    @Test
    public void removeValues() {
        _setValues();
        config.remove(UrlFileConfigurationOption.AUTHENTICATION_TYPE);
        assertNull(config.getAuthenticationType());
        config.remove(UrlFileConfigurationOption.STRICT_HOSTKEY_CHECKING);
        assertNull(config.isStrictHostKeyChecking());
        config.remove(UrlFileConfigurationOption.TIMEOUT);
        assertNull(config.getTimeOut());
        config.remove(UrlFileConfigurationOption.KNOWN_HOSTS_FILE);
        assertNull(config.getKnownHostsFile());
        config.remove(UrlFileConfigurationOption.PRIVATE_KEY_FILE);
        assertNull(config.getPrivateKeyFile());
        config.remove(UrlFileConfigurationOption.USERNAME);
        assertNull(config.getUsername());
        config.remove(UrlFileConfigurationOption.PASSWORD);
        assertNull(config.getPassword());
    }

    void _setValues() {
        config.setAuthenticationType(AuthenticationType.PASSWORD);
        config.setStrictHostKeyChecking(true);
        config.setTimeOut(60);
        config.setKnownHostsFile(KNOWN_HOSTS_FILE);
        config.setPrivateKeyFile(PRIVATE_KEY_FILE);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD.toCharArray());
    }
}