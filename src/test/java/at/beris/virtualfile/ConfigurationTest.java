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
import at.beris.virtualfile.client.sftp.AuthenticationType;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.CharUtils;
import at.beris.virtualfile.util.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static at.beris.virtualfile.TestHelper.TEST_HOME_DIRECTORY;

public class ConfigurationTest {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ConfigurationTest.class);

    public static final String TEST_CONFIGURATION_HOME = TEST_HOME_DIRECTORY + File.separator + "test123";
    public static final String TEST_CONFIGURATION_FILE = ConfigurationTest.class.getClassLoader().getResource("testconfiguration.xml").getPath();
    public static final char[] TEST_MASTER_PASSWORD = {'t', 'e', 's', 't'};

    @Test
    public void createDefaultConfiguration() {
        Configuration configuration = Configuration.create();
        Assert.assertEquals(Configuration.DEFAULT_FILE_CACHE_SIZE, configuration.getFileCacheSize());
        Assert.assertEquals(Configuration.DEFAULT_HOME_DIRECTORY, configuration.getHomeDirectory());
        Assert.assertFalse(CharUtils.isEmpty(configuration.getMasterPassword()));
    }

    @Test
    public void createCustomConfiguration() {
        Configuration configuration = Configuration.create(TEST_CONFIGURATION_HOME);
        int fileCacheSize = 8192;
        char[] masterPassword = {'a', 'b', 'c'};
        configuration.setFileCacheSize(fileCacheSize).setMasterPassword(masterPassword);
        Assert.assertEquals(TEST_CONFIGURATION_HOME, configuration.getHomeDirectory());
        Assert.assertEquals(fileCacheSize, configuration.getFileCacheSize());
        Assert.assertArrayEquals(masterPassword, configuration.getMasterPassword());
    }

    @Test
    public void setConfigurationValues() {
        Configuration configuration = Configuration.create();
        char[] password = TEST_MASTER_PASSWORD;
        String username = "tester";
        int timeout = 120;
        String privateKeyFile = "abcdef";
        String knownhosts = "knownhosts";
        boolean strictHostKeyChecking = true;
        AuthenticationType authenticationType = AuthenticationType.PUBLIC_KEY;

        configuration.setPassword(password);
        configuration.setUsername(username);
        configuration.setTimeout(timeout);
        configuration.setPrivateKeyFile(privateKeyFile);
        configuration.setAuthenticationType(authenticationType);
        configuration.setKnownHostsFile(knownhosts);
        configuration.setStrictHostKeyChecking(strictHostKeyChecking);

        for (ClientConfiguration clientConfiguration : new ClientConfiguration[]{configuration.getFtpClientConfiguration(),
                configuration.getSftpClientConfiguration(), configuration.getHttpClientConfiguration(),
                configuration.getHttpsClientConfiguration()}) {
            Assert.assertArrayEquals(password, clientConfiguration.getPassword());
            Assert.assertEquals(username, clientConfiguration.getUsername());
            Assert.assertEquals(timeout, clientConfiguration.getTimeout());
        }

        Assert.assertEquals(authenticationType, configuration.getSftpClientConfiguration().getAuthenticationType());
        Assert.assertEquals(knownhosts, configuration.getSftpClientConfiguration().getKnownHostsFile());
        Assert.assertEquals(privateKeyFile, configuration.getSftpClientConfiguration().getPrivateKeyFile());
    }

    @Test
    public void setConfigurationValuesPerProtocol() {
        Configuration configuration = Configuration.create();
        char[] password = TEST_MASTER_PASSWORD;
        String username = "tester";
        int timeout = 600;

        configuration.setTimeout(timeout, Protocol.SFTP);
        configuration.setPassword(password, Protocol.SFTP);
        configuration.setUsername(username, Protocol.SFTP);


        Assert.assertEquals(ClientConfiguration.DEFAULT_TIMEOUT, configuration.getFtpClientConfiguration().getTimeout());
        Assert.assertEquals(FtpClientConfiguration.DEFAULT_USERNAME_FTP, configuration.getFtpClientConfiguration().getUsername());
        Assert.assertEquals(ClientConfiguration.DEFAULT_PASSWORD, configuration.getFtpClientConfiguration().getPassword());

        Assert.assertEquals(timeout, configuration.getSftpClientConfiguration().getTimeout());
        Assert.assertEquals(username, configuration.getSftpClientConfiguration().getUsername());
        Assert.assertArrayEquals(password, configuration.getSftpClientConfiguration().getPassword());
    }

    @Test
    public void saveConfiguration() throws IOException {
        Configuration configuration = Configuration.create(TEST_CONFIGURATION_HOME);
        configuration.setMasterPassword(TEST_MASTER_PASSWORD);
        configuration.save();
        Assert.assertArrayEquals(Files.readAllBytes(Paths.get(TEST_CONFIGURATION_FILE)), Files.readAllBytes(configuration.getPath()));
    }

    @Test
    public void loadConfiguration() {
        Configuration configuration = Configuration.createFromXmlFile(new File(TEST_CONFIGURATION_FILE));
        Assert.assertEquals(10000, configuration.getFileCacheSize());
    }

    @AfterClass
    public static void afterTest() throws IOException {
        if (Paths.get(TEST_CONFIGURATION_HOME).toFile().exists())
            FileUtils.deleteDirectory(TEST_CONFIGURATION_HOME);
    }
}