/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContextConfigurationTest {

    public static final String KNOWN_HOSTS_FILE = "knownhosts";
    public static final String PRIVATE_KEY_FILE = "privkey";
    private ContextConfiguration config;

    @Before
    public void setUp() {
        config = new ContextConfiguration();
    }

    @Test
    public void setValues() {
        _setValues();
        assertEquals(Integer.valueOf(123), config.getFileCacheSize());
    }

    void _setValues() {
        config.setFileCacheSize(123);
    }
}