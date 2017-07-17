/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.ClientConfiguration;
import at.beris.virtualfile.client.ftp.FtpClientConfiguration;
import at.beris.virtualfile.client.http.HttpClientConfiguration;
import at.beris.virtualfile.client.https.HttpsClientConfiguration;
import at.beris.virtualfile.client.sftp.AuthenticationType;
import at.beris.virtualfile.client.sftp.SftpClientConfiguration;
import at.beris.virtualfile.crypto.EncoderDecoder;
import at.beris.virtualfile.crypto.PasswordEncoderDecoder;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.CharUtils;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Configuration {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    public static final String CONFIGURATION_FILENAME = "configuration.xml";

    private static final int MASTER_PASSWORD_LENGTH = 10;

    public static final int DEFAULT_FILE_CACHE_SIZE = 10000;
    public static final String DEFAULT_HOME_DIRECTORY = System.getProperty("user.home") + File.separator + ".VirtualFile";

    @XmlTransient
    private String homeDirectory;

    private int fileCacheSize;

    private char[] masterPassword;

    private SftpClientConfiguration sftpClientConfiguration;

    private FtpClientConfiguration ftpClientConfiguration;

    private HttpClientConfiguration httpClientConfiguration;

    private HttpsClientConfiguration httpsClientConfiguration;

    @XmlTransient
    private Map<Protocol, ClientConfiguration> clientConfigurationByProtocol;

    @XmlTransient
    private CallbackHandler callbackHandler;

    @XmlTransient
    private EncoderDecoder passwordEncoderDecoder;

    private Configuration() {
        this(DEFAULT_HOME_DIRECTORY);
    }

    private Configuration(String homeDirectory) {
        this.homeDirectory = homeDirectory;
        callbackHandler = new EmptySettingsBackHandler();
        passwordEncoderDecoder = new PasswordEncoderDecoder();

        sftpClientConfiguration = ClientConfiguration.createSFtpConfiguration();
        ftpClientConfiguration = ClientConfiguration.createFtpConfiguration();
        httpClientConfiguration = ClientConfiguration.createHttpConfiguration();
        httpsClientConfiguration = ClientConfiguration.createHttpsConfiguration();

        clientConfigurationByProtocol = new HashMap<>();
        clientConfigurationByProtocol.put(Protocol.SFTP, sftpClientConfiguration);
        clientConfigurationByProtocol.put(Protocol.FTP, ftpClientConfiguration);
        clientConfigurationByProtocol.put(Protocol.HTTP, httpClientConfiguration);
        clientConfigurationByProtocol.put(Protocol.HTTPS, httpsClientConfiguration);

        initDefaultSettings();
        setMasterPassword(CharUtils.generateCharSequence(MASTER_PASSWORD_LENGTH));
    }

    public static Configuration create() {
        return create(DEFAULT_HOME_DIRECTORY);
    }

    public static Configuration create(String homeDirectory) {
        File configurationFile = Paths.get(homeDirectory, CONFIGURATION_FILENAME).toFile();
        if (configurationFile.exists()) {
            Configuration configuration = loadFromXmlFile(configurationFile);
            configuration.setHomeDirectory(homeDirectory);
            return configuration;
        } else {
            return new Configuration(homeDirectory);
        }
    }

    static Configuration loadFromXmlFile(File file) {
        LOGGER.info("Loading configuration from file '{}'", file.toString());
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(Configuration.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Configuration) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new VirtualFileException(e);
        }
    }

    private String setHomeDirectory(String homeDirectory) {
        return this.homeDirectory = homeDirectory;

    }

    public String getHomeDirectory() {
        return homeDirectory;

    }

    public Path getPath() {
        return Paths.get(homeDirectory, CONFIGURATION_FILENAME);
    }

    public int getFileCacheSize() {
        return fileCacheSize;
    }

    public Configuration setFileCacheSize(int size) {
        this.fileCacheSize = size;
        return this;
    }

    public char[] getMasterPassword() {
        return passwordEncoderDecoder.decode(masterPassword);
    }

    public Configuration setMasterPassword(char[] password) {
        masterPassword = passwordEncoderDecoder.encode(password);
        return this;
    }

    void setCallbackHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    private void initDefaultSettings() {
        fileCacheSize = DEFAULT_FILE_CACHE_SIZE;
    }

    public void save() {
        File configurationFile = Paths.get(homeDirectory, CONFIGURATION_FILENAME).toFile();
        LOGGER.info("Saving configuration to '{}'", configurationFile.toString());

        try {
            Files.createDirectories(Paths.get(homeDirectory));
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuration.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(this, configurationFile);
        } catch (JAXBException | IOException e) {
            throw new VirtualFileException(e);
        }
    }

    protected SftpClientConfiguration getSftpClientConfiguration() {
        return sftpClientConfiguration;
    }

    protected FtpClientConfiguration getFtpClientConfiguration() {
        return ftpClientConfiguration;
    }

    protected HttpClientConfiguration getHttpClientConfiguration() {
        return httpClientConfiguration;
    }

    protected HttpsClientConfiguration getHttpsClientConfiguration() {
        return httpsClientConfiguration;
    }

    public Configuration setStrictHostKeyChecking(boolean strictHostKeyChecking) {
        sftpClientConfiguration.setStrictHostKeyChecking(strictHostKeyChecking);
        return this;
    }

    public Configuration setKnownHostsFile(String knownHostsFile) {
        sftpClientConfiguration.setKnownHostsFile(knownHostsFile);
        return this;
    }

    public Configuration setAuthenticationType(AuthenticationType authenticationType) {
        sftpClientConfiguration.setAuthenticationType(authenticationType);
        return this;
    }

    public Configuration setPrivateKeyFile(String privateKeyFile) {
        sftpClientConfiguration.setPrivateKeyFile(privateKeyFile);
        return this;
    }

    public Configuration setTimeout(int timeout) {
        for (ClientConfiguration clientConfiguration : clientConfigurationByProtocol.values()) {
            clientConfiguration.setTimeout(timeout);
        }
        return this;
    }

    public Configuration setTimeout(int timeout, Protocol protocol) {
        ClientConfiguration clientConfiguration = clientConfigurationByProtocol.get(protocol);
        if (clientConfiguration != null) {
            clientConfiguration.setTimeout(timeout);
        }
        return this;
    }

    public Configuration setUsername(String username) {
        for (ClientConfiguration clientConfiguration : clientConfigurationByProtocol.values()) {
            clientConfiguration.setUsername(username);
        }
        return this;
    }

    public Configuration setUsername(String username, Protocol protocol) {
        ClientConfiguration clientConfiguration = clientConfigurationByProtocol.get(protocol);
        if (clientConfiguration != null) {
            clientConfiguration.setUsername(username);
        }
        return this;
    }

    public Configuration setPassword(char[] password) {
        for (ClientConfiguration clientConfiguration : clientConfigurationByProtocol.values()) {
            clientConfiguration.setPassword(password);
        }
        return this;
    }

    public Configuration setPassword(char[] password, Protocol protocol) {
        ClientConfiguration clientConfiguration = clientConfigurationByProtocol.get(protocol);
        if (clientConfiguration != null) {
            clientConfiguration.setPassword(password);
        }
        return this;
    }

    public ClientConfiguration getClientConfiguration(Protocol protocol) {
        return clientConfigurationByProtocol.get(protocol);
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
