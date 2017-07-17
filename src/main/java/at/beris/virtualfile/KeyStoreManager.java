/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.VirtualFileException;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class KeyStoreManager {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SiteManager.class);

    private KeyStore keyStore;

    private Path keyStorePath;

    private char[] keyStorePassword;

    private Map<String, KeyStore.Entry> entryMap;

    private KeyStore.ProtectionParameter protectionParameter;

    private KeyStoreManager(Configuration configuration) {
        try {
            keyStore = KeyStore.getInstance("JCEKS");
        } catch (KeyStoreException e) {
            throw new VirtualFileException(e);
        }

        keyStorePath = Paths.get(configuration.getHomeDirectory(), "keystore");
        keyStorePassword = configuration.getMasterPassword();
        entryMap = new LinkedHashMap<>();
        protectionParameter = new KeyStore.PasswordProtection(keyStorePassword);
    }

    public static KeyStoreManager create(Configuration configuration) {
        return new KeyStoreManager(configuration);
    }

    /**
     * Load data from keystore.
     */
    public void load() {
        File keyStoreFile = keyStorePath.toFile();
        if (!keyStoreFile.exists())
            return;

        LOGGER.info("Loading keystore data from '{}'", keyStoreFile.toString());

        try (FileInputStream fis = new FileInputStream(keyStoreFile)) {
            keyStore.load(fis, keyStorePassword);
            entryMap.clear();
            Enumeration<String> aliasesEnumeration = keyStore.aliases();
            while (aliasesEnumeration.hasMoreElements()) {
                String alias = aliasesEnumeration.nextElement();
                entryMap.put(alias, keyStore.getEntry(alias, protectionParameter));
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableEntryException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Save data to keystore.
     */
    public void save() {
        File keyStoreFile = keyStorePath.toFile();
        LOGGER.info("Saving keystore data to '{}'", keyStoreFile.toString());

        if (keyStoreFile.exists())
            keyStoreFile.delete();

        try (FileOutputStream fos = new FileOutputStream(keyStoreFile)) {
            keyStore.load(null, keyStorePassword);

            for (Map.Entry<String, KeyStore.Entry> mapEntry : entryMap.entrySet()) {
                String alias = mapEntry.getKey();
                KeyStore.Entry keyStoreEntry = mapEntry.getValue();
                keyStore.setEntry(alias, keyStoreEntry, protectionParameter);
            }

            keyStore.store(fos, keyStorePassword);
        } catch (FileNotFoundException e) {
            throw new VirtualFileException(Message.FILE_NOT_FOUND(e.getMessage()), e);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Saves a password to the keystore.
     *
     * @param password
     * @return entry alias
     */
    public String addPassword(char[] password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
            SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(password));

            String alias = UUID.randomUUID().toString();
            entryMap.put(alias, new KeyStore.SecretKeyEntry(generatedSecret));

            return alias;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Retrieve a password from the keystore.
     *
     * @param alias Alias of the keyStoreEntry with the password
     * @return password
     */
    public char[] getPassword(String alias) {
        try {
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) entryMap.get(alias);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
            PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(
                    entry.getSecretKey(),
                    PBEKeySpec.class);

            return keySpec.getPassword();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Remove a password from the keystore.
     *
     * @param alias
     */
    public void removePassword(String alias) {
        entryMap.remove(alias);
    }
}
