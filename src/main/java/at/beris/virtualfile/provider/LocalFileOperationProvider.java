/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.attribute.BasicFilePermission;
import at.beris.virtualfile.attribute.DosFileAttribute;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.exception.PermissionDeniedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalFileOperationProvider extends AbstractFileOperationProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(LocalFileOperationProvider.class);

    public LocalFileOperationProvider(FileContext fileContext, Client client) {
        super(fileContext, client);
    }

    @Override
    public void create(FileModel model) {
        String pathName = model.getPath();
        java.io.File file = new java.io.File(pathName);
        if (model.isDirectory()) {
            if (!file.mkdirs())
                throw new at.beris.virtualfile.exception.FileAlreadyExistsException(pathName);
        } else {
            try {
                if (!file.createNewFile())
                    throw new at.beris.virtualfile.exception.FileAlreadyExistsException(pathName);
            } catch (IOException e) {
                if ("Permission denied".equals(e.getMessage()))
                    throw new PermissionDeniedException(e);
                else
                    throw new VirtualFileException(e);
            }
        }
    }

    @Override
    public Boolean exists(FileModel model) {
        return new java.io.File(model.getPath()).exists();
    }

    @Override
    public List<at.beris.virtualfile.File> list(FileModel model, Filter filter) {
        List<at.beris.virtualfile.File> fileList = new ArrayList<>();
        if (model.isDirectory()) {
            for (java.io.File childFile : new java.io.File(model.getPath()).listFiles()) {
                try {
                    at.beris.virtualfile.File file = FileManager.newFile(childFile.toURI().toURL());
                    if (filter == null || filter.filter(file))
                        fileList.add(file);
                } catch (MalformedURLException e) {
                    throw new VirtualFileException(e);
                }
            }
        }

        return fileList;
    }

    @Override
    public void delete(FileModel model) {
        java.io.File file = new java.io.File(model.getPath());
        try {
            if (file.exists()) {
                Files.walkFileTree(new java.io.File(file.getPath()).toPath(), new LocalFileDeletingVisitor());
            }
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public Byte[] checksum(FileModel model) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            FileInputStream fis = new FileInputStream(model.getPath());
            byte[] dataBytes = new byte[1024];

            int nread;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            byte[] digest = md.digest();
            Byte[] digestBytes = new Byte[digest.length];
            for (int i = 0; i < digest.length; i++)
                digestBytes[i] = digest[i];

            return digestBytes;
        } catch (NoSuchAlgorithmException e) {
            throw new VirtualFileException(e);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void updateModel(FileModel model) {
        try {
            java.io.File file = new java.io.File(model.getPath());
            model.setFileExists(file.exists());

            FileStore fileStore = Files.getFileStore(file.toPath());
            boolean basicFileAttributeViewSupported = fileStore.supportsFileAttributeView(BasicFileAttributeView.class);
            boolean fileOwnerAttributeViewSupported = fileStore.supportsFileAttributeView(FileOwnerAttributeView.class);
            boolean aclFileAttributeViewSupported = fileStore.supportsFileAttributeView(AclFileAttributeView.class);
            boolean posixFileAttributeViewSupported = fileStore.supportsFileAttributeView(PosixFileAttributeView.class);
            boolean dosFileAttributeViewSupported = fileStore.supportsFileAttributeView(DosFileAttributeView.class);

            if (basicFileAttributeViewSupported) {
                fillBasicFileAttributes(model, file);
            }

            if (fileOwnerAttributeViewSupported) {
                FileOwnerAttributeView fileAttributeView = Files.getFileAttributeView(file.toPath(), FileOwnerAttributeView.class);
                model.setOwner(fileAttributeView.getOwner());
            }

            if (aclFileAttributeViewSupported) {
                AclFileAttributeView attributeView = Files.getFileAttributeView(file.toPath(), AclFileAttributeView.class);
                model.setOwner(attributeView.getOwner());
                model.setAcl(attributeView.getAcl());
            }

            if (dosFileAttributeViewSupported) {
                fillDosFileAttributes(file, model);
            }

            if (posixFileAttributeViewSupported) {
                fillPosixFileAttributes(file, model);
            } else {
                fillDefaultFileAttributes(file, model);
            }
        } catch (IOException e) {
            if (e instanceof NoSuchFileException)
                throw new at.beris.virtualfile.exception.FileNotFoundException(e);
            else
                throw new VirtualFileException(e);
        }
    }

    @Override
    public InputStream getInputStream(FileModel model) {
        try {
            return new FileInputStream(model.getPath());
        } catch (FileNotFoundException e) {
            throw new at.beris.virtualfile.exception.FileNotFoundException(e);
        }
    }

    @Override
    public OutputStream getOutputStream(FileModel model) {
        try {
            return new FileOutputStream(model.getPath());
        } catch (FileNotFoundException e) {
            throw new at.beris.virtualfile.exception.FileNotFoundException(e);
        }
    }

    @Override
    public void setAcl(FileModel model) {
        java.io.File file = new java.io.File(model.getPath());
        FileStore fileStore = null;
        try {
            fileStore = Files.getFileStore(file.toPath());

            if (fileStore.supportsFileAttributeView(AclFileAttributeView.class)) {
                AclFileAttributeView attributeView = Files.getFileAttributeView(file.toPath(), AclFileAttributeView.class);
                attributeView.setAcl(model.getAcl());
            } else
                LOGGER.warn("ACL couldn't be set on file " + model.getUrl());

        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void setAttributes(FileModel model) {
        java.io.File file = new java.io.File(model.getPath());

        Set<BasicFilePermission> basicFilePermissionSet = new HashSet<>();
        Set<DosFileAttribute> dosAttributeSet = new HashSet<>();
        Set<PosixFilePermission> posixFilePermissionSet = new HashSet<>();

        for (FileAttribute attribute : model.getAttributes()) {
            if (attribute instanceof BasicFilePermission)
                basicFilePermissionSet.add((BasicFilePermission) attribute);
            else if (attribute instanceof DosFileAttribute)
                dosAttributeSet.add((DosFileAttribute) attribute);
            else if (attribute instanceof PosixFilePermission)
                posixFilePermissionSet.add((PosixFilePermission) attribute);
        }

        try {
            FileStore fileStore = Files.getFileStore(file.toPath());
            setBasicFileAttributes(file, basicFilePermissionSet);

            if (fileStore.supportsFileAttributeView(DosFileAttributeView.class)) {
                setDosFileAttributes(file, dosAttributeSet);
            } else if (dosAttributeSet.size() > 0)
                LOGGER.warn("DosAttributes specified but not supported by file " + model.getUrl());

            if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
                setPosixFilePermissions(file, posixFilePermissionSet);
            } else if (posixFilePermissionSet.size() > 0)
                LOGGER.warn("PosixFilePermissions specified but not supported by file " + model.getUrl());
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void setCreationTime(FileModel model) {
        setTimes(model.getPath(), null, null, model.getCreationTime());
    }

    @Override
    public void setGroup(FileModel model) {
        java.io.File file = new java.io.File(model.getPath());

        try {
            FileStore fileStore = Files.getFileStore(file.toPath());
            boolean posixFileAttributeViewSupported = fileStore.supportsFileAttributeView(PosixFileAttributeView.class);

            if (posixFileAttributeViewSupported) {
                PosixFileAttributeView attributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
                attributeView.setGroup(model.getGroup());
            }
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void setLastAccessTime(FileModel model) {
        setTimes(model.getPath(), null, model.getLastAccessTime(), null);
    }

    @Override
    public void setLastModifiedTime(FileModel model) {
        setTimes(model.getPath(), model.getLastModifiedTime(), null, null);
    }

    @Override
    public void setOwner(FileModel model) {
        java.io.File file = new java.io.File(model.getPath());
        FileStore fileStore = null;
        try {
            fileStore = Files.getFileStore(file.toPath());

            if (fileStore.supportsFileAttributeView(FileOwnerAttributeView.class)) {
                FileOwnerAttributeView fileOwnerAttributeView = Files.getFileAttributeView(file.toPath(), FileOwnerAttributeView.class);
                fileOwnerAttributeView.setOwner(model.getOwner());
            } else
                LOGGER.warn("Owner " + model.getOwner().getName() + " couldn't be set on file " + model.getUrl());

        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
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

    private void fillBasicFileAttributes(FileModel model, java.io.File file) throws IOException {
        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        model.setLastModifiedTime(basicFileAttributes.lastModifiedTime());
        model.setLastAccessTime(basicFileAttributes.lastAccessTime());
        model.setCreationTime(basicFileAttributes.creationTime());
        model.setSize(file.isDirectory() ? (file.list() != null ? file.list().length : 0) : basicFileAttributes.size());
        model.setDirectory(file.isDirectory());
        model.setSymbolicLink(basicFileAttributes.isSymbolicLink());
    }

    private void fillDosFileAttributes(java.io.File file, FileModel model) throws IOException {
        Set<FileAttribute> attributes = model.getAttributes();
        DosFileAttributes dosFileAttributes = Files.readAttributes(file.toPath(), DosFileAttributes.class);
        if (dosFileAttributes.isArchive())
            attributes.add(DosFileAttribute.ARCHIVE);
        if (dosFileAttributes.isHidden())
            attributes.add(DosFileAttribute.HIDDEN);
        if (dosFileAttributes.isReadOnly())
            attributes.add(DosFileAttribute.READ_ONLY);
        if (dosFileAttributes.isSystem())
            attributes.add(DosFileAttribute.SYSTEM);
    }

    private void fillDefaultFileAttributes(java.io.File file, FileModel model) {
        Set<FileAttribute> attributes = model.getAttributes();
        if (file.canRead()) {
            attributes.add(BasicFilePermission.READ);
        }
        if (file.canWrite()) {
            attributes.add(BasicFilePermission.WRITE);
        }
        if (file.canExecute()) {
            attributes.add(BasicFilePermission.EXECUTE);
        }
    }

    private void fillPosixFileAttributes(java.io.File file, FileModel model) throws IOException {
        Set<FileAttribute> attributes = model.getAttributes();

        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
        PosixFileAttributes posixFileAttributes = fileAttributeView.readAttributes();

        model.setOwner(posixFileAttributes.owner());
        model.setGroup(posixFileAttributes.group());

        for (java.nio.file.attribute.PosixFilePermission permission : posixFileAttributes.permissions()) {
            attributes.add(at.beris.virtualfile.attribute.PosixFilePermission.fromNioPermission(permission));
        }
    }

    private void setBasicFileAttributes(java.io.File file, Set<BasicFilePermission> basicFilePermissionSet) {
        file.setExecutable(basicFilePermissionSet.contains(BasicFilePermission.EXECUTE));
        file.setReadable(basicFilePermissionSet.contains(BasicFilePermission.READ));
        file.setWritable(basicFilePermissionSet.contains(BasicFilePermission.WRITE));
    }

    private void setDosFileAttributes(java.io.File file, Set<DosFileAttribute> dosAttributeSet) throws IOException {
        DosFileAttributeView dosFileAttributeView = Files.getFileAttributeView(file.toPath(), DosFileAttributeView.class);
        dosFileAttributeView.setArchive(dosAttributeSet.contains(DosFileAttribute.ARCHIVE));
        dosFileAttributeView.setHidden(dosAttributeSet.contains(DosFileAttribute.HIDDEN));
        dosFileAttributeView.setReadOnly(dosAttributeSet.contains(DosFileAttribute.READ_ONLY));
        dosFileAttributeView.setSystem(dosAttributeSet.contains(DosFileAttribute.SYSTEM));
    }

    private void setPosixFilePermissions(java.io.File file, Set<PosixFilePermission> posixFilePermissionSet) throws IOException {
        PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
        Set<java.nio.file.attribute.PosixFilePermission> newPermissions = new HashSet<>();

        for (PosixFilePermission permission : posixFilePermissionSet)
            newPermissions.add(permission.getNioPermission());

        posixFileAttributeView.setPermissions(newPermissions);
    }

    private void setTimes(String path, FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) {
        java.io.File file = new java.io.File(path);
        FileStore fileStore = null;
        try {
            fileStore = Files.getFileStore(file.toPath());

            if (fileStore.supportsFileAttributeView(BasicFileAttributeView.class)) {
                BasicFileAttributeView attributeView = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
                attributeView.readAttributes();
                attributeView.setTimes(lastModifiedTime, lastAccessTime, createTime);
            } else {
                String timeType = lastModifiedTime != null ? "LastModifiedTime" :
                        (lastAccessTime != null ? "LastAccessTime" : "CreateTime");
                LOGGER.warn(timeType + " couldn't be set on file " + path);
            }

        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }
}
