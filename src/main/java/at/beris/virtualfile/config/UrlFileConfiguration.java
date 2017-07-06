/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.config.value.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UrlFileConfiguration {
    private Map<UrlFileConfigurationOption, ConfigValue> settings;
    private UrlFileConfiguration parentConfig;

    public UrlFileConfiguration() {
        settings = new HashMap<>();
    }

    public UrlFileConfiguration(UrlFileConfiguration parentConfig) {
        this();
        this.parentConfig = parentConfig;
    }

    public void initValues() {
        settings.put(UrlFileConfigurationOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(true));
        settings.put(UrlFileConfigurationOption.KNOWN_HOSTS_FILE, new StringConfigValue(System.getProperty("user.home") + File.separator + ".ssh" + File.separator + "known_hosts"));
        settings.put(UrlFileConfigurationOption.TIMEOUT, new IntegerConfigValue(10));
        settings.put(UrlFileConfigurationOption.AUTHENTICATION_TYPE, new EnumConfigValue(AuthenticationType.PASSWORD));
        settings.put(UrlFileConfigurationOption.PRIVATE_KEY_FILE, new StringConfigValue(""));
        settings.put(UrlFileConfigurationOption.USERNAME, new StringConfigValue(""));
        settings.put(UrlFileConfigurationOption.PASSWORD, new CharArrayConfigValue(new char[]{}));
    }

    public void remove(UrlFileConfigurationOption key) {
        settings.remove(key);
    }

    public ConfigValue get(UrlFileConfigurationOption key) {
        return settings.get(key);
    }

    public void set(UrlFileConfigurationOption key, ConfigValue value) {
        settings.put(key, value);
    }

    public Boolean isStrictHostKeyChecking() {
        ConfigValue configValue = settings.get(UrlFileConfigurationOption.STRICT_HOSTKEY_CHECKING);
        Boolean value = configValue != null ? (Boolean) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.isStrictHostKeyChecking() : null;
        }

        return value;
    }

    public UrlFileConfiguration setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        settings.put(UrlFileConfigurationOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(strictHostKeyChecking));
        return this;
    }

    public String getKnownHostsFile() {
        ConfigValue configValue = settings.get(UrlFileConfigurationOption.KNOWN_HOSTS_FILE);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getKnownHostsFile() : null;
        }

        return value;
    }

    public UrlFileConfiguration setKnownHostsFile(String knownHostsFile) {
        settings.put(UrlFileConfigurationOption.KNOWN_HOSTS_FILE, new StringConfigValue(knownHostsFile));
        return this;
    }

    public Integer getTimeOut() {
        ConfigValue configValue = settings.get(UrlFileConfigurationOption.TIMEOUT);
        Integer value = configValue != null ? (Integer) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getTimeOut() : null;
        }

        return value;
    }

    public UrlFileConfiguration setTimeOut(int timeout) {
        settings.put(UrlFileConfigurationOption.TIMEOUT, new IntegerConfigValue(timeout));
        return this;
    }

    public AuthenticationType getAuthenticationType() {
        ConfigValue configValue = settings.get(UrlFileConfigurationOption.AUTHENTICATION_TYPE);
        AuthenticationType value = configValue != null ? (AuthenticationType) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getAuthenticationType() : null;
        }

        return value;
    }

    public UrlFileConfiguration setAuthenticationType(AuthenticationType authenticationType) {
        settings.put(UrlFileConfigurationOption.AUTHENTICATION_TYPE, new EnumConfigValue(authenticationType));
        return this;
    }

    public String getPrivateKeyFile() {
        ConfigValue configValue = settings.get(UrlFileConfigurationOption.PRIVATE_KEY_FILE);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getPrivateKeyFile() : null;
        }

        return value;
    }

    public UrlFileConfiguration setPrivateKeyFile(String privateKeyFile) {
        settings.put(UrlFileConfigurationOption.PRIVATE_KEY_FILE, new StringConfigValue(privateKeyFile));
        return this;
    }

    public String getUsername() {
        ConfigValue configValue = settings.get(UrlFileConfigurationOption.USERNAME);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getUsername() : null;
        }

        return value;
    }

    public UrlFileConfiguration setUsername(String username) {
        settings.put(UrlFileConfigurationOption.USERNAME, new StringConfigValue(username));
        return this;
    }

    public char[] getPassword() {
        ConfigValue configValue = settings.get(UrlFileConfigurationOption.PASSWORD);
        char[] value = configValue != null ? (char[]) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getPassword() : null;
        }

        return value;
    }

    public UrlFileConfiguration setPassword(char[] password) {
        settings.put(UrlFileConfigurationOption.PASSWORD, new CharArrayConfigValue(password));
        return this;
    }

    public UrlFileConfiguration setPassword(String password) {
        settings.put(UrlFileConfigurationOption.PASSWORD, new CharArrayConfigValue(password.toCharArray()));
        return this;
    }
}
