/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.config.value.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private Map<ConfigurationOption, ConfigValue> settings;
    private Configuration parentConfig;

    public Configuration() {
        settings = new HashMap<>();
    }

    public Configuration(Configuration parentConfig) {
        this();
        this.parentConfig = parentConfig;
    }

    public void initValues() {
        settings.put(ConfigurationOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(true));
        settings.put(ConfigurationOption.KNOWN_HOSTS_FILE, new StringConfigValue(System.getProperty("user.home") + File.separator + ".ssh" + File.separator + "known_hosts"));
        settings.put(ConfigurationOption.TIMEOUT, new IntegerConfigValue(10));
        settings.put(ConfigurationOption.AUTHENTICATION_TYPE, new EnumConfigValue(AuthenticationType.PASSWORD));
        settings.put(ConfigurationOption.PRIVATE_KEY_FILE, new StringConfigValue(""));
        settings.put(ConfigurationOption.USERNAME, new StringConfigValue(""));
        settings.put(ConfigurationOption.PASSWORD, new CharArrayConfigValue(new char[]{}));
    }

    public void remove(ConfigurationOption key) {
        settings.remove(key);
    }

    public ConfigValue get(ConfigurationOption key) {
        return settings.get(key);
    }

    public void set(ConfigurationOption key, ConfigValue value) {
        settings.put(key, value);
    }

    public Boolean isStrictHostKeyChecking() {
        ConfigValue configValue = settings.get(ConfigurationOption.STRICT_HOSTKEY_CHECKING);
        Boolean value = configValue != null ? (Boolean) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.isStrictHostKeyChecking() : null;
        }

        return value;
    }

    public Configuration setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        settings.put(ConfigurationOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(strictHostKeyChecking));
        return this;
    }

    public String getKnownHostsFile() {
        ConfigValue configValue = settings.get(ConfigurationOption.KNOWN_HOSTS_FILE);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getKnownHostsFile() : null;
        }

        return value;
    }

    public Configuration setKnownHostsFile(String knownHostsFile) {
        settings.put(ConfigurationOption.KNOWN_HOSTS_FILE, new StringConfigValue(knownHostsFile));
        return this;
    }

    public Integer getTimeOut() {
        ConfigValue configValue = settings.get(ConfigurationOption.TIMEOUT);
        Integer value = configValue != null ? (Integer) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getTimeOut() : null;
        }

        return value;
    }

    public Configuration setTimeOut(int timeout) {
        settings.put(ConfigurationOption.TIMEOUT, new IntegerConfigValue(timeout));
        return this;
    }

    public AuthenticationType getAuthenticationType() {
        ConfigValue configValue = settings.get(ConfigurationOption.AUTHENTICATION_TYPE);
        AuthenticationType value = configValue != null ? (AuthenticationType) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getAuthenticationType() : null;
        }

        return value;
    }

    public Configuration setAuthenticationType(AuthenticationType authenticationType) {
        settings.put(ConfigurationOption.AUTHENTICATION_TYPE, new EnumConfigValue(authenticationType));
        return this;
    }

    public String getPrivateKeyFile() {
        ConfigValue configValue = settings.get(ConfigurationOption.PRIVATE_KEY_FILE);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getPrivateKeyFile() : null;
        }

        return value;
    }

    public Configuration setPrivateKeyFile(String privateKeyFile) {
        settings.put(ConfigurationOption.PRIVATE_KEY_FILE, new StringConfigValue(privateKeyFile));
        return this;
    }

    public String getUsername() {
        ConfigValue configValue = settings.get(ConfigurationOption.USERNAME);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getUsername() : null;
        }

        return value;
    }

    public Configuration setUsername(String username) {
        settings.put(ConfigurationOption.USERNAME, new StringConfigValue(username));
        return this;
    }

    public char[] getPassword() {
        ConfigValue configValue = settings.get(ConfigurationOption.PASSWORD);
        char[] value = configValue != null ? (char[]) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getPassword() : null;
        }

        return value;
    }

    public Configuration setPassword(char[] password) {
        settings.put(ConfigurationOption.PASSWORD, new CharArrayConfigValue(password));
        return this;
    }

    public Configuration setPassword(String password) {
        settings.put(ConfigurationOption.PASSWORD, new CharArrayConfigValue(password.toCharArray()));
        return this;
    }
}
