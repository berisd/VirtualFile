/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.cache.FileCache;
import at.beris.virtualfile.cache.FileCacheCallbackHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FileCacheTest {

    public static final int CACHE_SIZE = 100;
    public static final String TEST_URL = "file://test.txt";
    private FileCache fileCache;

    @Mock
    private FileCacheCallbackHandler callbackHandlerMock;

    @Before
    public void setUp() {
        fileCache = new FileCache(CACHE_SIZE);
        fileCache.setCallbackHandler(callbackHandlerMock);
    }

    @Test
    public void putEntry() {
        putValue(TEST_URL, Mockito.mock(VirtualFile.class));
        Assert.assertEquals(1, fileCache.size());
    }

    @Test
    public void removeEntry() {
        putValue(TEST_URL, Mockito.mock(VirtualFile.class));
        removeValue(TEST_URL);
        Assert.assertEquals(0, fileCache.size());
    }

    @Test
    public void purgeCache() {
        List<VirtualFile> entries = new ArrayList<>();
        for (int i = 0; i < CACHE_SIZE + 1; i++) {
            VirtualFile file = Mockito.mock(VirtualFile.class);
            entries.add(file);
            putValue(TEST_URL + String.valueOf(i), file);
        }

        Assert.assertEquals(91, fileCache.size());
        verify(callbackHandlerMock, times(10)).beforeEntryRemoved(any(VirtualFile.class));
        assertOldestEntriesNotExist();
        newestNewestEntriesExist();
    }

    private void assertOldestEntriesNotExist() {
        for (int i = 0; i < 10; i++) {
            Assert.assertNull(fileCache.get(TEST_URL + String.valueOf(i)));
        }
    }

    private void newestNewestEntriesExist() {
        for (int i = 90; i < 100; i++) {
            Assert.assertNotNull(fileCache.get(TEST_URL + String.valueOf(i)));
        }
    }

    private void putValue(String key, VirtualFile file) {
        fileCache.put(key, file);
    }

    private void removeValue(String key) {
        fileCache.remove(key);
    }
}