/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.config.value.*;
import at.beris.virtualfile.crypto.EncoderDecoder;
import at.beris.virtualfile.crypto.PasswordEncoderDecoder;
import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.util.CharUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Configuration {

    private static final String SETTINGS_FILENAME = "configuration.ini";

    private static final int MASTER_PASSWORD_LENGTH = 20;

    private Map<ConfigOption, ConfigValue> configurationMap;

    private CallbackHandler callbackHandler;

    private EncoderDecoder passwordEncoderDecoder;

    private Configuration() {
        configurationMap = new LinkedHashMap<>();
        callbackHandler = new EmptySettingsBackHandler();
        passwordEncoderDecoder = new PasswordEncoderDecoder();
        initDefaultSettings();

        File settingsFile = getConfigurationPath().toFile();

        if (!settingsFile.exists()) {
            setMasterPassword(CharUtils.generateCharSequence(MASTER_PASSWORD_LENGTH));
        }
    }

    private Path getConfigurationPath() {
        return Paths.get(getHome(), SETTINGS_FILENAME);
    }

    public static Configuration create() {
        return new Configuration();
    }


    public String getHome() {
        return ((StringConfigValue) configurationMap.get(ConfigOption.HOME)).getValue();

    }

    public Configuration setHome(String path) {
        configurationMap.put(ConfigOption.HOME, new StringConfigValue(path));
        return this;
    }

    public int getFileCacheSize() {
        return ((IntegerConfigValue) configurationMap.get(ConfigOption.FILE_CACHE_SIZE)).getValue();
    }

    public Configuration setFileCacheSize(int size) {
        configurationMap.put(ConfigOption.FILE_CACHE_SIZE, new IntegerConfigValue(size));
        callbackHandler.changedFileCacheSize(size);
        return this;
    }

    public char[] getMasterPassword() {
        return passwordEncoderDecoder.decode(((CharArrayConfigValue) configurationMap.get(ConfigOption.MASTER_PASSWORD)).getValue());
    }

    public Configuration setMasterPassword(char[] password) {
        configurationMap.put(ConfigOption.MASTER_PASSWORD, new CharArrayConfigValue(passwordEncoderDecoder.encode(password)));
        return this;
    }

    public Boolean isStrictHostKeyChecking() {
        return ((BooleanConfigValue) configurationMap.get(ConfigOption.STRICT_HOSTKEY_CHECKING)).getValue();
    }

    public Configuration setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        configurationMap.put(ConfigOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(strictHostKeyChecking));
        return this;
    }

    public String getKnownHostsFile() {
        return ((StringConfigValue) configurationMap.get(ConfigOption.KNOWN_HOSTS_FILE)).getValue();
    }

    public Configuration setKnownHostsFile(String knownHostsFile) {
        configurationMap.put(ConfigOption.KNOWN_HOSTS_FILE, new StringConfigValue(knownHostsFile));
        return this;
    }

    public Integer getTimeOut() {
        return ((IntegerConfigValue) configurationMap.get(ConfigOption.TIMEOUT)).getValue();
    }

    public Configuration setTimeOut(int timeout) {
        configurationMap.put(ConfigOption.TIMEOUT, new IntegerConfigValue(timeout));
        return this;
    }

    public AuthenticationType getAuthenticationType() {
        return (AuthenticationType) ((EnumConfigValue) configurationMap.get(ConfigOption.AUTHENTICATION_TYPE)).getValue();

    }

    public Configuration setAuthenticationType(AuthenticationType authenticationType) {
        configurationMap.put(ConfigOption.AUTHENTICATION_TYPE, new EnumConfigValue(authenticationType));
        return this;
    }

    public String getPrivateKeyFile() {
        return ((StringConfigValue) configurationMap.get(ConfigOption.PRIVATE_KEY_FILE)).getValue();
    }

    public Configuration setPrivateKeyFile(String privateKeyFile) {
        configurationMap.put(ConfigOption.PRIVATE_KEY_FILE, new StringConfigValue(privateKeyFile));
        return this;
    }

    public String getUsername() {
        return ((StringConfigValue) configurationMap.get(ConfigOption.USERNAME)).getValue();
    }

    public Configuration setUsername(String username) {
        configurationMap.put(ConfigOption.USERNAME, new StringConfigValue(username));
        return this;
    }

    public char[] getPassword() {
        return ((CharArrayConfigValue) configurationMap.get(ConfigOption.PASSWORD)).getValue();
    }

    public Configuration setPassword(char[] password) {
        configurationMap.put(ConfigOption.PASSWORD, new CharArrayConfigValue(password));
        return this;
    }

    public Configuration setPassword(String password) {
        configurationMap.put(ConfigOption.PASSWORD, new CharArrayConfigValue(password.toCharArray()));
        return this;
    }

    public void setCallbackHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    private void initDefaultSettings() {
        configurationMap.put(ConfigOption.FILE_CACHE_SIZE, new IntegerConfigValue(10000));
        configurationMap.put(ConfigOption.HOME, new StringConfigValue(System.getProperty("user.home") + File.separator + ".VirtualFile"));
        configurationMap.put(ConfigOption.STRICT_HOSTKEY_CHECKING, new BooleanConfigValue(true));
        configurationMap.put(ConfigOption.KNOWN_HOSTS_FILE, new StringConfigValue(System.getProperty("user.home") + File.separator + ".ssh" + File.separator + "known_hosts"));
        configurationMap.put(ConfigOption.TIMEOUT, new IntegerConfigValue(10));
        configurationMap.put(ConfigOption.AUTHENTICATION_TYPE, new EnumConfigValue(AuthenticationType.PASSWORD));
        configurationMap.put(ConfigOption.PRIVATE_KEY_FILE, new StringConfigValue(""));
        configurationMap.put(ConfigOption.USERNAME, new StringConfigValue(""));
        configurationMap.put(ConfigOption.PASSWORD, new CharArrayConfigValue(new char[]{}));
    }

    public void load() {
        if (!getConfigurationPath().toFile().exists())
            return;

        try {
            //TODO To make the masterpassword more secure you shouldn't use strings but bytes
            for (String line : Files.readAllLines(getConfigurationPath())) {
                int posKeyValueSeperator = line.indexOf('=');
                String key = line.substring(0, posKeyValueSeperator).trim().toUpperCase();
                String value = line.substring(posKeyValueSeperator + 1).trim();
                ConfigOption configOption = ConfigOption.valueOf(key);
                setSetting(configOption, value);
            }
        } catch (IOException e) {
            new VirtualFileException(e);
        }
    }

    public void save() {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(getConfigurationPath().toFile()));
            for (Map.Entry<ConfigOption, ConfigValue> entry : configurationMap.entrySet()) {
                ConfigOption configOption = entry.getKey();
                if (!configOption.isPersisted())
                    continue;

                ConfigValue configValue = entry.getValue();
                String value;
                if (configValue instanceof CharArrayConfigValue)
                    value = String.valueOf((char[]) configValue.getValue());
                else
                    value = configValue.getValue().toString();
                pw.printf("%s = %s", configOption, value);
                pw.println();
            }
            pw.close();
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    private void setSetting(ConfigOption configOption, String stringValue) {
        ConfigValue configValue = createConfigValue(configOption.getConfigValueClass(), stringValue);
        configurationMap.put(configOption, configValue);
    }

    private ConfigValue createConfigValue(Class<?> clazz, String stringValue) {
        if (clazz.equals(Integer.class)) {
            return new IntegerConfigValue(Integer.valueOf(stringValue));
        } else if (clazz.equals(char[].class)) {
            return new CharArrayConfigValue(stringValue.toCharArray());
        } else if (clazz.equals(String.class)) {
            return new StringConfigValue(stringValue);
        } else if (clazz.equals(Boolean.class)) {
            return new BooleanConfigValue(Boolean.valueOf((stringValue)));
        } else if (clazz.equals(AuthenticationType.class)) {
            return new EnumConfigValue(AuthenticationType.valueOf(stringValue));
        } else {
            throw new VirtualFileException(Message.UNKNOWN_CONFIG_VALUE_CLASS());
        }
    }

    private class EmptySettingsBackHandler implements CallbackHandler {

        @Override
        public void changedFileCacheSize(int newSize) {

        }
    }

    public interface CallbackHandler {
        void changedFileCacheSize(int newSize);
    }
}
