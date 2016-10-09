/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.cache;

import at.beris.virtualfile.VirtualFile;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class FileCache implements Map<String, VirtualFile> {
    private float loadFactor;
    private int capacity;
    private int size;
    private FileCacheCallbackHandler callbackHandler;

    private Map<String, VirtualFile> cacheMap;

    public FileCache(int size) {
        this.size = size;
        this.loadFactor = 0.75F;
        calculateFields();
        createMap();
    }

    @Override
    public VirtualFile put(String key, VirtualFile value) {
        cacheMap.put(key, value);
        return value;
    }

    @Override
    public VirtualFile remove(Object key) {
        VirtualFile previousValue = cacheMap.get(key);
        if (previousValue != null) {
            cacheMap.remove(key);
            return previousValue;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ? extends VirtualFile> m) {
        for (Map.Entry<? extends String, ? extends VirtualFile> entry : m.entrySet())
            put(entry.getKey(), entry.getValue());
    }

    public int size() {
        return cacheMap.size();
    }

    @Override
    public boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return cacheMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cacheMap.containsValue(value);
    }

    @Override
    public VirtualFile get(Object key) {
        return cacheMap.get(key);
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }

    @Override
    public Set<String> keySet() {
        return cacheMap.keySet();
    }

    @Override
    public Collection<VirtualFile> values() {
        return cacheMap.values();
    }

    @Override
    public Set<Entry<String, VirtualFile>> entrySet() {
        return cacheMap.entrySet();
    }

    private void createMap() {
        cacheMap = new LinkedHashMap<String, VirtualFile>(capacity, loadFactor, true) {
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, VirtualFile> eldest) {
                boolean removeEntry = size() > FileCache.this.size;
                if (removeEntry)
                    callbackHandler.beforeEntryRemoved(eldest.getValue());
                return removeEntry;
            }
        };
    }

    public void setSize(int size) {
        this.size = size;
        calculateFields();
        disposeMap();
        createMap();
    }

    public void setCallbackHandler(FileCacheCallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    private void calculateFields() {
        this.capacity = (int) Math.ceil(size / loadFactor) + 1;
    }

    private void disposeMap() {
        cacheMap.clear();
        cacheMap = null;
    }
}
