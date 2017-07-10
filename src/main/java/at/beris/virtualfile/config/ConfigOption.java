/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.config.value.AuthenticationType;

public enum ConfigOption {
    FILE_CACHE_SIZE(Integer.class),
    HOME(String.class, false),
    MASTER_PASSWORD(char[].class),
    KNOWN_HOSTS_FILE(String.class),
    TIMEOUT(Integer.class),
    STRICT_HOSTKEY_CHECKING(Boolean.class),
    AUTHENTICATION_TYPE(AuthenticationType.class),
    PRIVATE_KEY_FILE(String.class),
    USERNAME(String.class),
    PASSWORD(char[].class);

    private Class<?> configValueClass;

    private boolean isPersisted;

    ConfigOption(Class<?> configValueClass) {
        this.configValueClass = configValueClass;
        this.isPersisted = true;
    }

    ConfigOption(Class<?> configValueClass, boolean isPersisted) {
        this.configValueClass = configValueClass;
        this.isPersisted = isPersisted;
    }

    public Class<?> getConfigValueClass() {
        return configValueClass;
    }

    public boolean isPersisted() {
        return isPersisted;
    }
}
