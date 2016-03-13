/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ClientConfig {
    private Map<ClientConfigOption, ConfigValue> settings;

    public ClientConfig() {
        settings = new HashMap<>();
    }

    public void initValues() {
        settings.put(ClientConfigOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(true));
        settings.put(ClientConfigOption.KNOWN_HOSTS_FILE, new StringConfigValue(System.getProperty("user.home") + File.separator + ".ssh" + File.separator + "known_hosts"));
        settings.put(ClientConfigOption.TIMEOUT, new IntegerConfigValue(10));
        settings.put(ClientConfigOption.AUTHENTICATION_TYPE, new EnumConfigValue(AuthenticationType.PASSWORD));
        settings.put(ClientConfigOption.PRIVATE_KEY_FILE, new StringConfigValue(""));
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
        return (configValue != null) ? (Boolean) configValue.getValue() : null;
    }

    public ClientConfig setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        settings.put(ClientConfigOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(strictHostKeyChecking));
        return this;
    }

    public String getKnownHostsFile() {
        ConfigValue configValue = settings.get(ClientConfigOption.KNOWN_HOSTS_FILE);
        return configValue != null ? (String) configValue.getValue() : null;
    }

    public ClientConfig setKnownHostsFile(String knownHostsFile) {
        settings.put(ClientConfigOption.KNOWN_HOSTS_FILE, new StringConfigValue(knownHostsFile));
        return this;
    }

    public Integer getTimeOut() {
        ConfigValue configValue = settings.get(ClientConfigOption.TIMEOUT);
        return configValue != null ? (Integer) configValue.getValue() : null;
    }

    public ClientConfig setTimeOut(int timeout) {
        settings.put(ClientConfigOption.TIMEOUT, new IntegerConfigValue(timeout));
        return this;
    }

    public AuthenticationType getAuthenticationType() {
        ConfigValue configValue = settings.get(ClientConfigOption.AUTHENTICATION_TYPE);
        return configValue != null ? (AuthenticationType) configValue.getValue() : null;
    }

    public ClientConfig setAuthenticationType(AuthenticationType authenticationType) {
        settings.put(ClientConfigOption.AUTHENTICATION_TYPE, new EnumConfigValue(authenticationType));
        return this;
    }

    public String getPrivateKeyFile() {
        ConfigValue configValue = settings.get(ClientConfigOption.PRIVATE_KEY_FILE);
        return configValue != null ? (String) configValue.getValue() : null;
    }

    public ClientConfig setPrivateKeyFile(String privateKeyFile) {
        settings.put(ClientConfigOption.PRIVATE_KEY_FILE, new StringConfigValue(privateKeyFile));
        return this;
    }
}
