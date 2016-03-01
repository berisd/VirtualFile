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
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.filter.IsDirectoryFilter;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.operation.CopyOperation;
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
    private Client client;

    public UrlFile(URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client) {
        this(null, url, model, fileOperationProviderMap, client);
    }

    public UrlFile(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap) {
        this(parent, url, model, fileOperationProviderMap, null);
    }

    public UrlFile(File parent, URL url, FileModel model, Map<FileType, FileOperationProvider> fileOperationProviderMap, Client client) {
        this.parent = parent;
        this.model = model;
        this.model.setUrl(url);
        this.fileOperationProviderMap = fileOperationProviderMap;
        this.client = client;
    }

    @Override
    public URL getUrl() {
//        LOGGER.info("Get URL for " + this);
        return model.getUrl();
    }

    @Override
    public FileModel getModel() {
//        LOGGER.info("Get model for " + this);
        return model;
    }

    @Override
    public Client getClient() {
        LOGGER.info("Get client for " + this);
        return client;
    }

    @Override
    public FileOperationProvider getFileOperationProvider() {
//        LOGGER.info("Get fileOperationProvider for " + this);
        return fileOperationProviderMap.get(model.requiredFileOperationProviderType());
    }

    @Override
    public String getName() {
        LOGGER.info("Get name for " + this);
        String path = model.getPath();

        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf('/'));

        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Override
    public FileTime getCreationTime() {
        LOGGER.info("Get creationTime for " + this);
        return model.getCreationTime();
    }

    @Override
    public FileTime getLastModifiedTime() {
        LOGGER.info("Get lastModifiedTime for " + this);
        return model.getLastModifiedTime();
    }

    @Override
    public FileTime getLastAccessTime() {
        LOGGER.info("Get lastAccessTime for " + this);
        return model.getLastAccessTime();
    }

    @Override
    public long getSize() {
        LOGGER.info("Get size for " + this);
        return model.getSize();
    }

    @Override
    public String getPath() {
        LOGGER.info("Get path for " + this);
        return model.getPath();
    }

    @Override
    public void delete() {
        LOGGER.info("Delete " + this);
        getFileOperationProvider().delete(client, model);
    }

    @Override
    public void dispose() {
        LOGGER.info("Dispose " + this);
        model.clear();
        model = null;
        client = null;
        parent = null;
        fileOperationProviderMap = null;
    }

    @Override
    public byte[] checksum() {
        LOGGER.info("Calculate checksum for " + this);
        return getFileOperationProvider().checksum(client, model);
    }

    @Override
    public boolean isDirectory() {
        LOGGER.info("Check isDirectory for " + this);
        return model.isDirectory();
    }

    @Override
    public boolean isSymbolicLink() {
        LOGGER.info("Check isSymbolicLink for " + this);
        return model.isSymbolicLink();
    }

    @Override
    public boolean isContainer() {
        LOGGER.info("Check isContainer for " + this);
        return this instanceof FileContainer;
    }

    @Override
    public File getParent() {
//        LOGGER.info("Get parent for " + this);
        return parent;
    }

    @Override
    public File getRoot() {
        LOGGER.info("Get root for " + this);
        File file = this;

        while (file.getParent() != null)
            file = file.getParent();

        return file;
    }

    @Override
    public boolean isRoot() {
        LOGGER.info("Check isRoot for " + this);
        return this.toString().equals(getRoot() != null ? getRoot().toString() : "");
    }

    @Override
    public boolean exists() {
        LOGGER.info("Check exists for " + this);
        return _exists();
    }

    @Override
    public void create() {
        LOGGER.info("Create " + this);
        getFileOperationProvider().create(this.client, this.model);
        _updateModel();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        LOGGER.info("Get Inputstream for " + this);
        return getFileOperationProvider().getInputStream(client, model);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        LOGGER.info("Get Outputstream for " + this);
        return getFileOperationProvider().getOutputStream(client, model);
    }

    @Override
    public List<AclEntry> getAcl() {
        LOGGER.info("Get ACL for " + this);
        return model.getAcl();
    }

    @Override
    public void setAcl(List<AclEntry> acl) {
        LOGGER.info("Set ACL for " + this);
        model.setAcl(acl);
        getFileOperationProvider().setAcl(client, model);
        _updateModel();
    }

    @Override
    public UserPrincipal getOwner() {
        LOGGER.info("Get owner for " + this);
        return model.getOwner();
    }

    @Override
    public void setOwner(UserPrincipal owner) {
        LOGGER.info("Set owner to " + owner + " for " + this);
        model.setOwner(owner);
        getFileOperationProvider().setOwner(client, model);
        _updateModel();
    }

    @Override
    public GroupPrincipal getGroup() {
        LOGGER.info("Get group for " + this);
        return model.getGroup();
    }

    @Override
    public void setGroup(GroupPrincipal group) {
        LOGGER.info("Set group to " + group + " for " + this);
        model.setGroup(group);
        getFileOperationProvider().setGroup(client, model);
        _updateModel();
    }

    @Override
    public void setLastAccessTime(FileTime time) {
        LOGGER.info("Set lastAccessTime to " + time + " for " + this);
        model.setLastAccessTime(time);
        getFileOperationProvider().setLastAccessTime(client, model);
        _updateModel();
    }

    @Override
    public void setLastModifiedTime(FileTime time) {
        LOGGER.info("Set lastModifiedTime to " + time + " for " + this);
        model.setLastModifiedTime(time);
        getFileOperationProvider().setLastModifiedTime(client, model);
        _updateModel();
    }

    @Override
    public void setAttributes(FileAttribute... attributes) {
        LOGGER.info("Set attributes for " + this);
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        getFileOperationProvider().setAttributes(client, model);
        _updateModel();
    }

    @Override
    public void setCreationTime(FileTime time) {
        LOGGER.info("Set creationTime to " + time + " for " + this);
        model.setCreationTime(time);
        getFileOperationProvider().setCreationTime(client, model);
        _updateModel();
    }

    @Override
    public Set<FileAttribute> getAttributes() {
        LOGGER.info("Get attributes for " + this);
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

        return fileList;
    }

    @Override
    public List<File> list() {
        LOGGER.info("List children for " + this);
        return getFileOperationProvider().list(client, model, null);
    }

    @Override
    public List<File> list(Filter filter) {
        LOGGER.info("List children for " + this + " with filter " + filter);
        return getFileOperationProvider().list(client, model, filter);
    }

    @Override
    public boolean isArchive() {
//        LOGGER.info("Check isArchive for " + this);
        return this instanceof Archive;
    }

    @Override
    public boolean isArchived() {
//        LOGGER.info("Check isArchived for " + this);
        return model.isArchived();
    }

    @Override
    public void copy(File targetFile) {
        LOGGER.info("Copy " + this + " to " + targetFile);
        new CopyOperation(this, targetFile, null);
    }

    @Override
    public void copy(File targetFile, CopyListener listener) {
        LOGGER.info("Copy " + this + " to " + targetFile);
        new CopyOperation(this, targetFile, listener);
    }

    @Override
    public void refresh() {
        LOGGER.info("Refresh " + this);
        _updateModel();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ((model != null && model.getUrl() != null) ? " " + maskedUrlString(model.getUrl()) :
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

    boolean _exists() {
        return getFileOperationProvider().exists(client, model);
    }

    void _updateModel() {
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
