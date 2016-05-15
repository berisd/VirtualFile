/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LRUMapTest {
    private LRUMap<String, Integer> cache;

    @Before
    public void setup() {
        cache = new LRUMap<>(3);
    }

    @Test
    public void addEntriesUpToSize() {
        cache.put("one", 1);
        cache.put("two", 2);
        cache.put("three", 3);

        Assert.assertEquals(3, cache.size());
        Assert.assertTrue(cache.containsKey("one"));
        Assert.assertTrue(cache.containsKey("two"));
        Assert.assertTrue(cache.containsKey("three"));
    }

    @Test
    public void addEntriesOverSize() {
        cache.put("one", 1);
        cache.put("two", 2);
        cache.put("three", 3);
        cache.put("four", 4);

        Assert.assertEquals(3, cache.size());
        Assert.assertTrue(cache.containsKey("two"));
        Assert.assertTrue(cache.containsKey("three"));
        Assert.assertTrue(cache.containsKey("four"));
    }

    @Test
    public void removeEntry() {
        cache.put("one", 1);
        cache.put("two", 2);

        Assert.assertEquals(2, cache.size());

        cache.remove("one");

        Assert.assertEquals(1, cache.size());
        Assert.assertFalse(cache.containsKey("one"));
        Assert.assertTrue(cache.containsKey("two"));
    }
}