/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.cache;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LRUMap<K, V> implements Map<K, V> {
    private float loadFactor;
    private int capacity;
    private int size;

    private Map<K, V> cacheMap;

    public LRUMap(int size) {
        this.size = size;
        this.loadFactor = 0.75F;
        this.capacity = (int) Math.ceil(size / loadFactor) + 1;

        cacheMap = new LinkedHashMap<K, V>(capacity, loadFactor, true) {
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LRUMap.this.size;
            }
        };
    }

    @Override
    public V put(K key, V value) {
        cacheMap.put(key, value);
        return value;
    }

    @Override
    public V remove(Object key) {
        V previousValue = cacheMap.get(key);
        if (previousValue != null) {
            cacheMap.remove(key);
            return previousValue;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet())
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
    public V get(Object key) {
        return cacheMap.get(key);
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return cacheMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return cacheMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return cacheMap.entrySet();
    }
}
