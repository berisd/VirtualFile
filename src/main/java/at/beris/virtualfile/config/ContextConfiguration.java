/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.config.value.ConfigValue;
import at.beris.virtualfile.config.value.IntegerConfigValue;
import at.beris.virtualfile.config.value.StringConfigValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ContextConfiguration {
    private Map<ContextConfigurationOption, ConfigValue> settings;

    public ContextConfiguration() {
        settings = new HashMap<>();
    }

    public void initValues() {
        settings.put(ContextConfigurationOption.FILE_CACHE_SIZE, new IntegerConfigValue(10000));
        settings.put(ContextConfigurationOption.HOME, new StringConfigValue(System.getProperty("user.home") + File.separator + ".VirtualFile"));
    }

    public Integer getFileCacheSize() {
        ConfigValue configValue = settings.get(ContextConfigurationOption.FILE_CACHE_SIZE);
        return configValue != null ? (Integer) configValue.getValue() : null;
    }

    public ContextConfiguration setFileCacheSize(int fileCacheSize) {
        settings.put(ContextConfigurationOption.FILE_CACHE_SIZE, new IntegerConfigValue(fileCacheSize));
        return this;
    }

    public String getHome() {
        ConfigValue configValue = settings.get(ContextConfigurationOption.HOME);
        return configValue != null ? (String) configValue.getValue() : null;
    }

    public ContextConfiguration setHome(String path) {
        settings.put(ContextConfigurationOption.HOME, new StringConfigValue(path));
        return this;
    }
}
