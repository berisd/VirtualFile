/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;;

import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.FileModel;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LocalFileOperationProvider implements IFileOperationProvider {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(LocalFileOperationProvider.class);

    @Override
    public IFile create(IClient client, FileModel model) {
        String pathName = model.getPath();
        File file = new File(pathName);
        if (model.isDirectory()) {
            if (!file.mkdirs())
                throw new at.beris.virtualfile.exception.FileAlreadyExistsException(pathName);
        } else {
            try {
                if (!file.createNewFile())
                    throw new at.beris.virtualfile.exception.FileAlreadyExistsException(pathName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public boolean exists(IClient client, FileModel model) {
        return new File(model.getPath()).exists();
    }

    @Override
    public List<IFile> list(IClient client, FileModel model) {
        List<IFile> fileList = new ArrayList<>();
        if (model.isDirectory()) {
            for (File childFile : new File(model.getPath()).listFiles()) {
                try {
                    fileList.add(FileManager.newFile(childFile.toURI().toURL()));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return fileList;
    }

    @Override
    public void delete(IClient client, FileModel model) {
        File file = new File(model.getPath());
        try {
            if (file.exists()) {
                Files.walkFileTree(new File(file.getPath()).toPath(), new LocalFileDeletingVisitor());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void add(IFile parent, IFile child) {
        throw new NotImplementedException("");
    }

    @Override
    public byte[] checksum(IClient client, FileModel model) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            FileInputStream fis = new FileInputStream(model.getPath());
            byte[] dataBytes = new byte[1024];

            int nread;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateModel(IClient client, FileModel model) {
        File file = new File(model.getPath());

        model.setSize(file.length());
        model.setLastModified(new java.util.Date(file.lastModified()));
    }

    @Override
    public void save(URL url, FileModel model) {
        throw new NotImplementedException("");
    }

    @Override
    public InputStream getInputStream(IClient client, FileModel model) throws IOException {
        return new FileInputStream(model.getPath());
    }

    @Override
    public OutputStream getOutputStream(IClient client, FileModel model) throws IOException {
        return new FileOutputStream(model.getPath());
    }

    private class LocalFileDeletingVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                throws IOException {
            if (attributes.isRegularFile()) {
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path directory, IOException ioe)
                throws IOException {
            Files.delete(directory);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException ioe)
                throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
