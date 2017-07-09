/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import org.junit.Assert;
import org.junit.Test;

public class KeyStoreManagerTest {
    private static final char[] KEY_STORE_PASSWORD = {'f', 'o', 'o', 'b', 'a', 'r'};
    @Test
    public void createStore() {
        char[] password = new char[]{'t', 'e', 's', 't'};

        KeyStoreManager keyStoreManager = new KeyStoreManager(TestHelper.TEST_HOME_DIRECTORY, KEY_STORE_PASSWORD);
        String entryAlias = keyStoreManager.addPassword(password);
        keyStoreManager.save();

        KeyStoreManager keyStoreManager2 = new KeyStoreManager(TestHelper.TEST_HOME_DIRECTORY, KEY_STORE_PASSWORD);
        Assert.assertArrayEquals(password, keyStoreManager2.getPassword(entryAlias));
    }

}