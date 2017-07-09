/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

public enum ConfigOption {
    FILE_CACHE_SIZE(Integer.class), HOME(String.class), MASTER_PASSWORD(char[].class);

    private Class<?> configValueClass;

    ConfigOption(Class<?> configValueClass) {
        this.configValueClass = configValueClass;
    }

    public Class<?> getConfigValueClass() {
        return configValueClass;
    }
}
