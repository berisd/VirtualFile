/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.filter.IsDirectoryFilter;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.operation.CopyOperation;
import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.FileUtils;
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
import java.util.*;

import static at.beris.virtualfile.util.FileUtils.maskedUrlString;

public class UrlFile implements File, Comparable<UrlFile> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrlFile.class);

    private File parent;
    private FileModel model;
    private Map<FileType, FileOperationProvider> fileOperationProviderMap;
    private Map<FileOperationEnum, FileOperation> fileOperationMap;
    private Client client;
    private StringBuilder stringBuilder;

    public UrlFile(URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        this(null, url, model, fileOperationProviderMap, client, fileOperationMap);
    }

    public UrlFile(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        this(parent, url, model, fileOperationProviderMap, null, fileOperationMap);
    }

    public UrlFile(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        this.parent = parent;
        this.model = model;
        this.model.setUrl(url);
        this.fileOperationProviderMap = fileOperationProviderMap;
        this.fileOperationMap = fileOperationMap;
        this.client = client;
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public URL getUrl() {
        LOGGER.debug("Get URL for " + this);
        URL url = model.getUrl();
        LOGGER.debug("Returns: " + url);
        return url;
    }

    @Override
    public FileModel getModel() {
        LOGGER.debug("Get model for " + this);
        LOGGER.debug("Returns: " + model);
        return model;
    }

    @Override
    public Client getClient() {
        LOGGER.debug("Get client for " + this);
        LOGGER.debug("Returns: " + client);
        return client;
    }

    @Override
    public FileOperationProvider getFileOperationProvider() {
        LOGGER.debug("Get fileOperationProvider for " + this);
        FileOperationProvider fileOperationProvider = fileOperationProviderMap.get(model.requiredFileOperationProviderType());
        LOGGER.debug("Returns: " + fileOperationProvider);
        return fileOperationProvider;
    }

    @Override
    public String getName() {
        LOGGER.debug("Get name for " + this);
        String path = model.getPath();

        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf('/'));

        String name = path.substring(path.lastIndexOf('/') + 1);
        LOGGER.debug("Returns: " + name);

        return name;
    }

    @Override
    public FileTime getCreationTime() {
        LOGGER.debug("Get creationTime for " + this);
        FileTime creationTime = model.getCreationTime();
        LOGGER.debug("Returns: " + creationTime);
        return creationTime;
    }

    @Override
    public FileTime getLastModifiedTime() {
        LOGGER.debug("Get lastModifiedTime for " + this);
        FileTime lastModifiedTime = model.getLastModifiedTime();
        LOGGER.debug("Returns: " + lastModifiedTime);
        return lastModifiedTime;
    }

    @Override
    public FileTime getLastAccessTime() {
        LOGGER.debug("Get lastAccessTime for " + this);
        FileTime lastAccessTime = model.getLastAccessTime();
        LOGGER.debug("Returns: " + lastAccessTime);
        return lastAccessTime;
    }

    @Override
    public long getSize() {
        LOGGER.debug("Get size for " + this);
        LOGGER.debug("Returns: " + model.getSize());
        return model.getSize();
    }

    @Override
    public String getPath() {
        LOGGER.debug("Get path for " + this);
        LOGGER.debug("Returns: " + model.getPath());
        return model.getPath();
    }

    @Override
    public void delete() {
        LOGGER.info("Delete " + this);
        getFileOperationProvider().delete(client, model);
    }

    @Override
    public void dispose() {
        LOGGER.debug("Dispose " + this);
        model.clear();
        model = null;
        client = null;
        parent = null;
        fileOperationProviderMap = null;
    }

    @Override
    public byte[] checksum() {
        LOGGER.info("Calculate checksum for " + this);
        byte[] checksum = getFileOperationProvider().checksum(client, model);

        stringBuilder.setLength(0);
        stringBuilder.append("Returns: ");
        for(byte b : checksum)
            stringBuilder.append(String.format("%02x", b));
        LOGGER.info(stringBuilder.toString());
        return checksum;
    }

    @Override
    public boolean isDirectory() {
        LOGGER.debug("Check isDirectory for " + this);
        LOGGER.debug("Returns: " + model.isDirectory());
        return model.isDirectory();
    }

    @Override
    public boolean isSymbolicLink() {
        LOGGER.debug("Check isSymbolicLink for " + this);
        LOGGER.debug("Returns: " + model.isSymbolicLink());
        return model.isSymbolicLink();
    }

    @Override
    public boolean isContainer() {
        LOGGER.debug("Check isContainer for " + this);
        boolean isContainer = this instanceof FileContainer;
        LOGGER.debug("Returns: " + isContainer);
        return isContainer;
    }

    @Override
    public File getParent() {
        LOGGER.debug("Get parent for " + this);
        LOGGER.debug("Returns: " + parent);
        return parent;
    }

    @Override
    public File getRoot() {
        LOGGER.debug("Get root for " + this);
        File root = this;

        while (root.getParent() != null)
            root = root.getParent();

        LOGGER.debug("Returns: " + root);
        return root;
    }

    @Override
    public boolean isRoot() {
        LOGGER.debug("Check isRoot for " + this);
        boolean isRoot = this.toString().equals(getRoot() != null ? getRoot().toString() : "");
        LOGGER.debug("Returns: " + isRoot);
        return isRoot;
    }

    @Override
    public boolean exists() {
        LOGGER.debug("Check exists for " + this);
        boolean exists = getFileOperationProvider().exists(client, model);
        LOGGER.debug("Returns: " + exists);
        return exists;
    }

    @Override
    public void create() {
        LOGGER.info("Create " + this);
        getFileOperationProvider().create(this.client, this.model);
        _updateModel();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        LOGGER.debug("Get Inputstream for " + this);
        return getFileOperationProvider().getInputStream(client, model);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        LOGGER.debug("Get Outputstream for " + this);
        return getFileOperationProvider().getOutputStream(client, model);
    }

    @Override
    public List<AclEntry> getAcl() {
        LOGGER.debug("Get ACL for " + this);
        List<AclEntry> acl = model.getAcl();
        LOGGER.debug("Returns: " + acl);
        return acl;
    }

    @Override
    public void setAcl(List<AclEntry> acl) {
        LOGGER.debug("Set ACL to " + acl + " for " + this);
        model.setAcl(acl);
        getFileOperationProvider().setAcl(client, model);
        _updateModel();
    }

    @Override
    public UserPrincipal getOwner() {
        LOGGER.debug("Get owner for " + this);
        LOGGER.debug("Returns: " + model.getOwner());
        return model.getOwner();
    }

    @Override
    public void setOwner(UserPrincipal owner) {
        LOGGER.debug("Set owner to " + owner + " for " + this);
        model.setOwner(owner);
        getFileOperationProvider().setOwner(client, model);
        _updateModel();
    }

    @Override
    public GroupPrincipal getGroup() {
        LOGGER.debug("Get group for " + this);
        LOGGER.debug("Returns: " + model.getGroup());
        return model.getGroup();
    }

    @Override
    public void setGroup(GroupPrincipal group) {
        LOGGER.debug("Set group to " + group + " for " + this);
        model.setGroup(group);
        getFileOperationProvider().setGroup(client, model);
        _updateModel();
    }

    @Override
    public void setLastAccessTime(FileTime time) {
        LOGGER.debug("Set lastAccessTime to " + time + " for " + this);
        model.setLastAccessTime(time);
        getFileOperationProvider().setLastAccessTime(client, model);
        _updateModel();
    }

    @Override
    public void setLastModifiedTime(FileTime time) {
        LOGGER.debug("Set lastModifiedTime to " + time + " for " + this);
        model.setLastModifiedTime(time);
        getFileOperationProvider().setLastModifiedTime(client, model);
        _updateModel();
    }

    @Override
    public void setAttributes(FileAttribute... attributes) {
        LOGGER.debug("Set attributes for " + this);
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        getFileOperationProvider().setAttributes(client, model);
        _updateModel();
    }

    @Override
    public void setCreationTime(FileTime time) {
        LOGGER.debug("Set creationTime to " + time + " for " + this);
        model.setCreationTime(time);
        getFileOperationProvider().setCreationTime(client, model);
        _updateModel();
    }

    @Override
    public Set<FileAttribute> getAttributes() {
        LOGGER.debug("Get attributes for " + this);
        LOGGER.debug("Returns: " + model.getAttributes());
        return model.getAttributes();
    }

    @Override
    public void addAttributes(FileAttribute... attributes) {
        LOGGER.info("Add attributes " + getAttributesString(attributes) + " to " + this);

        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.addAttribute(attribute);

        _updateModel();
    }

    @Override
    public Archive asArchive() {
        return (Archive) this;
    }

    @Override
    public Directory asDirectory() {
        return (Directory) this;
    }

    @Override
    public void removeAttributes(FileAttribute... attributes) {
        LOGGER.info("Remove attributes " + getAttributesString(attributes) + " from " + this);
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.removeAttribute(attribute);

        _updateModel();
    }

    @Override
    public List<File> find(Filter filter) {
        LOGGER.info("Find children for " + this + " with filter " + filter);
        Filter directoriesFilter = new IsDirectoryFilter().equalTo(true);
        Filter withDirectoriesFilter = ((Filter) filter.clone()).or(new IsDirectoryFilter().equalTo(true));

        List<File> fileList = getFileOperationProvider().list(client, model, withDirectoriesFilter);
        Map<Filter, List<File>> partitionedFileList = FileUtils.groupFileListByFilters(fileList, Arrays.asList(filter, directoriesFilter));

        fileList.clear();
        fileList = partitionedFileList.get(filter);
        List<File> directoryList = partitionedFileList.get(directoriesFilter);

        for (File directory : directoryList) {
            fileList.addAll(directory.find(filter));
        }
        directoryList.clear();

        LOGGER.info("Returns: " + fileList.size() + " entries");

        return fileList;
    }

    @Override
    public List<File> list() {
        LOGGER.info("List children for " + this);
        List<File> fileList = getFileOperationProvider().list(client, model, null);
        LOGGER.info("Returns: " + fileList.size() + " entries");
        return fileList;
    }

    @Override
    public List<File> list(Filter filter) {
        LOGGER.info("List children for " + this + " with filter " + filter);
        List<File> fileList = getFileOperationProvider().list(client, model, filter);
        LOGGER.info("Returns: " + fileList.size() + " entries");
        return fileList;
    }

    @Override
    public boolean isArchive() {
        LOGGER.debug("Check isArchive for " + this);
        boolean isArchive = this instanceof Archive;
        LOGGER.debug("Returns: " + isArchive);
        return isArchive;
    }

    @Override
    public boolean isArchived() {
        LOGGER.debug("Check isArchived for " + this);
        return model.isArchived();
    }

    @Override
    public void copy(File targetFile) {
        LOGGER.info("Copy " + this + " to " + targetFile);
        CopyOperation copyOperation = (CopyOperation) fileOperationMap.get(FileOperationEnum.COPY);
        if (copyOperation == null)
            throw new OperationNotSupportedException();

        copyOperation.execute(this, targetFile);
    }

    @Override
    public void copy(File targetFile, CopyListener listener) {
        LOGGER.info("Copy " + this + " to " + targetFile + " with Listener");
        CopyOperation copyOperation = (CopyOperation) fileOperationMap.get(FileOperationEnum.COPY);
        if (copyOperation == null)
            throw new OperationNotSupportedException();

        copyOperation.execute(this, targetFile, listener);
    }

    @Override
    public void refresh() {
        LOGGER.info("Refresh " + this);
        _updateModel();
    }

    @Override
    public String toString() {
        return this.getClass().getInterfaces()[0].getSimpleName() + ((model != null && model.getUrl() != null) ? " " + maskedUrlString(model.getUrl()) :
                "@" + Integer.toHexString(System.identityHashCode(this)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlFile otherFile = (UrlFile) o;

        return compareTo(otherFile) == 0;
    }

    @Override
    public int hashCode() {
        return model.getUrl().toString().hashCode();
    }

    @Override
    public int compareTo(UrlFile o) {
        return model.getUrl().toString().compareTo(o.getUrl().toString());
    }

    void _updateModel() {
        LOGGER.debug("UpdateModel for " + this);
        getFileOperationProvider().updateModel(client, model);
    }

    private String getAttributesString(FileAttribute[] attributes) {
        String attributesString = "";
        if (attributes.length < 1)
            attributesString = "<none>";
        else {
            for (FileAttribute attribute : attributes)
                attributesString = (attributesString != "" ? ", " : "") + attribute.toString();
        }
        return attributesString;
    }
}
