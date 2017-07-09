/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.client.ftp.FtpClient;
import at.beris.virtualfile.client.http.HttpClient;
import at.beris.virtualfile.client.https.HttpsClient;
import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.config.value.CharArrayConfigValue;
import at.beris.virtualfile.config.value.ConfigValue;
import at.beris.virtualfile.config.value.IntegerConfigValue;
import at.beris.virtualfile.config.value.StringConfigValue;
import at.beris.virtualfile.crypto.EncoderDecoder;
import at.beris.virtualfile.crypto.PasswordEncoderDecoder;
import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.provider.*;
import at.beris.virtualfile.util.CharUtils;
import at.beris.virtualfile.util.UrlUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configurator {
    private static final String SETTINGS_FILENAME = "settings.ini";

    private static final int MASTER_PASSWORD_LENGTH = 20;

    private Map<Protocol, Class> fileOperationProviderClassMap;
    private Map<Protocol, Class> clientClassMap;

    private UrlFileConfiguration defaultUrlFileConfiguration;
    private Map<Protocol, UrlFileConfiguration> configurationPerProtocolMap;
    private Map<URL, UrlFileConfiguration> configurationPerUrlMap;

    private Map<ConfigOption, ConfigValue> settings;

    private EncoderDecoder passwordEncoderDecoder;

    private ConfiguratorCallbackHandler callbackHandler;

    public Configurator() {
        callbackHandler = new EmptyCallBackHandler();
        passwordEncoderDecoder = new PasswordEncoderDecoder();

        fileOperationProviderClassMap = new HashMap<>();
        clientClassMap = new HashMap<>();
        configurationPerProtocolMap = new HashMap<>();
        configurationPerUrlMap = new HashMap<>();

        defaultUrlFileConfiguration = new UrlFileConfiguration();
        defaultUrlFileConfiguration.initValues();

        clientClassMap.put(Protocol.FILE, null);
        clientClassMap.put(Protocol.SFTP, SftpClient.class);
        clientClassMap.put(Protocol.FTP, FtpClient.class);
        clientClassMap.put(Protocol.HTTP, HttpClient.class);
        clientClassMap.put(Protocol.HTTPS, HttpsClient.class);

        fileOperationProviderClassMap.put(Protocol.FILE, LocalFileOperationProvider.class);
        fileOperationProviderClassMap.put(Protocol.SFTP, SftpClientFileOperationProvider.class);
        fileOperationProviderClassMap.put(Protocol.FTP, FtpClientFileOperationProvider.class);
        fileOperationProviderClassMap.put(Protocol.HTTP, HttpClientFileOperationProvider.class);
        fileOperationProviderClassMap.put(Protocol.HTTPS, HttpsClientFileOperationProvider.class);

        for (Protocol protocol : Protocol.values()) {
            configurationPerProtocolMap.put(protocol, new UrlFileConfiguration(defaultUrlFileConfiguration));
        }

        settings = new LinkedHashMap<>();
        initDefaultSettings();
        Path settingsPath = Paths.get(getHome(), SETTINGS_FILENAME);
        File settingsFile = settingsPath.toFile();

        if (settingsFile.exists()) {
            loadSettings(settingsPath);
        } else {
            setMasterPassword(CharUtils.generateCharSequence(MASTER_PASSWORD_LENGTH));
            saveSettings(settingsPath);
        }
    }

    public void setCallbackHandler(ConfiguratorCallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    public Class getFileOperationProviderClass(Protocol protocol) {
        return fileOperationProviderClassMap.get(protocol);
    }

    public Class getClientClass(Protocol protocol) {
        return clientClassMap.get(protocol);
    }

    void put(Protocol protocol, Class fileOperationProviderClass, Class clientClass) {
        fileOperationProviderClassMap.put(protocol, fileOperationProviderClass);
        clientClassMap.put(protocol, clientClass);
    }

    public UrlFileConfiguration createConfiguration(URL url) {
        Protocol protocol = UrlUtils.getProtocol(url);

        UrlFileConfiguration protocolConfig = configurationPerProtocolMap.get(protocol);
        if (protocolConfig == null) {
            protocolConfig = new UrlFileConfiguration(defaultUrlFileConfiguration);
            configurationPerProtocolMap.put(protocol, protocolConfig);
        }

        URL siteUrl = UrlUtils.newUrl(UrlUtils.getSiteUrlString(url.toString()));
        UrlFileConfiguration urlConfig = configurationPerUrlMap.get(siteUrl);
        if (urlConfig == null) {
            urlConfig = new UrlFileConfiguration(protocolConfig);
            configurationPerUrlMap.put(siteUrl, urlConfig);
        }

        return urlConfig;
    }

    @Deprecated
    public UrlFileConfiguration getConfiguration() {
        return defaultUrlFileConfiguration;
    }

    @Deprecated
    public UrlFileConfiguration getConfiguration(Protocol protocol) {
        return configurationPerProtocolMap.get(protocol);
    }

    public UrlFileConfiguration getConfiguration(VirtualFile file) {
        URL siteUrl = UrlUtils.newUrl(UrlUtils.getSiteUrlString(file.getUrl().toString()));
        return configurationPerUrlMap.get(siteUrl);
    }


    public String getHome() {
        return ((StringConfigValue) settings.get(ConfigOption.HOME)).getValue();

    }

    public Configurator setHome(String path) {
        settings.put(ConfigOption.HOME, new StringConfigValue(path));
        return this;
    }

    public int getFileCacheSize() {
        return ((IntegerConfigValue) settings.get(ConfigOption.FILE_CACHE_SIZE)).getValue();
    }

    public Configurator setFileCacheSize(int size) {
        settings.put(ConfigOption.FILE_CACHE_SIZE, new IntegerConfigValue(size));
        callbackHandler.changedFileCacheSize(size);
        return this;
    }

    public char[] getMasterPassword() {
        return passwordEncoderDecoder.decode(((CharArrayConfigValue) settings.get(ConfigOption.MASTER_PASSWORD)).getValue());
    }

    public Configurator setMasterPassword(char[] password) {
        settings.put(ConfigOption.MASTER_PASSWORD, new CharArrayConfigValue(passwordEncoderDecoder.encode(password)));
        return this;
    }

    private void initDefaultSettings() {
        settings.put(ConfigOption.FILE_CACHE_SIZE, new IntegerConfigValue(10000));
        settings.put(ConfigOption.HOME, new StringConfigValue(System.getProperty("user.home") + File.separator + ".VirtualFile"));
        settings.put(ConfigOption.MASTER_PASSWORD, new CharArrayConfigValue(passwordEncoderDecoder.encode(CharUtils.generateCharSequence(MASTER_PASSWORD_LENGTH))));
    }


    private void loadSettings(Path settingsPath) {
        try {
            //TODO To make the masterpassword more secure you shouldn't use strings but bytes
            for (String line : Files.readAllLines(settingsPath)) {
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

    private void saveSettings(Path settingsPath) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(settingsPath.toFile()));
            for (Map.Entry<ConfigOption, ConfigValue> entry : settings.entrySet()) {
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
        settings.put(configOption, configValue);
    }

    private ConfigValue createConfigValue(Class<?> clazz, String stringValue) {
        if (clazz.equals(Integer.class)) {
            return new IntegerConfigValue(Integer.valueOf(stringValue));
        } else if (clazz.equals(char[].class)) {
            return new CharArrayConfigValue(stringValue.toCharArray());
        } else if (clazz.equals(String.class)) {
            return new StringConfigValue(stringValue);
        } else {
            throw new VirtualFileException(Message.UNKNOWN_CONFIG_VALUE_CLASS());
        }
    }

    private class EmptyCallBackHandler implements ConfiguratorCallbackHandler {

        @Override
        public void changedFileCacheSize(int newSize) {

        }
    }
}
