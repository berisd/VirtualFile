/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.logging;

import at.beris.virtualfile.*;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.ObjectWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

public class FileLoggingWrapper implements ObjectWrapper<File>, File, Directory, Archive {

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
    public void addAttributes(FileAttribute... attributes) {
        logger.info("Add attributes {} to {}", FileUtils.getAttributesString(attributes), rootFile);
        wrappedFile.addAttributes(attributes);
    }

    @Override
    public Archive asArchive() {
        logger.info("Call asArchive() on {}", rootFile);
        return wrappedFile.asArchive();
    }

    @Override
    public Directory asDirectory() {
        logger.info("Call asDirectory() on {}", rootFile);
        return wrappedFile.asDirectory();
    }

    @Override
    public Byte[] checksum() {
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
    public void copy(File targetFile) {
        logger.info("Copy {} to {}", rootFile, targetFile);
        wrappedFile.copy(targetFile);
    }

    @Override
    public void copy(File targetFile, CopyListener listener) {
        logger.info("Copy {} to {} with Listener", rootFile, targetFile);
        wrappedFile.copy(targetFile, listener);
    }

    @Override
    public void create() {
        logger.info("Create {}", rootFile);
        wrappedFile.create();
    }

    @Override
    public void delete() {
        logger.info("Delete {}", rootFile);
        wrappedFile.delete();
    }

    @Override
    public Boolean exists() {
        logger.info("Check exists for {}", rootFile);
        Boolean exists = wrappedFile.exists();
        logger.info("Returns: {}", exists);
        return exists;
    }

    @Override
    public List<File> find(Filter filter) {
        logger.info("Find children for {} with filter {}", rootFile, filter);
        List<File> fileList = wrappedFile.find(filter);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public List<AclEntry> getAcl() {
        logger.debug("Get ACL for {}", rootFile);
        List<AclEntry> acl = wrappedFile.getAcl();
        logger.debug("Returns: {}", acl);
        return acl;
    }

    @Override
    public Set<FileAttribute> getAttributes() {
        logger.debug("Get attributes for {}", rootFile);
        Set<FileAttribute> attributes = wrappedFile.getAttributes();
        logger.debug("Returns: {}", FileUtils.getAttributesString(attributes.toArray(new FileAttribute[0])));
        return attributes;
    }

    @Override
    public FileTime getCreationTime() {
        logger.debug("Get creationTime for {}", rootFile);
        FileTime creationTime = wrappedFile.getCreationTime();
        logger.debug("Returns: {}", creationTime);
        return creationTime;
    }

    @Override
    public GroupPrincipal getGroup() {
        logger.debug("Get group for {}", rootFile);
        GroupPrincipal group = wrappedFile.getGroup();
        logger.debug("Returns: {}" + group);
        return group;
    }

    @Override
    public InputStream getInputStream() {
        logger.debug("Get Inputstream for {}", rootFile);
        return wrappedFile.getInputStream();
    }

    @Override
    public FileTime getLastAccessTime() {
        logger.debug("Get lastAccessTime for {}", rootFile);
        FileTime lastAccessTime = wrappedFile.getLastAccessTime();
        logger.debug("Returns: {}", lastAccessTime);
        return lastAccessTime;
    }

    @Override
    public FileTime getLastModifiedTime() {
        logger.debug("Get lastModifiedTime for {}", rootFile);
        FileTime lastModifiedTime = wrappedFile.getLastModifiedTime();
        logger.debug("Returns: {}", lastModifiedTime);
        return lastModifiedTime;
    }

    @Override
    public FileModel getModel() {
        logger.debug("Get model for {}", rootFile);
        FileModel model = wrappedFile.getModel();
        logger.debug("Returns: ", model);
        return model;
    }

    @Override
    public String getName() {
        logger.debug("Get name for {}", rootFile);
        String name = wrappedFile.getName();
        logger.debug("Returns: {}", name);
        return name;
    }

    @Override
    public OutputStream getOutputStream() {
        logger.debug("Get Outputstream for {}", rootFile);
        return wrappedFile.getOutputStream();
    }

    @Override
    public UserPrincipal getOwner() {
        logger.debug("Get owner for {}", rootFile);
        UserPrincipal owner = wrappedFile.getOwner();
        logger.debug("Returns: {}", owner);
        return owner;
    }

    @Override
    public File getParent() {
        logger.debug("Get parent for {}", rootFile);
        File parent = wrappedFile.getParent();
        logger.debug("Returns: {}", parent);
        return parent;
    }

    @Override
    public String getPath() {
        logger.debug("Get path for {}", rootFile);
        String path = wrappedFile.getPath();
        logger.debug("Returns: {}", path);
        return path;
    }

    @Override
    public File getRoot() {
        logger.debug("Get root for {}", rootFile);
        File root = wrappedFile.getRoot();
        logger.debug("Returns: {}", root);
        return root;
    }

    @Override
    public Site getSite() {
        logger.debug("Get site for {}", rootFile);
        Site site = wrappedFile.getSite();
        logger.debug("Returns: {}", site);
        return site;
    }

    @Override
    public long getSize() {
        logger.debug("Get size for {}", rootFile);
        long size = wrappedFile.getSize();
        logger.debug("Returns: {}", size);
        return size;
    }

    @Override
    public URL getUrl() {
        logger.debug("Get URL for {}", rootFile);
        URL url = wrappedFile.getUrl();
        logger.debug("Returns: {}", url);
        return url;
    }

    @Override
    public boolean isArchive() {
        logger.debug("Check isArchive for {}", rootFile);
        boolean isArchive = wrappedFile.isArchive();
        logger.debug("Returns: {}", isArchive);
        return isArchive;
    }

    @Override
    public boolean isArchived() {
        logger.debug("Check isArchived for {}", rootFile);
        boolean isArchived = wrappedFile.isArchived();
        logger.debug("Returns: {}", isArchived);
        return isArchived;
    }

    @Override
    public boolean isContainer() {
        logger.debug("Check isContainer for {}", rootFile);
        boolean isContainer = wrappedFile.isContainer();
        logger.debug("Returns: {}", isContainer);
        return isContainer;
    }

    @Override
    public boolean isDirectory() {
        logger.debug("Check isDirectory for {}", rootFile);
        boolean isDirectory = wrappedFile.isDirectory();
        logger.debug("Returns: {}", isDirectory);
        return isDirectory;
    }

    @Override
    public boolean isRoot() {
        logger.debug("Check isRoot for {}", rootFile);
        boolean isRoot = wrappedFile.isRoot();
        logger.debug("Returns: {}", isRoot);
        return isRoot;
    }

    @Override
    public boolean isSymbolicLink() {
        logger.debug("Check isSymbolicLink for {}", rootFile);
        boolean isSymbolicLink = wrappedFile.isSymbolicLink();
        logger.debug("Returns: {}", isSymbolicLink);
        return isSymbolicLink;
    }

    @Override
    public List<File> list() {
        logger.info("List children for {}", rootFile);
        List<File> fileList = wrappedFile.list();
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public List<File> list(Filter filter) {
        logger.info("List children for {} with filter {}", rootFile, filter);
        List<File> fileList = wrappedFile.list(filter);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public void refresh() {
        logger.info("Refresh {}", rootFile);
        wrappedFile.refresh();
    }

    @Override
    public void removeAttributes(FileAttribute... attributes) {
        logger.info("Remove attributes {} from {}", FileUtils.getAttributesString(attributes), rootFile);
        wrappedFile.removeAttributes(attributes);
    }

    @Override
    public void setAcl(List<AclEntry> acl) {
        logger.info("Set ACL to {} for {}", acl, rootFile);
        wrappedFile.setAcl(acl);
    }

    @Override
    public void setAttributes(FileAttribute... attributes) {
        logger.info("Set attributes for {}", rootFile);
        wrappedFile.setAttributes(attributes);
    }

    @Override
    public void setCreationTime(FileTime time) {
        logger.info("Set creationTime to {} for {}", time, rootFile);
        wrappedFile.setCreationTime(time);
    }

    @Override
    public void setGroup(GroupPrincipal group) {
        logger.info("Set group to {} for {}", group, rootFile);
        wrappedFile.setGroup(group);
    }

    @Override
    public void setLastAccessTime(FileTime time) {
        logger.info("Set lastAccessTime to {} for {}", time, rootFile);
        wrappedFile.setLastAccessTime(time);
    }

    @Override
    public void setLastModifiedTime(FileTime time) {
        logger.info("Set lastModifiedTime to {} for {}", time, rootFile);
        wrappedFile.setLastModifiedTime(time);
    }

    @Override
    public void setOwner(UserPrincipal owner) {
        logger.info("Set owner to {} for {}", owner, rootFile);
        wrappedFile.setOwner(owner);
    }

    @Override
    public void dispose() {
        logger.debug("Dispose {}", rootFile);
        wrappedFile.dispose();
    }

    @Override
    public List<File> extract(File target) {
        logger.info("Extract {} to {}", rootFile, target);
        return ((Archive) wrappedFile).extract(target);
    }

    @Override
    public void add(File file) {
        logger.info("Add {} to {}", rootFile, file);
        ((FileContainer) wrappedFile).add(file);
    }

    @Override
    public void delete(File file) {
        logger.info("Delete {} from {}", file, rootFile);
        ((FileContainer) wrappedFile).delete(file);
    }
}
