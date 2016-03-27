/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later. 
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.config.value.ConfigValue;
import at.beris.virtualfile.config.value.IntegerConfigValue;

import java.util.HashMap;
import java.util.Map;

public class BaseConfig {
    private Map<BaseConfigOption, ConfigValue> settings;

    public BaseConfig() {
        settings = new HashMap<>();
    }

    public void initValues() {
        settings.put(BaseConfigOption.FILE_CACHE_SIZE, new IntegerConfigValue(4096));
    }

    public Integer getFileCacheSize() {
        ConfigValue configValue = settings.get(BaseConfigOption.FILE_CACHE_SIZE);
        return configValue != null ? (Integer) configValue.getValue() : null;
    }

    public BaseConfig setFileCacheSize(int fileCacheSize) {
        settings.put(BaseConfigOption.FILE_CACHE_SIZE, new IntegerConfigValue(fileCacheSize));
        return this;
    }
}
