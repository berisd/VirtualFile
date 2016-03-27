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

public class ClientConfig {
    private Map<ClientConfigOption, ConfigValue> settings;
    private ClientConfig parentConfig;

    public ClientConfig() {
        settings = new HashMap<>();
    }

    public ClientConfig(ClientConfig parentConfig) {
        this();
        this.parentConfig = parentConfig;
    }

    public void initValues() {
        settings.put(ClientConfigOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(true));
        settings.put(ClientConfigOption.KNOWN_HOSTS_FILE, new StringConfigValue(System.getProperty("user.home") + File.separator + ".ssh" + File.separator + "known_hosts"));
        settings.put(ClientConfigOption.TIMEOUT, new IntegerConfigValue(10));
        settings.put(ClientConfigOption.AUTHENTICATION_TYPE, new EnumConfigValue(AuthenticationType.PASSWORD));
        settings.put(ClientConfigOption.PRIVATE_KEY_FILE, new StringConfigValue(""));
        settings.put(ClientConfigOption.USERNAME, new StringConfigValue(""));
        settings.put(ClientConfigOption.PASSWORD, new CharArrayConfigValue(new char[]{}));
    }

    public void remove(ClientConfigOption key) {
        settings.remove(key);
    }

    public ConfigValue get(ClientConfigOption key) {
        return settings.get(key);
    }

    public void set(ClientConfigOption key, ConfigValue value) {
        settings.put(key, value);
    }

    public Boolean isStrictHostKeyChecking() {
        ConfigValue configValue = settings.get(ClientConfigOption.STRICT_HOSTKEY_CHECKING);
        Boolean value = configValue != null ? (Boolean) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.isStrictHostKeyChecking() : null;
        }

        return value;
    }

    public ClientConfig setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        settings.put(ClientConfigOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(strictHostKeyChecking));
        return this;
    }

    public String getKnownHostsFile() {
        ConfigValue configValue = settings.get(ClientConfigOption.KNOWN_HOSTS_FILE);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getKnownHostsFile() : null;
        }

        return value;
    }

    public ClientConfig setKnownHostsFile(String knownHostsFile) {
        settings.put(ClientConfigOption.KNOWN_HOSTS_FILE, new StringConfigValue(knownHostsFile));
        return this;
    }

    public Integer getTimeOut() {
        ConfigValue configValue = settings.get(ClientConfigOption.TIMEOUT);
        Integer value = configValue != null ? (Integer) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getTimeOut() : null;
        }

        return value;
    }

    public ClientConfig setTimeOut(int timeout) {
        settings.put(ClientConfigOption.TIMEOUT, new IntegerConfigValue(timeout));
        return this;
    }

    public AuthenticationType getAuthenticationType() {
        ConfigValue configValue = settings.get(ClientConfigOption.AUTHENTICATION_TYPE);
        AuthenticationType value = configValue != null ? (AuthenticationType) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getAuthenticationType() : null;
        }

        return value;
    }

    public ClientConfig setAuthenticationType(AuthenticationType authenticationType) {
        settings.put(ClientConfigOption.AUTHENTICATION_TYPE, new EnumConfigValue(authenticationType));
        return this;
    }

    public String getPrivateKeyFile() {
        ConfigValue configValue = settings.get(ClientConfigOption.PRIVATE_KEY_FILE);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getPrivateKeyFile() : null;
        }

        return value;
    }

    public ClientConfig setPrivateKeyFile(String privateKeyFile) {
        settings.put(ClientConfigOption.PRIVATE_KEY_FILE, new StringConfigValue(privateKeyFile));
        return this;
    }

    public String getUsername() {
        ConfigValue configValue = settings.get(ClientConfigOption.USERNAME);
        String value = configValue != null ? (String) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getUsername() : null;
        }

        return value;
    }

    public ClientConfig setUsername(String username) {
        settings.put(ClientConfigOption.USERNAME, new StringConfigValue(username));
        return this;
    }

    public char[] getPassword() {
        ConfigValue configValue = settings.get(ClientConfigOption.PASSWORD);
        char[] value = configValue != null ? (char[]) configValue.getValue() : null;

        if (value == null) {
            value = parentConfig != null ? parentConfig.getPassword() : null;
        }

        return value;
    }

    public ClientConfig setPassword(char[] password) {
        settings.put(ClientConfigOption.PASSWORD, new CharArrayConfigValue(password));
        return this;
    }
}
