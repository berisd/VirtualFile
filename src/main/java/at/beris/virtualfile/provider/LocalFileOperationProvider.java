/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.*;
import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.IFilter;
import at.beris.virtualfile.util.FileUtils;
import org.apache.commons.lang3.NotImplementedException;

import java.io.File;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class LocalFileOperationProvider implements IFileOperationProvider {

    private Map<PosixFilePermission, Attribute> posixFilePermissionToAttributeMap = new HashMap<>();

    public LocalFileOperationProvider() {
        posixFilePermissionToAttributeMap = createPosixFilePermissionToAttributeMap();
    }

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
                throw new VirtualFileException(e);
            }
        }
        return null;
    }

    @Override
    public boolean exists(IClient client, FileModel model) {
        return new File(model.getPath()).exists();
    }

    @Override
    public List<IFile> list(IClient client, FileModel model, Optional<IFilter> filter) {
        List<IFile> fileList = new ArrayList<>();
        if (model.isDirectory()) {
            for (File childFile : new File(model.getPath()).listFiles()) {
                try {
                    IFile file = FileManager.newFile(childFile.toURI().toURL());
                    if (!filter.isPresent() || filter.get().filter(file))
                        fileList.add(file);
                } catch (MalformedURLException e) {
                    throw new VirtualFileException(e);
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
            throw new VirtualFileException(e);
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
            throw new VirtualFileException(e);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void updateModel(IClient client, FileModel model) {
        File file = new File(model.getPath());
        Set<Attribute> attributes = model.getAttributes();

        if (model.getUrl().toString().endsWith("/") && (!file.isDirectory())) {
            String urlString = model.getUrl().toString();
            model.setUrl(FileUtils.newUrl(urlString.substring(0, urlString.length() - 1)));
        } else if (!model.getUrl().toString().endsWith("/") && (file.isDirectory())) {
            String urlString = model.getUrl().toString() + "/";
            model.setUrl(FileUtils.newUrl(urlString));
        }

        try {
            FileStore fileStore = Files.getFileStore(file.toPath());
            boolean basicFileAttributeViewSupported = fileStore.supportsFileAttributeView(BasicFileAttributeView.class);
            boolean posixFileAttributeViewSupported = fileStore.supportsFileAttributeView(PosixFileAttributeView.class);
            boolean dosFileAttributeViewSupported = fileStore.supportsFileAttributeView(DosFileAttributeView.class);

            if (basicFileAttributeViewSupported) {
                fillBasicFileAttributes(model, file);
            }

            if (dosFileAttributeViewSupported) {
                fillDosFileAttributes(file, attributes);
            }

            if (posixFileAttributeViewSupported) {
                fillPosixFileAttributes(file, attributes);
            } else {
                fillDefaultFileAttributes(file, attributes);
            }
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
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

    @Override
    public Set<Attribute> getAttributes(IClient client, FileModel model) {
        return model.getAttributes();
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

    private Map<PosixFilePermission, Attribute> createPosixFilePermissionToAttributeMap() {
        Map<PosixFilePermission, Attribute> map = new HashMap<>();
        map.put(PosixFilePermission.OWNER_READ, Attribute.OWNER_READ);
        map.put(PosixFilePermission.OWNER_WRITE, Attribute.OWNER_WRITE);
        map.put(PosixFilePermission.OWNER_EXECUTE, Attribute.OWNER_EXECUTE);
        map.put(PosixFilePermission.GROUP_READ, Attribute.GROUP_READ);
        map.put(PosixFilePermission.GROUP_WRITE, Attribute.GROUP_WRITE);
        map.put(PosixFilePermission.GROUP_EXECUTE, Attribute.GROUP_EXECUTE);
        map.put(PosixFilePermission.OTHERS_READ, Attribute.OTHERS_READ);
        map.put(PosixFilePermission.OTHERS_WRITE, Attribute.OTHERS_WRITE);
        map.put(PosixFilePermission.OTHERS_EXECUTE, Attribute.OTHERS_EXECUTE);
        return map;
    }

    private void fillBasicFileAttributes(FileModel model, File file) throws IOException {
        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        model.setLastModifiedTime(new Date(basicFileAttributes.lastModifiedTime().toMillis()));
        model.setLastAccessTime(new Date(basicFileAttributes.lastAccessTime().toMillis()));
        model.setCreationTime(new Date(basicFileAttributes.creationTime().toMillis()));
        model.setSize(file.isDirectory() ? file.list().length : basicFileAttributes.size());
    }

    private void fillDosFileAttributes(File file, Set<Attribute> attributes) throws IOException {
        DosFileAttributes dosFileAttributes = Files.readAttributes(file.toPath(), DosFileAttributes.class);
        if (dosFileAttributes.isArchive())
            attributes.add(Attribute.ARCHIVE);
        if (dosFileAttributes.isHidden())
            attributes.add(Attribute.HIDDEN);
        if (dosFileAttributes.isReadOnly())
            attributes.add(Attribute.READ_ONLY);
        if (dosFileAttributes.isSystem())
            attributes.add(Attribute.SYSTEM);
    }

    private void fillDefaultFileAttributes(File file, Set<Attribute> attributes) {
        if (file.canRead()) {
            attributes.add(Attribute.READ);
        }
        if (file.canWrite()) {
            attributes.add(Attribute.WRITE);
        }
        if (file.canExecute()) {
            attributes.add(Attribute.EXECUTE);
        }
    }

    private void fillPosixFileAttributes(File file, Set<Attribute> attributes) throws IOException {
        PosixFileAttributes posixFileAttributes = Files.readAttributes(file.toPath(), PosixFileAttributes.class);
        Set<PosixFilePermission> permissions = posixFileAttributes.permissions();

        for (PosixFilePermission permission : permissions) {
            attributes.add(posixFilePermissionToAttributeMap.get(permission));
        }
    }
}
