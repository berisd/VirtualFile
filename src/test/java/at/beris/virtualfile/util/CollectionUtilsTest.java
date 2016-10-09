/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtilsTest {
    @Test
    public void removeEntriesByValueFromMap() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "abc");
        map.put(2, "edf");
        map.put(3, "abc");
        CollectionUtils.removeEntriesByValueFromMap(map, "abc");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(map.get(2), "edf");
    }
}