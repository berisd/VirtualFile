/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.attribute.BasicFilePermission;
import at.beris.virtualfile.attribute.DosFileAttribute;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
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
    public void create(FileModel model) throws IOException {
        File file = null;
        try {
            file = new File(model.getUrl().toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        if (model.isDirectory()) {
            if (!file.mkdirs())
                throw new FileAlreadyExistsException(model.getUrl().toString());
        } else {
            if (!file.createNewFile())
                throw new FileAlreadyExistsException(model.getUrl().toString());
        }
    }

    @Override
    public Boolean exists(FileModel model) throws IOException {
        try {
            return new File(model.getUrl().toURI()).exists();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<VirtualFile> list(FileModel model, Filter filter) throws IOException {
        List<VirtualFile> fileList = new ArrayList<>();
        if (model.isDirectory()) {
            try {
                for (File childFile : new File(model.getUrl().toURI()).listFiles()) {
                    VirtualFile file = fileContext.newFile(childFile.toURI().toURL());
                    if (filter == null || filter.filter(file))
                        fileList.add(file);
                }
            } catch (URISyntaxException e) {
                throw new IOException(e);
            }
        }

        return fileList;
    }

    @Override
    public void delete(FileModel model) throws IOException {
        File file = null;
        try {
            file = new File(model.getUrl().toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        Files.walkFileTree(file.toPath(), new LocalFileDeletingVisitor());
    }

    @Override
    public Byte[] checksum(FileModel model) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File(model.getUrl().toURI()));) {
            MessageDigest md = MessageDigest.getInstance("SHA1");
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
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void updateModel(FileModel model) throws IOException {
        File file = null;
        try {
            file = new File(model.getUrl().toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        model.setFileExists(file.exists());

        if (!model.isFileExists())
            return;

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
    }

    @Override
    public InputStream getInputStream(FileModel model) throws IOException {
        try {
            return new FileInputStream(new File(model.getUrl().toURI()));
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public OutputStream getOutputStream(FileModel model) throws IOException {
        try {
            return new FileOutputStream(new File(model.getUrl().toURI()));
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void setAcl(FileModel model) throws IOException {
        File file = null;
        try {
            file = new File(model.getUrl().toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        FileStore fileStore = null;
        fileStore = Files.getFileStore(file.toPath());

        if (fileStore.supportsFileAttributeView(AclFileAttributeView.class)) {
            AclFileAttributeView attributeView = Files.getFileAttributeView(file.toPath(), AclFileAttributeView.class);
            attributeView.setAcl(model.getAcl());
        } else
            LOGGER.warn("ACL couldn't be set on file " + model.getUrl());

    }

    @Override
    public void setAttributes(FileModel model) throws IOException {
        File file = null;
        try {
            file = new File(model.getUrl().toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

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
    }

    @Override
    public void setCreationTime(FileModel model) throws IOException {
        setTimes(model.getUrl(), null, null, model.getCreationTime());
    }

    @Override
    public void setGroup(FileModel model) throws IOException {
        File file = null;
        try {
            file = new File(model.getUrl().toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        FileStore fileStore = Files.getFileStore(file.toPath());
        boolean posixFileAttributeViewSupported = fileStore.supportsFileAttributeView(PosixFileAttributeView.class);

        if (posixFileAttributeViewSupported) {
            PosixFileAttributeView attributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
            attributeView.setGroup(model.getGroup());
        }
    }

    @Override
    public void setLastAccessTime(FileModel model) throws IOException {
        setTimes(model.getUrl(), null, model.getLastAccessTime(), null);
    }

    @Override
    public void setLastModifiedTime(FileModel model) throws IOException {
        setTimes(model.getUrl(), model.getLastModifiedTime(), null, null);
    }

    @Override
    public void setOwner(FileModel model) throws IOException {
        File file = null;
        try {
            file = new File(model.getUrl().toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        FileStore fileStore = null;
        fileStore = Files.getFileStore(file.toPath());

        if (fileStore.supportsFileAttributeView(FileOwnerAttributeView.class)) {
            FileOwnerAttributeView fileOwnerAttributeView = Files.getFileAttributeView(file.toPath(), FileOwnerAttributeView.class);
            fileOwnerAttributeView.setOwner(model.getOwner());
        } else
            LOGGER.warn("Owner " + model.getOwner().getName() + " couldn't be set on file " + model.getUrl());
    }

    @Override
    public List<VirtualFile> extract(FileModel model, VirtualFile target) throws IOException {
        throw new OperationNotSupportedException();
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
    }

    private void fillBasicFileAttributes(FileModel model, File file) throws IOException {
        BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        model.setLastModifiedTime(basicFileAttributes.lastModifiedTime());
        model.setLastAccessTime(basicFileAttributes.lastAccessTime());
        model.setCreationTime(basicFileAttributes.creationTime());
        model.setSize(file.isDirectory() ? (file.list() != null ? file.list().length : 0) : basicFileAttributes.size());
        model.setDirectory(file.isDirectory());
        boolean isSymbolicLink = Files.isSymbolicLink(file.toPath());
        model.setSymbolicLink(isSymbolicLink);
        if (isSymbolicLink) {
            Path symbolicLink = Files.readSymbolicLink(file.toPath());
            model.setLinkTarget(symbolicLink.toUri().toURL());
        } else
            model.setLinkTarget(null);
    }

    private void fillDosFileAttributes(File file, FileModel model) throws IOException {
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

    private void fillDefaultFileAttributes(File file, FileModel model) {
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

    private void fillPosixFileAttributes(File file, FileModel model) throws IOException {
        Set<FileAttribute> attributes = model.getAttributes();

        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
        PosixFileAttributes posixFileAttributes = fileAttributeView.readAttributes();

        model.setOwner(posixFileAttributes.owner());
        model.setGroup(posixFileAttributes.group());

        for (java.nio.file.attribute.PosixFilePermission permission : posixFileAttributes.permissions()) {
            attributes.add(at.beris.virtualfile.attribute.PosixFilePermission.fromNioPermission(permission));
        }
    }

    private void setBasicFileAttributes(File file, Set<BasicFilePermission> basicFilePermissionSet) {
        file.setExecutable(basicFilePermissionSet.contains(BasicFilePermission.EXECUTE));
        file.setReadable(basicFilePermissionSet.contains(BasicFilePermission.READ));
        file.setWritable(basicFilePermissionSet.contains(BasicFilePermission.WRITE));
    }

    private void setDosFileAttributes(File file, Set<DosFileAttribute> dosAttributeSet) throws IOException {
        DosFileAttributeView dosFileAttributeView = Files.getFileAttributeView(file.toPath(), DosFileAttributeView.class);
        dosFileAttributeView.setArchive(dosAttributeSet.contains(DosFileAttribute.ARCHIVE));
        dosFileAttributeView.setHidden(dosAttributeSet.contains(DosFileAttribute.HIDDEN));
        dosFileAttributeView.setReadOnly(dosAttributeSet.contains(DosFileAttribute.READ_ONLY));
        dosFileAttributeView.setSystem(dosAttributeSet.contains(DosFileAttribute.SYSTEM));
    }

    private void setPosixFilePermissions(File file, Set<PosixFilePermission> posixFilePermissionSet) throws IOException {
        PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
        Set<java.nio.file.attribute.PosixFilePermission> newPermissions = new HashSet<>();

        for (PosixFilePermission permission : posixFilePermissionSet)
            newPermissions.add(permission.getNioPermission());

        posixFileAttributeView.setPermissions(newPermissions);
    }

    private void setTimes(URL url, FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException {
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        FileStore fileStore = Files.getFileStore(file.toPath());

        if (fileStore.supportsFileAttributeView(BasicFileAttributeView.class)) {
            BasicFileAttributeView attributeView = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
            attributeView.readAttributes();
            attributeView.setTimes(lastModifiedTime, lastAccessTime, createTime);
        } else {
            String timeType = lastModifiedTime != null ? "LastModifiedTime" :
                    (lastAccessTime != null ? "LastAccessTime" : "CreateTime");
            LOGGER.warn(timeType + " couldn't be set on file " + url.toString());
        }
    }
}
