/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.logging;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.ObjectWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

public class FileLoggingWrapper implements ObjectWrapper<File>, File {

    private File wrappedFile;
    private File rootFile;
    private StringBuilder stringBuilder;
    private Logger logger;

    public FileLoggingWrapper(File file) {
        this.wrappedFile = file;
        this.rootFile = file;

        while (rootFile instanceof ObjectWrapper) {
            ObjectWrapper wrapper = (ObjectWrapper) rootFile;
            rootFile = (File) wrapper.getWrappedObject();
        }

        this.stringBuilder = new StringBuilder();
        this.logger = LoggerFactory.getLogger(FileLoggingWrapper.class);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public File getWrappedObject() {
        return wrappedFile;
    }

    @Override
    public void addAttributes(FileAttribute... attributes) throws IOException {
        logger.info("Add attributes {} to {}", FileUtils.getAttributesString(attributes), rootFile);
        wrappedFile.addAttributes(attributes);
    }

    @Override
    public Byte[] checksum() throws IOException {
        logger.info("Calculate checksum for {}", rootFile);
        Byte[] checksum = wrappedFile.checksum();
        stringBuilder.setLength(0);
        stringBuilder.append("Returns: ");
        for (byte b : checksum)
            stringBuilder.append(String.format("%02x", b));
        logger.info(stringBuilder.toString());
        return checksum;
    }

    @Override
    public void copy(File targetFile) throws IOException {
        logger.info("Copy {} to {}", rootFile, targetFile);
        wrappedFile.copy(targetFile);
    }

    @Override
    public void copy(File targetFile, CopyListener listener) throws IOException {
        logger.info("Copy {} to {} with Listener", rootFile, targetFile);
        wrappedFile.copy(targetFile, listener);
    }

    @Override
    public void create() throws IOException {
        logger.info("Create {}", rootFile);
        wrappedFile.create();
    }

    @Override
    public void delete() throws IOException {
        logger.info("Delete {}", rootFile);
        wrappedFile.delete();
    }

    @Override
    public Boolean exists() throws IOException {
        logger.info("Check exists for {}", rootFile);
        Boolean exists = wrappedFile.exists();
        logger.info("Returns: {}", exists);
        return exists;
    }

    @Override
    public List<File> find(Filter filter) throws IOException {
        logger.info("Find children for {} with filter {}", rootFile, filter);
        List<File> fileList = wrappedFile.find(filter);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public List<AclEntry> getAcl() throws IOException {
        logger.debug("Get ACL for {}", rootFile);
        List<AclEntry> acl = wrappedFile.getAcl();
        logger.debug("Returns: {}", acl);
        return acl;
    }

    @Override
    public Set<FileAttribute> getAttributes() throws IOException {
        logger.debug("Get attributes for {}", rootFile);
        Set<FileAttribute> attributes = wrappedFile.getAttributes();
        logger.debug("Returns: {}", FileUtils.getAttributesString(attributes.toArray(new FileAttribute[0])));
        return attributes;
    }

    @Override
    public FileTime getCreationTime() throws IOException {
        logger.debug("Get creationTime for {}", rootFile);
        FileTime creationTime = wrappedFile.getCreationTime();
        logger.debug("Returns: {}", creationTime);
        return creationTime;
    }

    @Override
    public GroupPrincipal getGroup() throws IOException {
        logger.debug("Get group for {}", rootFile);
        GroupPrincipal group = wrappedFile.getGroup();
        logger.debug("Returns: {}" + group);
        return group;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        logger.debug("Get Inputstream for {}", rootFile);
        return wrappedFile.getInputStream();
    }

    @Override
    public FileTime getLastAccessTime() throws IOException {
        logger.debug("Get lastAccessTime for {}", rootFile);
        FileTime lastAccessTime = wrappedFile.getLastAccessTime();
        logger.debug("Returns: {}", lastAccessTime);
        return lastAccessTime;
    }

    @Override
    public FileTime getLastModifiedTime() throws IOException {
        logger.debug("Get lastModifiedTime for {}", rootFile);
        FileTime lastModifiedTime = wrappedFile.getLastModifiedTime();
        logger.debug("Returns: {}", lastModifiedTime);
        return lastModifiedTime;
    }

    @Override
    public FileModel getModel() throws IOException {
        logger.debug("Get model for {}", rootFile);
        FileModel model = wrappedFile.getModel();
        logger.debug("Returns: ", model);
        return model;
    }

    @Override
    public String getName() throws IOException {
        logger.debug("Get name for {}", rootFile);
        String name = wrappedFile.getName();
        logger.debug("Returns: {}", name);
        return name;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        logger.debug("Get Outputstream for {}", rootFile);
        return wrappedFile.getOutputStream();
    }

    @Override
    public UserPrincipal getOwner() throws IOException {
        logger.debug("Get owner for {}", rootFile);
        UserPrincipal owner = wrappedFile.getOwner();
        logger.debug("Returns: {}", owner);
        return owner;
    }

    @Override
    public File getParent() throws IOException {
        logger.debug("Get parent for {}", rootFile);
        File parent = wrappedFile.getParent();
        logger.debug("Returns: {}", parent);
        return parent;
    }

    @Override
    public String getPath() throws IOException {
        logger.debug("Get path for {}", rootFile);
        String path = wrappedFile.getPath();
        logger.debug("Returns: {}", path);
        return path;
    }

    @Override
    public File getRoot() throws IOException {
        logger.debug("Get root for {}", rootFile);
        File root = wrappedFile.getRoot();
        logger.debug("Returns: {}", root);
        return root;
    }

    @Override
    public long getSize() throws IOException {
        logger.debug("Get size for {}", rootFile);
        long size = wrappedFile.getSize();
        logger.debug("Returns: {}", size);
        return size;
    }

    @Override
    public URL getUrl() throws IOException {
        logger.debug("Get URL for {}", rootFile);
        URL url = wrappedFile.getUrl();
        logger.debug("Returns: {}", url);
        return url;
    }

    @Override
    public boolean isArchive() throws IOException {
        logger.debug("Check isArchive for {}", rootFile);
        boolean isArchive = wrappedFile.isArchive();
        logger.debug("Returns: {}", isArchive);
        return isArchive;
    }

    @Override
    public boolean isArchived() throws IOException {
        logger.debug("Check isArchived for {}", rootFile);
        boolean isArchived = wrappedFile.isArchived();
        logger.debug("Returns: {}", isArchived);
        return isArchived;
    }

    @Override
    public boolean isContainer() throws IOException {
        logger.debug("Check isContainer for {}", rootFile);
        boolean isContainer = wrappedFile.isContainer();
        logger.debug("Returns: {}", isContainer);
        return isContainer;
    }

    @Override
    public boolean isDirectory() throws IOException {
        logger.debug("Check isDirectory for {}", rootFile);
        boolean isDirectory = wrappedFile.isDirectory();
        logger.debug("Returns: {}", isDirectory);
        return isDirectory;
    }

    @Override
    public boolean isRoot() throws IOException {
        logger.debug("Check isRoot for {}", rootFile);
        boolean isRoot = wrappedFile.isRoot();
        logger.debug("Returns: {}", isRoot);
        return isRoot;
    }

    @Override
    public boolean isSymbolicLink() throws IOException {
        logger.debug("Check isSymbolicLink for {}", rootFile);
        boolean isSymbolicLink = wrappedFile.isSymbolicLink();
        logger.debug("Returns: {}", isSymbolicLink);
        return isSymbolicLink;
    }

    @Override
    public List<File> list() throws IOException {
        logger.info("List children for {}", rootFile);
        List<File> fileList = wrappedFile.list();
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public List<File> list(Filter filter) throws IOException {
        logger.info("List children for {} with filter {}", rootFile, filter);
        List<File> fileList = wrappedFile.list(filter);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public void refresh() throws IOException {
        logger.info("Refresh {}", rootFile);
        wrappedFile.refresh();
    }

    @Override
    public void removeAttributes(FileAttribute... attributes) throws IOException {
        logger.info("Remove attributes {} from {}", FileUtils.getAttributesString(attributes), rootFile);
        wrappedFile.removeAttributes(attributes);
    }

    @Override
    public void setAcl(List<AclEntry> acl) throws IOException {
        logger.info("Set ACL to {} for {}", acl, rootFile);
        wrappedFile.setAcl(acl);
    }

    @Override
    public void setAttributes(FileAttribute... attributes) throws IOException {
        logger.info("Set attributes for {}", rootFile);
        wrappedFile.setAttributes(attributes);
    }

    @Override
    public void setCreationTime(FileTime time) throws IOException {
        logger.info("Set creationTime to {} for {}", time, rootFile);
        wrappedFile.setCreationTime(time);
    }

    @Override
    public void setGroup(GroupPrincipal group) throws IOException {
        logger.info("Set group to {} for {}", group, rootFile);
        wrappedFile.setGroup(group);
    }

    @Override
    public void setLastAccessTime(FileTime time) throws IOException {
        logger.info("Set lastAccessTime to {} for {}", time, rootFile);
        wrappedFile.setLastAccessTime(time);
    }

    @Override
    public void setLastModifiedTime(FileTime time) throws IOException {
        logger.info("Set lastModifiedTime to {} for {}", time, rootFile);
        wrappedFile.setLastModifiedTime(time);
    }

    @Override
    public void setOwner(UserPrincipal owner) throws IOException {
        logger.info("Set owner to {} for {}", owner, rootFile);
        wrappedFile.setOwner(owner);
    }

    @Override
    public void dispose() throws IOException {
        logger.debug("Dispose {}", rootFile);
        wrappedFile.dispose();
    }

    @Override
    public List<File> extract(File target) throws IOException {
        logger.info("Extract {} to {}", rootFile, target);
        return wrappedFile.extract(target);
    }

    @Override
    public void add(File file) throws IOException {
        logger.info("Add {} to {}", rootFile, file);
        wrappedFile.add(file);
    }

    @Override
    public void delete(File file) throws IOException {
        logger.info("Delete {} from {}", file, rootFile);
        wrappedFile.delete(file);
    }
}
