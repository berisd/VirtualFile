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
    @Test
    public void createStore() {
        char[] password = new char[]{'t', 'e', 's', 't'};

        Configuration configuration = TestHelper.createConfiguration();


        KeyStoreManager keyStoreManager = KeyStoreManager.create(configuration);
        String entryAlias = keyStoreManager.addPassword(password);
        keyStoreManager.save();

        KeyStoreManager keyStoreManager2 = KeyStoreManager.create(configuration);
        keyStoreManager2.load();
        Assert.assertArrayEquals(password, keyStoreManager2.getPassword(entryAlias));
    }

}