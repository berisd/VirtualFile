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
import java.util.UUID;

public class KeyStoreManager {

    private KeyStore keyStore;

    private File keyStoreFile;

    private char[] keyStorePassword;

    public KeyStoreManager(String parentPath, char[] password) {
        this.keyStorePassword = password;
        Path keyStorePath = Paths.get(parentPath, "keystore");

        keyStoreFile = keyStorePath.toFile();

        try {
            keyStore = KeyStore.getInstance("JCEKS");

            if (keyStoreFile.exists()) {
                try (FileInputStream fis = new FileInputStream(keyStoreFile)) {
                    keyStore.load(fis, password);
                }
            } else {
                keyStore.load(null, password);
            }


        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new VirtualFileException(e);
        }
    }

    public void save() {
        try (FileOutputStream fos = new FileOutputStream(keyStoreFile)) {
            keyStore.store(fos, keyStorePassword);
        } catch (FileNotFoundException e) {
            throw new VirtualFileException(Message.FILE_NOT_FOUND(e.getMessage()), e);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Saves a password to the keystore.
     * @param password
     * @return entry alias
     */
    public String addPassword(char[] password) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
            SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(password));

            String alias = UUID.randomUUID().toString();

            keyStore.setEntry(alias, new KeyStore.SecretKeyEntry(
                    generatedSecret), new KeyStore.PasswordProtection(keyStorePassword));
            return alias;
        } catch (NoSuchAlgorithmException | KeyStoreException | InvalidKeySpecException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Retrieve a password from the keystore.
     * @param alias Alias of the keyStoreEntry with the password
     * @return password
     */
    public char[] getPassword(String alias) {
        try {
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry)keyStore.getEntry(alias, new KeyStore.PasswordProtection(keyStorePassword));

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
            PBEKeySpec keySpec = (PBEKeySpec)factory.getKeySpec(
                    entry.getSecretKey(),
                    PBEKeySpec.class);

            return keySpec.getPassword();
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | InvalidKeySpecException e) {
            throw new VirtualFileException(e);
        }
    }
}
