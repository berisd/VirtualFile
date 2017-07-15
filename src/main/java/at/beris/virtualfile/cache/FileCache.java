/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.cache;

import at.beris.virtualfile.UrlFile;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileCache {
    public static final float LOAD_FACTOR = 0.75F;
    // Percent of cache entries purged when cache is full
    public static final float PURGE_FACTOR = 0.1F;
    private float loadFactor;
    private int capacity;
    private int maxSize;
    private CallbackHandler callbackHandler;

    private Map<String, UrlFile> cacheMap;

    public FileCache(int maxSize) {
        this.maxSize = maxSize;
        this.loadFactor = LOAD_FACTOR;
        calculateFields();
        createMap();
    }

    public UrlFile put(String key, UrlFile value) {
        if (isCacheFull()) {
            purgeCache();
        }
        cacheMap.put(key, value);
        return value;
    }

    public UrlFile remove(String key) {
        return cacheMap.remove(key);
    }

    public int size() {
        return cacheMap.size();
    }

    public UrlFile get(String key) {
        return cacheMap.get(key);
    }

    public void clear() {
        cacheMap.clear();
    }

    private void createMap() {
        cacheMap = new LinkedHashMap<>(capacity, loadFactor);
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        calculateFields();
        disposeMap();
        createMap();
    }

    public int getPurgeSize() {
        return (int) (maxSize * PURGE_FACTOR);
    }

    public void setCallbackHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    private void calculateFields() {
        this.capacity = (int) Math.ceil(maxSize / (100 * loadFactor)) + 1;
    }

    private void disposeMap() {
        cacheMap.clear();
        cacheMap = null;
    }

    private boolean isCacheFull() {
        return cacheMap.size() == maxSize;
    }

    private void purgeCache() {
        int numOfEntriesToPurge = getPurgeSize();
        int numOfEntriesPurged = 0;

        Iterator<Map.Entry<String, UrlFile>> it = cacheMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, UrlFile> entry = it.next();
            UrlFile file = entry.getValue();
            if (numOfEntriesPurged < numOfEntriesToPurge) {
                it.remove();
                callbackHandler.afterEntryPurged(file);
                numOfEntriesPurged++;
            }
        }
    }

    public interface CallbackHandler {
        void afterEntryPurged(UrlFile value);
    }
}
