/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.config.value.AuthenticationType;
import at.beris.virtualfile.protocol.Protocol;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class SimpleConfiguratorTest {
    public static final Integer FILE_CACHE_SIZE = 1024;
    public static final String KNOWN_HOSTS_FILE = "knownhostsfile";
    public static final String KNOWN_HOSTS_FILE2 = "knownhostsfile2";
    public static final String KNOWN_HOSTS_FILE3 = "knownhostsfile3";
    public static final String PRIVATE_KEY_FILE = "privatekeyfile";
    public static final String PRIVATE_KEY_FILE2 = "privatekeyfile2";
    public static final String PRIVATE_KEY_FILE3 = "privatekeyfile3";
    public static final String USERNAME1 = "user1";
    public static final String USERNAME2 = "user2";
    public static final String PASSWORD1 = "pwd1";
    public static final String PASSWORD2 = "pwd2";
    public static final Integer TIME_OUT = 111;
    public static final Integer TIME_OUT2 = 112;
    public static final Integer TIME_OUT3 = 113;

    private SimpleConfigurator config;

    @Before
    public void setUp() {
        config = new SimpleConfigurator(new Configurator());
        FileManager.registerProtocolURLStreamHandlers();
    }

    @Test
    public void setAndGetValues() throws Exception {
        URL url = new URL("file://test:pwd@site1.example.com:22/test.txt");
        setValues(url);

        assertEquals(FILE_CACHE_SIZE, config.getFileCacheSize());
        assertEquals(AuthenticationType.PASSWORD, config.getAuthenticationType());
        assertEquals(AuthenticationType.PUBLIC_KEY, config.getAuthenticationType(Protocol.SFTP));
        assertEquals(AuthenticationType.PUBLIC_KEY, config.getAuthenticationType(url));
        assertEquals(KNOWN_HOSTS_FILE, config.getKnownHostsFile());
        assertEquals(KNOWN_HOSTS_FILE2, config.getKnownHostsFile(Protocol.SFTP));
        assertEquals(KNOWN_HOSTS_FILE3, config.getKnownHostsFile(url));
        assertEquals(PRIVATE_KEY_FILE, config.getPrivateKeyFile());
        assertEquals(PRIVATE_KEY_FILE2, config.getPrivateKeyFile(Protocol.SFTP));
        assertEquals(PRIVATE_KEY_FILE3, config.getPrivateKeyFile(url));
        assertTrue(config.isStrictHostKeyChecking());
        assertFalse(config.isStrictHostKeyChecking(Protocol.SFTP));
        assertTrue(config.isStrictHostKeyChecking(url));
        assertEquals(TIME_OUT, config.getTimeOut());
        assertEquals(TIME_OUT2, config.getTimeOut(Protocol.SFTP));
        assertEquals(TIME_OUT3, config.getTimeOut(url));
        assertEquals(USERNAME1, config.getUsername());
        assertEquals(USERNAME2, config.getUsername(url));
        assertEquals(PASSWORD1, String.valueOf(config.getPassword()));
        assertEquals(PASSWORD2, String.valueOf(config.getPassword(url)));
    }

    @Test
    public void setAndGetValuesForDifferentProtocols() throws Exception {
        config.setTimeOut(TIME_OUT, Protocol.FILE);
        config.setTimeOut(TIME_OUT2, Protocol.SFTP);
        assertEquals(TIME_OUT, config.getTimeOut(Protocol.FILE));
        assertEquals(TIME_OUT2, config.getTimeOut(Protocol.SFTP));
    }

    @Test
    public void setAndGetValuesForDifferentSites() throws Exception {
        URL url = new URL("file://test:pwd@site1.example.com:22/test.txt");
        URL url2 = new URL("file://test:pwd@site2.example.com:22/test.txt");

        config.setTimeOut(TIME_OUT, url);
        config.setTimeOut(TIME_OUT2, url2);
        assertEquals(TIME_OUT, config.getTimeOut(url));
        assertEquals(TIME_OUT2, config.getTimeOut(url2));
    }

    private void setValues(URL url) {
        config.setFileCacheSize(FILE_CACHE_SIZE);
        config.setAuthenticationType(AuthenticationType.PASSWORD);
        config.setAuthenticationType(AuthenticationType.PUBLIC_KEY, Protocol.SFTP);
        config.setAuthenticationType(AuthenticationType.PUBLIC_KEY, url);
        config.setKnownHostsFile(KNOWN_HOSTS_FILE);
        config.setKnownHostsFile(KNOWN_HOSTS_FILE2, Protocol.SFTP);
        config.setKnownHostsFile(KNOWN_HOSTS_FILE3, url);
        config.setPrivateKeyFile(PRIVATE_KEY_FILE);
        config.setPrivateKeyFile(PRIVATE_KEY_FILE2, Protocol.SFTP);
        config.setPrivateKeyFile(PRIVATE_KEY_FILE3, url);
        config.setStrictHostKeyChecking(true);
        config.setStrictHostKeyChecking(false, Protocol.SFTP);
        config.setStrictHostKeyChecking(true, url);
        config.setTimeOut(TIME_OUT);
        config.setTimeOut(TIME_OUT2, Protocol.SFTP);
        config.setTimeOut(TIME_OUT3, url);
        config.setUsername(USERNAME1);
        config.setUsername(USERNAME2, url);
        config.setPassword(PASSWORD1);
        config.setPassword(PASSWORD2, url);
    }
}
