/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import java.util.Iterator;
import java.util.Map;

public class CollectionUtils {
    public static <K, V> void removeEntriesByValueFromMap(Map<K, V> map, V value) {
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            if (entry.getValue().equals(value))
                it.remove();
        }
    }
}
